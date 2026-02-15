package com.loan.origination.system.microservices.home.adapter.out.persistence;

import com.loan.origination.system.microservices.home.domain.model.FilterItem;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.model.SearchIntent;
import com.loan.origination.system.microservices.home.domain.port.out.HomeSearchPort;
import java.lang.reflect.*;
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

@Component
public class PgVectorHomeSearchAdapter implements HomeSearchPort {

  private static final Logger LOG = LoggerFactory.getLogger(PgVectorHomeSearchAdapter.class);
  private final VectorStore vectorStore;
  private final ChatClient chatClient;

  public PgVectorHomeSearchAdapter(VectorStore vectorStore, ChatClient chatClient) {
    this.vectorStore = vectorStore;
    this.chatClient = chatClient;
  }

  @Override
  public void indexHome(Home home) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("homeId", home.getId().toString());
    metadata.put("city", home.getAddress().city());
    metadata.put("state", home.getAddress().state());
    metadata.put("price", home.getPrice());
    metadata.put("beds", home.getBeds());
    metadata.put("baths", home.getBaths());
    metadata.put("sqft", home.getSqft());
    metadata.put("status", home.getStatus().name()); // Store as String (e.g., "AVAILABLE")

    // 2. Create the document using the description as the search text
    Document document = new Document(home.getDescription(), metadata);

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
    return Arrays.stream(Home.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.joining(", "));
  }

  private Filter.Expression mapToExpression(FilterExpressionBuilder b, FilterItem f) {
    if (f.column() == null || f.operator() == null) return null;

    return switch (f.operator().toUpperCase()) {
      case "GT", ">" -> b.gt(f.column(), f.value()).build();
      case "LT", "<" -> b.lt(f.column(), f.value()).build();
      case "GTE", ">=" -> b.gte(f.column(), f.value()).build();
      case "LTE", "<=" -> b.lte(f.column(), f.value()).build();
      case "EQ", "==" -> b.eq(f.column(), f.value()).build();
      default -> b.eq(f.column(), f.value()).build();
    };
  }
}
