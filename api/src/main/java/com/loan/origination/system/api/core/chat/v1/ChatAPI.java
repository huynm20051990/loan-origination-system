package com.loan.origination.system.api.core.chat.v1;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * Primary port contract for the AI Chat Box feature.
 *
 * <p>Accepts a natural language query and session ID as query parameters, then returns a
 * Server-Sent Event stream with the following named event types:
 *
 * <ul>
 *   <li>{@code listings} — JSON array of matching homes (emitted once after home-service
 *       responds, before AI tokens begin)
 *   <li>{@code token} — individual AI text chunks (emitted per streaming token)
 *   <li>{@code done} — stream completion signal (empty data)
 *   <li>{@code error} — error message; stream terminates after this event
 * </ul>
 *
 * <p>Uses GET so the browser's native {@code EventSource} API can open the SSE connection
 * directly without requiring custom header support.
 *
 * <p>This interface is implemented by {@code ChatController} in {@code chat-service} and
 * consumed by the Angular {@code ChatService} via the Spring Cloud Gateway.
 */
@RequestMapping("/api/v1/chat")
public interface ChatAPI {

  /**
   * Stream a chat response for a natural language home-search query.
   *
   * @param sessionId frontend-generated UUID identifying the chat session
   * @param query non-blank natural language home-search query
   * @return SSE stream — see event type documentation above
   */
  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<ServerSentEvent<String>> stream(
      @RequestParam String sessionId,
      @RequestParam String query);
}
