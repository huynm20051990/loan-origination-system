package com.loan.origination.system.microservices.chat.application.port.output;

import java.util.List;

/**
 * Output port for querying home listings from an external search service.
 *
 * <p>Implementations of this interface are responsible for translating natural-language
 * queries into HTTP calls to the home-service search API and mapping the response
 * to {@link HomeResult} instances.
 *
 * <p>Callers should handle {@link com.loan.origination.system.microservices.chat.infrastructure.output.client.HomeSearchUnavailableException}
 * when the downstream service is unreachable or returns a non-2xx response.
 */
public interface HomeSearchPort {

    /**
     * Searches for home listings matching the given natural-language query.
     *
     * @param query the user's natural-language search string (e.g. "3 beds under $500k in Austin")
     * @return a list of matching {@link HomeResult} records; empty list if no matches are found
     */
    List<HomeResult> search(String query);
}
