package com.loan.origination.system.microservices.chat.infrastructure.output.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.loan.origination.system.microservices.chat.application.port.output.HomeResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for {@link HomeSearchAdapter}.
 *
 * <p>Uses WireMock to simulate the home-service HTTP layer without starting a real server.
 * Verifies two scenarios:
 * <ol>
 *   <li>A successful {@code 200} response is deserialised into a correctly-typed
 *       {@link List}{@code <}{@link HomeResult}{@code >}.</li>
 *   <li>A {@code 503} response causes the adapter to throw
 *       {@link HomeSearchUnavailableException}, preserving the error contract
 *       so callers can handle degraded-service gracefully.</li>
 * </ol>
 *
 * <p>These tests are written in the <em>Red</em> phase and will fail to compile until
 * {@link HomeSearchAdapter} and {@link HomeSearchUnavailableException} are created in T024/T025.
 */
class HomeSearchAdapterIT {

    /** JSON body returned by WireMock for the happy-path stub. */
    private static final String HOMES_JSON = """
            [
              {
                "id": "home-42",
                "price": 495000,
                "beds": 3,
                "baths": 2.0,
                "sqft": 1850,
                "imageUrl": "https://img.example.com/42",
                "address": {
                  "street": "789 Pine Rd",
                  "city": "Austin",
                  "state": "TX",
                  "zip": "78703"
                },
                "status": "active",
                "description": "Updated kitchen, open floor plan"
              },
              {
                "id": "home-99",
                "price": 480000,
                "beds": 3,
                "baths": 2.5,
                "sqft": 2100,
                "imageUrl": "https://img.example.com/99",
                "address": {
                  "street": "321 Elm St",
                  "city": "Austin",
                  "state": "TX",
                  "zip": "78704"
                },
                "status": "active",
                "description": "Corner lot with mature trees"
              }
            ]
            """;

    private WireMockServer wireMock;
    private HomeSearchAdapter adapter;

    /**
     * Starts an ephemeral WireMock server on a random port and wires the adapter
     * to its base URL so every test runs against a clean server instance.
     */
    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMock.start();

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.port())
                .build();

        adapter = new HomeSearchAdapter(restClient);
    }

    /** Stops the WireMock server after each test to release the ephemeral port. */
    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    /**
     * Happy path: the home-service returns a JSON array with two listings.
     *
     * <p>Asserts that the adapter deserialises the array into a {@code List<HomeResult>}
     * with the correct field values for both the top-level record and the nested
     * {@link HomeResult.Address} record.
     */
    @Test
    @DisplayName("search() deserialises 200 JSON array into List<HomeResult> correctly")
    void search_shouldDeserialiseResultsWhenHomeServiceReturns200() {
        // Given: home-service returns two matching listings
        wireMock.stubFor(
                get(urlPathEqualTo("/api/v1/homes/search"))
                        .withQueryParam("query", equalTo("3 beds under $500k"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(HOMES_JSON)));

        // When
        List<HomeResult> results = adapter.search("3 beds under $500k");

        // Then: list size and first-result fields
        assertThat(results).hasSize(2);

        HomeResult first = results.get(0);
        assertThat(first.id()).isEqualTo("home-42");
        assertThat(first.price()).isEqualTo(495_000);
        assertThat(first.beds()).isEqualTo(3);
        assertThat(first.baths()).isEqualTo(2.0);
        assertThat(first.sqft()).isEqualTo(1_850);
        assertThat(first.status()).isEqualTo("active");
        assertThat(first.address().city()).isEqualTo("Austin");
        assertThat(first.address().state()).isEqualTo("TX");
        assertThat(first.address().zip()).isEqualTo("78703");

        HomeResult second = results.get(1);
        assertThat(second.id()).isEqualTo("home-99");
        assertThat(second.beds()).isEqualTo(3);
        assertThat(second.address().street()).isEqualTo("321 Elm St");
    }

    /**
     * Degraded-service scenario: the home-service returns HTTP 503.
     *
     * <p>Asserts that the adapter wraps the underlying {@code RestClientException}
     * in a {@link HomeSearchUnavailableException} with the canonical message
     * {@code "home-service unavailable"} so callers can differentiate service
     * unavailability from other errors.
     */
    @Test
    @DisplayName("search() throws HomeSearchUnavailableException when home-service returns 503")
    void search_shouldThrowHomeSearchUnavailableExceptionOn503() {
        // Given: home-service is degraded
        wireMock.stubFor(
                get(urlPathEqualTo("/api/v1/homes/search"))
                        .withQueryParam("query", equalTo("anything"))
                        .willReturn(
                                aResponse()
                                        .withStatus(503)
                                        .withBody("Service Unavailable")));

        // When / Then
        assertThatThrownBy(() -> adapter.search("anything"))
                .isInstanceOf(HomeSearchUnavailableException.class)
                .hasMessageContaining("home-service unavailable");
    }
}
