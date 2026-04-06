package com.loan.origination.system.microservices.chat.infrastructure.output.ai.tools;

import com.loan.origination.system.api.core.home.dto.HomeResponseDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HomeSearchChatTools {

  private static final Logger LOG = LoggerFactory.getLogger(HomeSearchChatTools.class);
  private static final String SEARCH_ENDPOINT = "/api/v1/homes/search";

  private final RestClient restClient;

  public HomeSearchChatTools(
      RestClient.Builder restClientBuilder,
      @Value("${home-service.base-url}") String homeServiceBaseUrl) {
    this.restClient = restClientBuilder.baseUrl(homeServiceBaseUrl).build();
  }

  @Tool(description = "Search home listings using a natural language query about price, bedrooms, location, or other property features. Returns matching home summaries.")
  public List<HomeResponseDTO> searchHomesForChat(String query) {
    LOG.info("Searching homes in real-time from home-service for: {}", query);

    List<HomeResponseDTO> results = restClient.get()
        .uri(uriBuilder -> uriBuilder.path(SEARCH_ENDPOINT).queryParam("query", query).build())
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});

    if (results == null) {
      return List.of();
    }

    LOG.info("Found {} homes matching: {}", results.size(), query);
    return results;
  }
}
