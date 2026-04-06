import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChatSession } from '../models/chat-session';
import { ChatMessage } from '../models/chat-message';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly API_URL = 'https://localhost:8443/api/v1/chat';

  constructor(private http: HttpClient, private authService: AuthService) {}

  createSession(): Observable<ChatSession> {
    return this.http.post<ChatSession>(`${this.API_URL}/sessions`, {});
  }

  getMessages(sessionId: string): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.API_URL}/sessions/${sessionId}/messages`);
  }

  deleteSession(sessionId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/sessions/${sessionId}`);
  }

  /**
   * Sends a message and streams AI response tokens via SSE.
   * Calls onToken for each token, onDone when stream completes.
   */
  sendMessage(
    sessionId: string,
    content: string,
    onToken: (token: string) => void,
    onDone: () => void
  ): void {
    const url = `${this.API_URL}/sessions/${sessionId}/messages`;

    // Use fetch API for SSE streaming (HttpClient doesn't natively support SSE streaming).
    // Manually attach the Bearer token because fetch bypasses Angular's HTTP interceptors.
    this.authService.getAccessToken().subscribe(token => {
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ content })
    }).then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      const reader = response.body!.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      const pump = (): Promise<void> => reader.read().then(({ done, value }) => {
        if (done) {
          onDone();
          return;
        }
        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split('\n');
        buffer = lines.pop() ?? '';

        for (const line of lines) {
          if (line.startsWith('data:')) {
            const jsonStr = line.substring(5).trim();
            if (jsonStr) {
              try {
                const chunk = JSON.parse(jsonStr);
                if (chunk.done) {
                  onDone();
                  return;
                }
                if (chunk.token) {
                  onToken(chunk.token);
                }
              } catch {
                // skip malformed chunk
              }
            }
          }
        }
        return pump();
      });

      return pump();
    }).catch(err => {
      console.error('SSE stream error:', err);
      onDone();
    });
    });
  }
}
