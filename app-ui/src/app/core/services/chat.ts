import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

/** Represents a single SSE event received from the chat-service stream endpoint. */
export interface ChatSseEvent {
  /** Named SSE event type: {@code listings}, {@code token}, {@code done}, or {@code error}. */
  type: string;
  /** Raw event data string. For {@code listings} events this is a JSON-encoded Home array. */
  data: string;
}

/**
 * Angular service that bridges the chat-service SSE stream to an RxJS Observable.
 *
 * <p>Opens a native {@link EventSource} connection to the chat-service gateway endpoint.
 * Named SSE events ({@code listings}, {@code token}, {@code done}, {@code error}) are
 * mapped to {@link ChatSseEvent} emissions on the returned Observable.
 *
 * <p>The connection is automatically closed when:
 * <ul>
 *   <li>The server emits a {@code done} event (stream complete).
 *   <li>The server emits an {@code error} event (stream terminates gracefully).
 *   <li>The subscriber unsubscribes (teardown logic).
 * </ul>
 */
@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private readonly BASE_URL = '/api/v1/chat/stream';

  /**
   * Opens an SSE connection to the chat-service and returns a stream of typed events.
   *
   * @param sessionId - Frontend-generated UUID that scopes chat memory per user session.
   * @param query - Natural language home-search query entered by the user.
   * @returns Observable that emits {@link ChatSseEvent} objects until the stream closes.
   */
  stream(sessionId: string, query: string): Observable<ChatSseEvent> {
    return new Observable<ChatSseEvent>((observer) => {
      const url =
        `${this.BASE_URL}?sessionId=${encodeURIComponent(sessionId)}` +
        `&query=${encodeURIComponent(query)}`;

      const es = new EventSource(url);

      const handleListings = (event: Event): void => {
        observer.next({ type: 'listings', data: (event as MessageEvent).data });
      };

      const handleToken = (event: Event): void => {
        observer.next({ type: 'token', data: (event as MessageEvent).data });
      };

      const handleDone = (_event: Event): void => {
        es.close();
        observer.complete();
      };

      const handleError = (event: Event): void => {
        observer.next({ type: 'error', data: (event as MessageEvent).data });
        es.close();
        observer.complete();
      };

      es.addEventListener('listings', handleListings);
      es.addEventListener('token', handleToken);
      es.addEventListener('done', handleDone);
      es.addEventListener('error', handleError);

      // Teardown: close the connection if the consumer unsubscribes before the stream ends.
      return () => {
        es.removeEventListener('listings', handleListings);
        es.removeEventListener('token', handleToken);
        es.removeEventListener('done', handleDone);
        es.removeEventListener('error', handleError);
        es.close();
      };
    });
  }
}
