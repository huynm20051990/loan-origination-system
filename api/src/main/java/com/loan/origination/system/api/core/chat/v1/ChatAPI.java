package com.loan.origination.system.api.core.chat.v1;

import com.loan.origination.system.api.core.chat.dto.ChatRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * Primary port contract for the AI Chat Box feature.
 *
 * <p>Accepts a natural language query and session ID, then returns a Server-Sent Event stream
 * with the following named event types:
 *
 * <ul>
 *   <li>{@code listings} — JSON array of matching homes (emitted once after home-service
 *       responds, before AI tokens begin)
 *   <li>{@code token} — individual AI text chunks (emitted per streaming token)
 *   <li>{@code done} — stream completion signal (empty data)
 *   <li>{@code error} — error message; stream terminates after this event
 * </ul>
 *
 * <p>This interface is implemented by {@code ChatController} in {@code chat-service} and
 * consumed by the Angular {@code ChatService} via the Spring Cloud Gateway.
 */
@RequestMapping("/api/v1/chat")
public interface ChatAPI {

  /**
   * Stream a chat response for a natural language home-search query.
   *
   * @param request contains {@code sessionId} (frontend-generated UUID) and {@code query}
   *     (non-blank natural language string)
   * @return SSE stream — see event type documentation above
   */
  @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<ServerSentEvent<String>> stream(@RequestBody ChatRequestDTO request);
}
