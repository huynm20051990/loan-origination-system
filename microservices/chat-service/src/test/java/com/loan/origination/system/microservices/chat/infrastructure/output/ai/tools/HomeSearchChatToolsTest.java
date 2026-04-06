package com.loan.origination.system.microservices.chat.infrastructure.output.ai.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.loan.origination.system.api.core.home.dto.HomeResponseDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@ExtendWith(MockitoExtension.class)
class HomeSearchChatToolsTest {

  @Mock
  private RestClient.Builder restClientBuilder;

  @Mock
  private RestClient restClient;

  @Mock
  private RestClient.RequestHeadersUriSpec<?> uriSpec;

  @Mock
  private RestClient.RequestHeadersSpec<?> headersSpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  private HomeSearchChatTools homeSearchChatTools;

  @BeforeEach
  void setUp() {
    when(restClientBuilder.baseUrl(any(String.class))).thenReturn(restClientBuilder);
    when(restClientBuilder.build()).thenReturn(restClient);
    homeSearchChatTools = new HomeSearchChatTools(restClientBuilder, "http://home-service");
  }

  @Test
  void searchHomesForChat_callsHomeServiceSearchEndpoint() {
    HomeResponseDTO home = new HomeResponseDTO(
        UUID.randomUUID(), new BigDecimal("450000"), 3, 2.0, 1500, null, null, "ACTIVE", "Nice home");

    when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(Function.class))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(home));

    List<HomeResponseDTO> results = homeSearchChatTools.searchHomesForChat("3-bedroom home under $500k");

    verify(restClient).get();
    assertThat(results).hasSize(1);
    assertThat(results.get(0).beds()).isEqualTo(3);
    assertThat(results.get(0).price()).isEqualByComparingTo(new BigDecimal("450000"));
  }

  @Test
  void searchHomesForChat_returnsEmptyListWhenNoResults() {
    when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(Function.class))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of());

    List<HomeResponseDTO> results = homeSearchChatTools.searchHomesForChat("mansion in Mars");

    assertThat(results).isEmpty();
  }

  @Test
  void searchHomesForChat_returnsEmptyListWhenNullResponse() {
    when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(Function.class))).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

    List<HomeResponseDTO> results = homeSearchChatTools.searchHomesForChat("anything");

    assertThat(results).isEmpty();
  }
}
