package com.loan.origination.system.microservices.chat.infrastructure.input.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.api.core.chat.v1.ChatAPI;
import com.loan.origination.system.microservices.chat.application.port.input.ChatUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

/**
 * Primary adapter that exposes the AI chat SSE endpoint over HTTP.
 *
 * <p>Implements {@link ChatAPI} and delegates all business logic to {@link ChatUseCase}.
 * Validation of the request body is enforced via {@code @Valid}; Spring WebFlux will return
 * {@code 400 Bad Request} automatically when a {@code @NotBlank} constraint is violated.
 *
 * <p>All SSE event data fields are serialised as JSON on the wire so that every consumer
 * (browser {@code EventSource}, {@link org.springframework.test.web.reactive.server.WebTestClient})
 * can decode each event's payload with a single {@code JSON.parse()} call. Data that is already
 * valid JSON (e.g. the {@code listings} payload) is forwarded unchanged; raw text fragments
 * (e.g. AI tokens) are wrapped in a JSON string literal before transmission.
 */
@RestController
public class ChatController implements ChatAPI {

    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    private final ChatUseCase chatUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Constructs the controller with the chat use case.
     *
     * @param chatUseCase the input port that orchestrates the SSE streaming flow
     */
    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link ChatUseCase#stream(String, String)} after extracting the session ID
     * and query from the validated request body. Each SSE event's data is normalised to a valid
     * JSON value so that the wire format is consistent regardless of event type.
     */
    @Override
    public Flux<ServerSentEvent<String>> stream(@RequestParam String sessionId, @RequestParam String query) {
        if (sessionId == null || sessionId.isBlank() || query == null || query.isBlank()) {
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "sessionId and query must not be blank"));
        }
        LOG.debug("Received chat stream request: sessionId={}, query={}", sessionId, query);
        return chatUseCase.stream(sessionId, query)
                .map(event -> ServerSentEvent.<String>builder()
                        .event(event.event())
                        .data(toJsonData(event.data()))
                        .build());
    }

    /**
     * Ensures the data string is valid JSON before it is written to the SSE wire format.
     *
     * <p>If the raw data is already a well-formed JSON value (e.g. an array or object produced by
     * the {@code listings} event), it is returned unchanged. Raw text fragments (e.g. AI token
     * chunks or error messages) are serialised as a JSON string literal so the client always
     * receives a parseable value.
     *
     * @param raw the original data string from the use-case event (may be null)
     * @return a JSON-safe string; never {@code null}
     */
    private String toJsonData(String raw) {
        if (raw == null) {
            return "null";
        }
        // Check whether raw is already valid JSON (e.g. the listings array).
        // If so, return it unchanged to avoid double-encoding.
        try {
            objectMapper.readTree(raw);
            return raw;
        } catch (JsonProcessingException e) {
            // raw is plain text — wrap it in a JSON string literal
            try {
                return objectMapper.writeValueAsString(raw);
            } catch (JsonProcessingException ex) {
                LOG.warn("Failed to JSON-encode SSE data '{}'; falling back to null", raw, ex);
                return "null";
            }
        }
    }
}
