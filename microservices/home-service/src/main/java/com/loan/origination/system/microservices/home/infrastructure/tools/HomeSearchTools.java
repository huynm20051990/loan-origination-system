package com.loan.origination.system.microservices.home.infrastructure.tools;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.stereotype.Component;

@Component
public class HomeSearchTools {

  private static final Logger LOG = LoggerFactory.getLogger(HomeSearchTools.class);

  private final VectorStore vectorStore;

  public HomeSearchTools(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Tool(
      description = "Search for real estate properties using a vibe and a filter string.",
      returnDirect = true)
  public List<UUID> searchProperties(
      @ToolParam(description = "The style/feel of the home (e.g. 'modern')") String query,
      @ToolParam(description = "The structured filter string (e.g. price > 100000)")
          String filterString) {

    LOG.info("AI Search Request - Query: '{}', Filter: '{}'", query, filterString);
    Filter.Expression expression = new FilterExpressionTextParser().parse(filterString);

    SearchRequest request =
        SearchRequest.builder().query(query).filterExpression(expression).topK(5).build();

    // Your requested logic:
    return vectorStore.similaritySearch(request).stream()
        .map(doc -> UUID.fromString(doc.getMetadata().get("homeId").toString()))
        .toList();
  }
}
