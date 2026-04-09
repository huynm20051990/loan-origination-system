package com.loan.origination.system.microservices.chat.infrastructure.output.client;

import com.loan.origination.system.microservices.chat.application.port.output.HomeResult;
import com.loan.origination.system.microservices.chat.application.port.output.HomeSearchPort;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
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
 *
 * <p>Each outbound request carries a W3C {@code traceparent} header derived from the active
 * Micrometer {@link Tracer} span so that Istio/Jaeger can correlate the downstream home-service
 * call back to the originating chat-service trace.
 */
@Component
public class HomeSearchAdapter implements HomeSearchPort {

    private static final Logger LOG = LoggerFactory.getLogger(HomeSearchAdapter.class);

    /** W3C Trace Context version — always {@code "00"} per the specification. */
    private static final String W3C_VERSION = "00";

    private static final ParameterizedTypeReference<List<HomeResult>> HOME_RESULT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final Tracer tracer;

    /**
     * Constructs the adapter with the pre-configured home-service {@link RestClient} and a
     * Micrometer {@link Tracer} used for W3C trace context propagation.
     *
     * @param restClient a {@link RestClient} with the home-service base URL already set; injected
     *     as the {@code homeRestClient} bean from {@code BeanConfiguration}
     * @param tracer the Micrometer {@link Tracer} used to read the active span's trace/span IDs
     *     so they can be forwarded to home-service via the W3C {@code traceparent} header
     */
    public HomeSearchAdapter(RestClient restClient, Tracer tracer) {
        this.restClient = restClient;
        this.tracer = tracer;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Issues a {@code GET /api/v1/homes/search} request with the user's query as the
     * {@code query} parameter. The W3C {@code traceparent} header is injected when there is an
     * active span so that the downstream home-service call is visible in Istio/Jaeger traces.
     * On success the JSON array is deserialised into a {@code List<HomeResult>} using Spring's
     * {@link RestClient} message converters (Jackson).
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
                            .headers(
                                    headers -> {
                                        String traceparent = buildTraceParent();
                                        if (traceparent != null) {
                                            headers.set("traceparent", traceparent);
                                        }
                                    })
                            .retrieve()
                            .body(HOME_RESULT_LIST_TYPE);
            return results != null ? results : List.of();
        } catch (RestClientException ex) {
            LOG.warn("home-service search failed for query='{}': {}", query, ex.getMessage());
            throw new HomeSearchUnavailableException(ex);
        }
    }

    /**
     * Builds a W3C {@code traceparent} header value from the currently active Micrometer span.
     *
     * <p>Format: {@code 00-{traceId}-{spanId}-{flags}} where {@code flags} is {@code "01"} when
     * the span is sampled and {@code "00"} otherwise. Returns {@code null} when there is no active
     * span so the caller can skip header injection rather than sending an invalid value.
     *
     * @return a W3C-formatted {@code traceparent} string, or {@code null} if no span is active
     */
    private String buildTraceParent() {
        Span current = tracer.currentSpan();
        if (current == null) {
            return null;
        }
        TraceContext ctx = current.context();
        String flags = Boolean.TRUE.equals(ctx.sampled()) ? "01" : "00";
        return W3C_VERSION + "-" + ctx.traceId() + "-" + ctx.spanId() + "-" + flags;
    }
}
