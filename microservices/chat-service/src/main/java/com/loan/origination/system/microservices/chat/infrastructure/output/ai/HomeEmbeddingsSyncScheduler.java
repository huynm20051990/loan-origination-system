package com.loan.origination.system.microservices.chat.infrastructure.output.ai;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HomeEmbeddingsSyncScheduler {

  private static final Logger LOG = LoggerFactory.getLogger(HomeEmbeddingsSyncScheduler.class);
  private static final String HOMES_ENDPOINT = "/api/v1/homes";

  private final RestClient restClient;
  private final VectorStore vectorStore;

  public HomeEmbeddingsSyncScheduler(
      RestClient.Builder restClientBuilder,
      VectorStore vectorStore,
      @Value("${home-service.base-url}") String homeServiceBaseUrl) {
    this.restClient = restClientBuilder.baseUrl(homeServiceBaseUrl).build();
    this.vectorStore = vectorStore;
  }

  @Scheduled(fixedRateString = "PT15M")
  public void syncEmbeddings() {
    LOG.info("Starting home embeddings sync from home-service");

    List<Map<String, Object>> listings = restClient.get()
        .uri(HOMES_ENDPOINT)
        .retrieve()
        .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

    if (listings == null || listings.isEmpty()) {
      LOG.info("No listings returned from home-service; skipping sync");
      return;
    }

    List<Document> documents = listings.stream()
        .map(this::toDocument)
        .toList();

    vectorStore.add(documents);
    LOG.info("Synced {} home listing embeddings into chat_home_embeddings", documents.size());
  }

  private Document toDocument(Map<String, Object> listing) {
    String text = buildText(listing);
    return Document.builder()
        .text(text)
        .metadata(listing)
        .build();
  }

  private String buildText(Map<String, Object> listing) {
    StringBuilder sb = new StringBuilder();
    if (listing.containsKey("address")) sb.append("Address: ").append(listing.get("address")).append(". ");
    if (listing.containsKey("price")) sb.append("Price: $").append(listing.get("price")).append(". ");
    if (listing.containsKey("bedrooms")) sb.append("Bedrooms: ").append(listing.get("bedrooms")).append(". ");
    if (listing.containsKey("bathrooms")) sb.append("Bathrooms: ").append(listing.get("bathrooms")).append(". ");
    if (listing.containsKey("sqft")) sb.append("Sqft: ").append(listing.get("sqft")).append(". ");
    if (listing.containsKey("description")) sb.append(listing.get("description"));
    return sb.toString().trim();
  }
}
