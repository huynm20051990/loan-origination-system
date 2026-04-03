package com.loan.origination.system.microservices.chat.infrastructure.output.ai.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class HomeSearchChatToolsTest {

  @Mock
  private VectorStore vectorStore;

  private HomeSearchChatTools homeSearchChatTools;

  @BeforeEach
  void setUp() {
    homeSearchChatTools = new HomeSearchChatTools(vectorStore);
  }

  @Test
  void searchHomesForChat_callsVectorStoreWithQuery() {
    String query = "3-bedroom home under $500k";
    Document doc = Document.builder()
        .text("Nice home at 123 Main St, 3 beds, $450k")
        .metadata(Map.of("address", "123 Main St", "price", "450000"))
        .build();
    when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

    List<Map<String, Object>> results = homeSearchChatTools.searchHomesForChat(query);

    ArgumentCaptor<SearchRequest> captor = ArgumentCaptor.forClass(SearchRequest.class);
    verify(vectorStore).similaritySearch(captor.capture());
    assertThat(captor.getValue().getQuery()).isEqualTo(query);
    assertThat(captor.getValue().getTopK()).isEqualTo(5);
  }

  @Test
  void searchHomesForChat_returnsContentAndMetadata() {
    Document doc = Document.builder()
        .text("Lovely 2-bed condo")
        .metadata(Map.of("beds", "2"))
        .build();
    when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));

    List<Map<String, Object>> results = homeSearchChatTools.searchHomesForChat("condo 2 beds");

    assertThat(results).hasSize(1);
    assertThat(results.get(0)).containsKey("content");
    assertThat(results.get(0)).containsKey("metadata");
    assertThat(results.get(0).get("content")).isEqualTo("Lovely 2-bed condo");
  }

  @Test
  void searchHomesForChat_returnsEmptyListWhenNoResults() {
    when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

    List<Map<String, Object>> results = homeSearchChatTools.searchHomesForChat("mansion in Mars");

    assertThat(results).isEmpty();
  }
}
