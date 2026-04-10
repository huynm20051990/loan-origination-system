package com.loan.origination.system.microservices.chat.application.port.output;

import java.util.List;
import reactor.core.publisher.Mono;

/**
 * Output port for querying home listings from an external search service.
 *
 * <p>Implementations of this interface are responsible for translating natural-language
 * queries into non-blocking HTTP calls to the home-service search API and mapping the
 * response to {@link HomeResult} instances.
 *
 * <p>The reactive return type ({@link Mono}) keeps the caller (running on a Reactor
 * event-loop thread) non-blocking throughout the I/O operation.
 */
public interface HomeSearchPort {

    /**
     * Searches for home listings matching the given natural-language query.
     *
     * @param query the user's natural-language search string (e.g. "3 beds under $500k in Austin")
     * @return a {@link Mono} emitting the list of matching {@link HomeResult} records,
     *         or an empty list if no matches are found; errors signal as
     *         {@link com.loan.origination.system.microservices.chat.infrastructure.output.client.HomeSearchUnavailableException}
     */
    Mono<List<HomeResult>> search(String query);
}
