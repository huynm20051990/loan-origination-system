package com.loan.origination.system.microservices.home.infrastructure.output.persistence;

import com.loan.origination.system.microservices.home.application.port.output.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.domain.model.FilterItem;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.model.SearchIntent;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.entity.HomeEntity;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.repository.HomeRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HomePersistenceAdapter implements HomeRepositoryPort {

  private static final Logger LOG = LoggerFactory.getLogger(HomePersistenceAdapter.class);

  private final HomeRepository homeRepository;
  private final HomePersistenceMapper mapper;
  private final VectorStore vectorStore;
  private final ChatClient chatClient;

  public HomePersistenceAdapter(
      HomeRepository homeRepository,
      HomePersistenceMapper mapper,
      VectorStore vectorStore,
      ChatClient chatClient) {
    this.homeRepository = homeRepository;
    this.mapper = mapper;
    this.vectorStore = vectorStore;
    this.chatClient = chatClient;
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

    Document document = new Document(home.getId().toString(), searchContent, metadata);
    vectorStore.add(List.of(document));
  }

  @Override
  public List<UUID> search(String query) {

    // STEP 1: AI Extracting intent (Vibe + Structured Filters)
    // When building your prompt
    String systemPrompt =
        """
        You are a real estate assistant. Extract 'vibe' and 'filters'.
        VALID FILTER COLUMNS: %s

        Instructions:
        1. If a detail matches a VALID FILTER COLUMN, create a FilterItem.
        2. Everything else (style, materials, feelings) MUST go into the 'vibe'.
        3. If 'vibe' would be empty, set it to 'neutral'.
        """
            .formatted(getValidColumns());

    SearchIntent intent;
    try {
      // Attempt AI Extraction
      LOG.info("Start chatting using query: " + query);
      intent =
          chatClient.prompt().system(systemPrompt).user(query).call().entity(SearchIntent.class);
    } catch (Exception e) {
      // FALLBACK: If AI fails (Quota, Timeout, etc.), use raw query
      // This ensures the user still gets results!
      intent = new SearchIntent(query, List.of());
    }
    LOG.info(intent.toString());

    // STEP 2: Build the Metadata Filters
    FilterExpressionBuilder b = new FilterExpressionBuilder();
    Filter.Expression finalExpression = null;

    if (intent.filters() != null && !intent.filters().isEmpty()) {
      for (FilterItem item : intent.filters()) {
        Filter.Expression current = mapToExpression(b, item);
        if (current != null) {
          if (finalExpression == null) {
            finalExpression = current;
          } else {
            // We join them using the builder and immediately build
            finalExpression =
                new Filter.Expression(Filter.ExpressionType.AND, finalExpression, current);
          }
        }
      }
    }

    String searchPath =
        (intent.vibe() == null || intent.vibe().equalsIgnoreCase("neutral"))
            ? query // Use original query if vibe is empty
            : intent.vibe();

    // 3. Configure and Execute Request
    var request =
        SearchRequest.builder()
            .query(searchPath)
            .topK(5)
            .similarityThreshold(0.5)
            .filterExpression(finalExpression)
            .build();
    LOG.info(request.toString());

    // STEP 4: Execute using the 'request' object
    return vectorStore.similaritySearch(request).stream()
        .map(doc -> UUID.fromString(doc.getMetadata().get("homeId").toString()))
        .toList();
  }

  private String getValidColumns() {
    // Explicitly listing columns to ensure the AI knows about nested address fields
    return "street, city, state, country, price, beds, baths, sqft, status";
  }

  private Filter.Expression mapToExpression(FilterExpressionBuilder b, FilterItem f) {
    if (f.column() == null || f.operator() == null || f.value() == null) return null;

    String col = f.column().toLowerCase();
    Object val = f.value();

    // 1. Identify Numeric Columns
    List<String> numericColumns = List.of("price", "beds", "baths", "sqft");

    if (numericColumns.contains(col)) {
      if (val instanceof String s) {
        // Remove '$', 'k', and commas, then convert to Double
        try {
          val = Double.parseDouble(s.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
          LOG.error("Could not parse numeric filter for {}: {}", col, s);
          return null; // Skip invalid numeric filters
        }
      }
    } else if (val instanceof String s) {
      // 2. Handle Text Columns (city, state, etc.)
      val = s.toLowerCase().trim();
    }

    return switch (f.operator().toUpperCase()) {
      case "GT", ">" -> b.gt(f.column(), val).build();
      case "LT", "<" -> b.lt(f.column(), val).build();
      case "GTE", ">=" -> b.gte(f.column(), val).build();
      case "LTE", "<=" -> b.lte(f.column(), val).build();
      default -> b.eq(f.column(), val).build();
    };
  }
}
