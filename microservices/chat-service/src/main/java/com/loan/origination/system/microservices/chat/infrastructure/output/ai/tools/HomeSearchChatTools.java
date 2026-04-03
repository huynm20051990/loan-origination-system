package com.loan.origination.system.microservices.chat.infrastructure.output.ai.tools;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class HomeSearchChatTools {

  private static final Logger LOG = LoggerFactory.getLogger(HomeSearchChatTools.class);

  private final VectorStore vectorStore;

  public HomeSearchChatTools(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Tool(description = "Search home listings using a natural language query about price, bedrooms, location, or other property features. Returns matching home summaries.")
  public List<Map<String, Object>> searchHomesForChat(String query) {
    LOG.info("Searching home embeddings for: {}", query);

    List<Document> results =
        vectorStore.similaritySearch(
            SearchRequest.builder().query(query).topK(5).build());

    return results.stream()
        .map(
            doc -> {
              Map<String, Object> meta = doc.getMetadata();
              return Map.of(
                  "content", doc.getText(),
                  "metadata", meta != null ? meta : Map.of());
            })
        .toList();
  }
}
