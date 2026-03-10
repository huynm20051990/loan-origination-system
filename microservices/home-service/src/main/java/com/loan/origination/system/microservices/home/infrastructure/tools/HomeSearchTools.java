package com.loan.origination.system.microservices.home.infrastructure.tools;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
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

  public HomeSearchTools(VectorStore vectorStore, ChatClient.Builder builder) {
    this.vectorStore = vectorStore;
  }

  @Tool(
      description =
          """
    Search for real estate properties using natural language and metadata filters.
    Use this tool whenever the user asks to find, list, or filter homes.
    Returns a list of unique property IDs matching the criteria.
    """)
  public List<UUID> searchProperties(
      @ToolParam(
              description =
                  """
            The 'vibe' or descriptive part of the search (e.g., 'modern kitchen', 'quiet neighborhood', 'spacious backyard').
            Extract feelings and styles here. If no vibe is provided, use the original user query.
            """)
          String query,
      @ToolParam(
              description =
                  """
    A structured metadata filter string using Spring AI syntax.
    AVAILABLE FIELDS: street, city, state, country, price, beds, baths, sqft, status.
    OPERATORS: ==, !=, <, <=, >, >=, &&, ||.

    STRICT MAPPING RULES:
    - 'More than', 'upper', 'greater than', 'above', 'higher than' -> Use >
    - 'At least', 'minimum', 'starting from', 'from' -> Use >=
    - 'Less than', 'under', 'below', 'lower than' -> Use <
    - 'At most', 'maximum', 'up to', 'no more than' -> Use <=
    - 'Exactly', 'equal to', 'is' -> Use ==

    CASE SENSITIVITY:
    - All string values (city, state, status, street, country) MUST be lowercase.

    Example: (price > 500000 && city == 'austin' && beds >= 3)
    """)
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
