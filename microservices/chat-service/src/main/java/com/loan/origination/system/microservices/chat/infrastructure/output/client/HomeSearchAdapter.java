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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Secondary adapter that fulfils the {@link HomeSearchPort} output port contract by calling the
 * home-service search REST API using a non-blocking {@link WebClient}.
 *
 * <p>Translates a natural-language query string into a reactive
 * {@code GET /api/v1/homes/search?query=…} request and deserialises the JSON array response body
 * directly into a typed {@code List<HomeResult>}.
 *
 * <p>Any error response or network failure is mapped to a {@link HomeSearchUnavailableException}
 * via {@link Mono#onErrorMap} so callers treat home-service unavailability as a distinct,
 * handleable failure mode without coupling to the HTTP transport layer.
 *
 * <p>Each outbound request carries a W3C {@code traceparent} header derived from the active
 * Micrometer {@link Tracer} span so that Istio/Jaeger can correlate the downstream home-service
 * call back to the originating chat-service trace.
 */
@Component
public class HomeSearchAdapter implements HomeSearchPort {

    private static final Logger LOG = LoggerFactory.getLogger(HomeSearchAdapter.class);

    private static final String W3C_VERSION = "00";

    private static final ParameterizedTypeReference<List<HomeResult>> HOME_RESULT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final WebClient webClient;
    private final Tracer tracer;

    /**
     * Constructs the adapter with the pre-configured home-service {@link WebClient} and a
     * Micrometer {@link Tracer} used for W3C trace context propagation.
     *
     * @param webClient a {@link WebClient} with the home-service base URL already set
     * @param tracer    the Micrometer {@link Tracer} used to read the active span's trace/span IDs
     */
    public HomeSearchAdapter(WebClient webClient, Tracer tracer) {
        this.webClient = webClient;
        this.tracer = tracer;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Issues a non-blocking {@code GET /api/v1/homes/search} request. The W3C {@code traceparent}
     * header is injected when there is an active span. On success the JSON array is deserialised
     * into a {@code List<HomeResult>}. Any error is mapped to
     * {@link HomeSearchUnavailableException}.
     */
    @Override
    public Mono<List<HomeResult>> search(String query) {
        LOG.debug("Querying home-service: query={}", query);
        String traceparent = buildTraceParent();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/homes/search")
                        .queryParam("query", query)
                        .build())
                .headers(headers -> {
                    if (traceparent != null) {
                        headers.set("traceparent", traceparent);
                    }
                })
                .retrieve()
                .bodyToMono(HOME_RESULT_LIST_TYPE)
                .map(results -> results != null ? results : List.<HomeResult>of())
                .onErrorMap(WebClientResponseException.class,
                        ex -> new HomeSearchUnavailableException(ex))
                .onErrorMap(Exception.class,
                        ex -> ex instanceof HomeSearchUnavailableException
                                ? ex
                                : new HomeSearchUnavailableException(ex))
                .doOnError(HomeSearchUnavailableException.class,
                        ex -> LOG.warn("home-service search failed for query='{}': {}", query, ex.getMessage()));
    }

    /**
     * Builds a W3C {@code traceparent} header value from the currently active Micrometer span.
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
