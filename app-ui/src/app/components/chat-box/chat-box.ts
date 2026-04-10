import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subscription } from 'rxjs';
import { ChatMessage } from '../../core/models/chat';
import { ChatService } from '../../core/services/chat';
import { HomeSearchStateService } from '../../core/services/home-search-state';
import { Home } from '../../core/models/home';

/**
 * Sidebar chat component that streams natural-language home queries to the chat-service
 * and reactively updates the listings panel via {@link HomeSearchStateService}.
 *
 * <p>Emits a user message immediately on submit, then appends an assistant message that is
 * built incrementally from incoming {@code token} SSE events. On a {@code listings} event
 * the component delegates home data to {@link HomeSearchStateService} so the listings panel
 * updates without a page reload.
 */
@Component({
  selector: 'app-chat-box',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './chat-box.html',
  styleUrls: ['./chat-box.scss'],
})
export class ChatBoxComponent {
  /** Ordered list of chat messages rendered in the message list. */
  readonly messages = signal<ChatMessage[]>([]);

  /** True while an SSE stream is in progress; disables the submit button. */
  readonly isLoading = signal(false);

  /** Controls whether the side panel is visible. */
  readonly isOpen = signal(false);

  /** Current value of the query input field (bound via ngModel). */
  queryInput = '';

  /**
   * Stable session identifier scoping the server-side chat memory for this browser session.
   * A new UUID is generated once per component instance.
   */
  private readonly sessionId: string = crypto.randomUUID();

  /** Active SSE subscription — held so it can be cancelled on error/done. */
  private activeStream: Subscription | null = null;

  constructor(
    private readonly chatService: ChatService,
    private readonly homeSearchStateService: HomeSearchStateService,
  ) {}

  /** Toggles the side panel open/closed. */
  toggle(): void {
    this.isOpen.update(v => !v);
  }

  /**
   * Submits the current query to the chat-service SSE endpoint.
   *
   * <p>Appends the user turn to the message list, creates a streaming assistant
   * placeholder, and wires the observable events:
   * <ul>
   *   <li>{@code listings} — parses the JSON home array and delegates to
   *       {@link HomeSearchStateService#updateHomes}.
   *   <li>{@code token} — concatenates the token onto the last assistant message.
   *   <li>{@code done} — marks streaming complete and re-enables the submit button.
   *   <li>{@code error} — appends an error notice and re-enables the submit button.
   * </ul>
   */
  onSubmit(): void {
    const query = this.queryInput.trim();
    if (!query || this.isLoading()) {
      return;
    }

    this.isLoading.set(true);
    this.queryInput = '';

    // Append user turn.
    this.messages.update((msgs) => [
      ...msgs,
      { role: 'user', content: query, isStreaming: false },
    ]);

    // Append streaming assistant placeholder.
    this.messages.update((msgs) => [
      ...msgs,
      { role: 'assistant', content: '', isStreaming: true },
    ]);

    this.activeStream = this.chatService.stream(this.sessionId, query).subscribe({
      next: (event) => {
        if (event.type === 'listings') {
          try {
            const homes: Home[] = JSON.parse(event.data);
            this.homeSearchStateService.updateHomes(homes);
          } catch {
            // Malformed listings payload — skip silently.
          }
        } else if (event.type === 'token') {
          this.appendToLastAssistantMessage(event.data);
        } else if (event.type === 'done') {
          this.finalizeAssistantMessage();
          this.isLoading.set(false);
        } else if (event.type === 'error') {
          this.appendToLastAssistantMessage(
            event.data ?? 'An error occurred. Please try again.',
          );
          this.finalizeAssistantMessage();
          this.isLoading.set(false);
        }
      },
      complete: () => {
        // Ensure loading is cleared if the observable completes without a done/error event.
        this.finalizeAssistantMessage();
        this.isLoading.set(false);
      },
      error: () => {
        this.appendToLastAssistantMessage('Connection error. Please try again.');
        this.finalizeAssistantMessage();
        this.isLoading.set(false);
      },
    });
  }

  /**
   * Resets the chat and listings panel to their default state.
   *
   * <p>Delegates to {@link HomeSearchStateService#reset} to reload the full unfiltered
   * listings, clears the message history, and resets the input field.
   */
  onReset(): void {
    this.activeStream?.unsubscribe();
    this.activeStream = null;
    this.homeSearchStateService.reset();
    this.messages.set([]);
    this.queryInput = '';
    this.isLoading.set(false);
  }

  /** Appends {@code text} to the content of the last message in the list. */
  private appendToLastAssistantMessage(text: string): void {
    this.messages.update((msgs) => {
      if (msgs.length === 0) {
        return msgs;
      }
      const updated = [...msgs];
      const last = updated[updated.length - 1];
      updated[updated.length - 1] = { ...last, content: last.content + text };
      return updated;
    });
  }

  /** Clears the {@code isStreaming} flag on the last message. */
  private finalizeAssistantMessage(): void {
    this.messages.update((msgs) => {
      if (msgs.length === 0) {
        return msgs;
      }
      const updated = [...msgs];
      const last = updated[updated.length - 1];
      updated[updated.length - 1] = { ...last, isStreaming: false };
      return updated;
    });
  }
}
