package com.loan.origination.system.microservices.chat.application.port.input;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * Primary input port for the chat use case.
 *
 * <p>Defines the contract for streaming AI-assisted home search responses via
 * Server-Sent Events (SSE). Implementations orchestrate the full flow:
 * <ol>
 *   <li>Query the home-service for matching listings and emit a {@code listings} event.</li>
 *   <li>Build a prompt that includes the listing results as context.</li>
 *   <li>Stream the AI model's reply as sequential {@code token} events.</li>
 *   <li>Emit a {@code done} event to signal completion.</li>
 *   <li>Emit an {@code error} event if the home-service is unavailable.</li>
 * </ol>
 *
 * <p>The {@code sessionId} is used by the chat-memory advisor to maintain per-session
 * conversation history across multiple turns.
 */
public interface ChatUseCase {

    /**
     * Streams an AI-assisted chat response for the given query as Server-Sent Events.
     *
     * <p>Event types in emission order:
     * <ul>
     *   <li>{@code listings} — JSON array of {@link com.loan.origination.system.microservices.chat.application.port.output.HomeResult} objects (may be empty).</li>
     *   <li>{@code token} — one or more AI-generated text fragments.</li>
     *   <li>{@code done} — signals the end of the stream.</li>
     *   <li>{@code error} — emitted instead of the above if the home-service is unavailable.</li>
     * </ul>
     *
     * @param sessionId unique identifier for the user's chat session; used to correlate memory
     * @param query     the user's natural-language home search query
     * @return a {@link Flux} of {@link ServerSentEvent} items; never {@code null}
     */
    Flux<ServerSentEvent<String>> stream(String sessionId, String query);
}
