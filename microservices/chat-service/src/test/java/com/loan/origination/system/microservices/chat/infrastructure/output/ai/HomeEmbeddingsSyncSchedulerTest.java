package com.loan.origination.system.microservices.chat.infrastructure.output.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
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
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class HomeEmbeddingsSyncSchedulerTest {

  @Mock
  private RestClient.Builder restClientBuilder;

  @Mock
  private RestClient restClient;

  @Mock
  private VectorStore vectorStore;

  private HomeEmbeddingsSyncScheduler scheduler;

  @BeforeEach
  void setUp() {
    when(restClientBuilder.baseUrl(any(String.class))).thenReturn(restClientBuilder);
    when(restClientBuilder.build()).thenReturn(restClient);
    scheduler = new HomeEmbeddingsSyncScheduler(restClientBuilder, vectorStore, "http://home-service");
  }

  @Test
  void syncEmbeddings_addsDocumentsToVectorStore() {
    // Arrange: mock RestClient chain returning sample listing JSON
    RestClient.RequestHeadersUriSpec uriSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec headersSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersSpec.class);
    RestClient.ResponseSpec responseSpec = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);

    List<Map<String, Object>> listings = List.of(
        Map.of("id", "1", "address", "123 Main St", "price", 450000, "bedrooms", 3),
        Map.of("id", "2", "address", "456 Oak Ave", "price", 620000, "bedrooms", 4)
    );

    when(restClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri(any(String.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(listings);

    // Act
    scheduler.syncEmbeddings();

    // Assert
    ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
    verify(vectorStore).add(captor.capture());
    List<Document> documents = captor.getValue();
    assertThat(documents).hasSize(2);
    assertThat(documents.get(0).getText()).contains("123 Main St");
    assertThat(documents.get(1).getText()).contains("456 Oak Ave");
  }

  @Test
  void syncEmbeddings_doesNotCallVectorStoreWhenNoListings() {
    RestClient.RequestHeadersUriSpec uriSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec headersSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersSpec.class);
    RestClient.ResponseSpec responseSpec = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);

    when(restClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri(any(String.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(List.of());

    scheduler.syncEmbeddings();

    verify(vectorStore, never()).add(anyList());
  }
}
