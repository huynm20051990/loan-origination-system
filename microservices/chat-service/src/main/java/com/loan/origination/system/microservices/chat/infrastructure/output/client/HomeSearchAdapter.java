package com.loan.origination.system.microservices.chat.infrastructure.output.client;

import com.loan.origination.system.microservices.chat.application.port.output.HomeResult;
import com.loan.origination.system.microservices.chat.application.port.output.HomeSearchPort;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Secondary adapter that fulfils the {@link HomeSearchPort} output port contract by calling the
 * home-service search REST API.
 *
 * <p>Translates a natural-language query string into a {@code GET /api/v1/homes/search?query=…}
 * request and deserialises the JSON array response body directly into a typed
 * {@code List<HomeResult>}.
 *
 * <p>Any {@link RestClientException} thrown by the underlying {@link RestClient} (e.g. non-2xx
 * status, network timeout) is caught and re-raised as a {@link HomeSearchUnavailableException}
 * so that callers can treat home-service unavailability as a distinct, handleable failure mode
 * without coupling to the HTTP transport layer.
 */
@Component
public class HomeSearchAdapter implements HomeSearchPort {

    private static final Logger LOG = LoggerFactory.getLogger(HomeSearchAdapter.class);

    private static final ParameterizedTypeReference<List<HomeResult>> HOME_RESULT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    /**
     * Constructs the adapter with the pre-configured home-service {@link RestClient}.
     *
     * @param restClient a {@link RestClient} with the home-service base URL already set; injected
     *     as the {@code homeRestClient} bean from {@code BeanConfiguration}
     */
    public HomeSearchAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Issues a {@code GET /api/v1/homes/search} request with the user's query as the
     * {@code query} parameter. On success the JSON array is deserialised into a
     * {@code List<HomeResult>} using Spring's {@link RestClient} message converters (Jackson).
     *
     * @throws HomeSearchUnavailableException if the home-service returns a non-2xx status or the
     *     request cannot reach the server
     */
    @Override
    public List<HomeResult> search(String query) {
        LOG.debug("Querying home-service: query={}", query);
        try {
            List<HomeResult> results =
                    restClient
                            .get()
                            .uri(
                                    uriBuilder ->
                                            uriBuilder
                                                    .path("/api/v1/homes/search")
                                                    .queryParam("query", query)
                                                    .build())
                            .retrieve()
                            .body(HOME_RESULT_LIST_TYPE);
            return results != null ? results : List.of();
        } catch (RestClientException ex) {
            LOG.warn("home-service search failed for query='{}': {}", query, ex.getMessage());
            throw new HomeSearchUnavailableException(ex);
        }
    }
}
