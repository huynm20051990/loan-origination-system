package com.loan.origination.system.microservices.chat.infrastructure.output.client;

/**
 * Unchecked exception thrown when the home-service search endpoint is unavailable.
 *
 * <p>Raised by {@code HomeSearchAdapter} when the downstream REST call returns a non-2xx
 * response or when a network-level error prevents the request from completing. Callers
 * (e.g. {@code ChatApplicationService}) should catch this exception and emit an {@code error}
 * Server-Sent Event to the client rather than propagating it as an unhandled fault.
 */
public class HomeSearchUnavailableException extends RuntimeException {

    /**
     * Creates a new exception with the standard "home-service unavailable" message.
     */
    public HomeSearchUnavailableException() {
        super("home-service unavailable");
    }

    /**
     * Creates a new exception with the standard message and a root cause.
     *
     * @param cause the underlying exception that triggered this failure
     */
    public HomeSearchUnavailableException(Throwable cause) {
        super("home-service unavailable", cause);
    }
}
