package com.loan.origination.system.microservices.home.infrastructure.output.persistence;

import com.loan.origination.system.microservices.home.application.port.output.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.entity.HomeEntity;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.repository.HomeRepository;
import com.loan.origination.system.microservices.home.infrastructure.tools.HomeSearchTools;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HomePersistenceAdapter implements HomeRepositoryPort {

  private static final Logger LOG = LoggerFactory.getLogger(HomePersistenceAdapter.class);

  private final HomeRepository homeRepository;
  private final HomePersistenceMapper mapper;
  private final VectorStore vectorStore;
  private final ChatClient chatClient;

  @Value("classpath:/prompts/search-properties.st")
  private Resource searchResource;

  public HomePersistenceAdapter(
      HomeRepository homeRepository,
      HomePersistenceMapper mapper,
      VectorStore vectorStore,
      ChatClient.Builder builder,
      HomeSearchTools homeSearchTools) {
    this.homeRepository = homeRepository;
    this.mapper = mapper;
    this.vectorStore = vectorStore;
    this.chatClient =
        builder
            .defaultTools(homeSearchTools)
            .defaultAdvisors(ToolCallAdvisor.builder().build())
            .build();
  }

  @Override
  @Transactional
  public Home save(Home home) {
    HomeEntity entity = mapper.toEntity(home);
    HomeEntity saved = homeRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Home> findById(UUID id) {
    return homeRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Home> findAll() {
    return homeRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    homeRepository.deleteById(id);
  }

  @Override
  @Transactional // Ensure atomicity
  public void indexHome(Home home) {
    vectorStore.add(List.of(mapToDocument(home)));
  }

  @Override
  public void indexHomes(List<Home> homes) {
    if (homes.isEmpty()) return;
    List<Document> documents = homes.stream().map(this::mapToDocument).toList();
    vectorStore.add(documents);
  }

  private Document mapToDocument(Home home) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("homeId", home.getId().toString());

    // Address Fields (Normalized to lowercase for case-insensitivity)
    metadata.put("street", home.getAddress().street().toLowerCase());
    metadata.put("city", home.getAddress().city().toLowerCase());
    metadata.put("state", home.getAddress().state().toLowerCase());
    metadata.put("country", home.getAddress().country().toLowerCase());

    // Numeric Fields (Stored as Numbers to prevent SQL Grammar/&& errors)
    metadata.put("price", home.getPrice().doubleValue());
    metadata.put("beds", home.getBeds());
    metadata.put("baths", home.getBaths());
    metadata.put("sqft", home.getSqft());
    metadata.put("status", home.getStatus().name().toLowerCase());

    // Updated descriptive string for semantic search
    String searchContent =
        String.format(
            "Home at %s, %s, %s, %s. Price: %s. %d beds and %.1f baths. %s",
            home.getAddress().street(),
            home.getAddress().city(),
            home.getAddress().state(),
            home.getAddress().country(),
            home.getPrice(),
            home.getBeds(),
            home.getBaths(),
            home.getDescription() == null ? "" : home.getDescription());

    return new Document(home.getId().toString(), searchContent, metadata);
  }

  @Override
  public List<UUID> search(String userQuery) {

    try {
      LOG.info("Starting agentic search for query: {}", userQuery);
      return chatClient
          .prompt()
          // Instructions go in SYSTEM (Spring AI handles the Resource reading and params)
          .system(s -> s.text(searchResource).param("userQuery", userQuery))
          // The actual input goes in USER
          .user(userQuery)
          .call()
          .entity(new ParameterizedTypeReference<List<UUID>>() {});
    } catch (RuntimeException e) {
      LOG.error("Agentic search failed, falling back to basic similarity search", e);
      return vectorStore
          .similaritySearch(SearchRequest.builder().query(userQuery).topK(5).build())
          .stream()
          .map(doc -> UUID.fromString(doc.getMetadata().get("homeId").toString()))
          .toList();
    }
  }
}
