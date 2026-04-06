import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  AfterViewChecked,
  Output,
  EventEmitter
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ChatService } from '../../core/services/chat';
import { ChatMessage } from '../../core/models/chat-message';

const RATE_LIMIT_COUNT = 5;
const RATE_LIMIT_WINDOW_MS = 10_000;
const COOLDOWN_SECONDS = 15;

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
    MatProgressSpinnerModule
  ],
  templateUrl: './chat-box.component.html',
  styleUrls: ['./chat-box.component.scss']
})
export class ChatBoxComponent implements OnInit, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  @Output() listingsReset = new EventEmitter<void>();
  @Output() searchRequested = new EventEmitter<string>();

  sessionId: string | null = null;
  messages: ChatMessage[] = [];
  userInput = '';
  isLoading = false;
  streamingContent = '';
  errorMessage = '';
  rateLimitWarning = false;
  rateLimitCooldown = false;
  cooldownSeconds = COOLDOWN_SECONDS;

  private messageSentTimestamps: number[] = [];
  private lastMessageContent = '';
  private cooldownInterval: ReturnType<typeof setInterval> | null = null;

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.chatService.createSession().subscribe({
      next: session => {
        this.sessionId = session.sessionId;
        this.loadHistory(session.sessionId);
      },
      error: () => {
        this.errorMessage = 'Unable to start chat session. Please refresh the page.';
      }
    });
  }

  private loadHistory(sessionId: string): void {
    this.chatService.getMessages(sessionId).subscribe({
      next: msgs => {
        this.messages = msgs;
      }
    });
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  sendMessage(): void {
    if (!this.userInput.trim() || !this.sessionId || this.isLoading || this.rateLimitCooldown) {
      return;
    }

    if (this.isRateLimited()) {
      this.startCooldown();
      return;
    }

    this.errorMessage = '';
    this.lastMessageContent = this.userInput;
    this.doSend(this.userInput);
  }

  retryLastMessage(): void {
    if (this.lastMessageContent) {
      this.errorMessage = '';
      this.doSend(this.lastMessageContent);
    }
  }

  clearSession(): void {
    if (!this.sessionId) return;
    this.chatService.deleteSession(this.sessionId).subscribe({
      next: () => {
        this.messages = [];
        this.streamingContent = '';
        this.errorMessage = '';
        this.listingsReset.emit();
        this.chatService.createSession().subscribe(session => {
          this.sessionId = session.sessionId;
        });
      }
    });
  }

  private doSend(content: string): void {
    if (!this.sessionId) return;

    this.messageSentTimestamps.push(Date.now());
    this.messages.push({
      messageId: crypto.randomUUID(),
      role: 'USER',
      content,
      timestamp: new Date().toISOString()
    });
    this.userInput = '';
    this.isLoading = true;
    this.streamingContent = '';
    this.searchRequested.emit(content);

    setTimeout(() => {
      this.messages.push({
        messageId: crypto.randomUUID(),
        role: 'ASSISTANT',
        content: 'The listings have been updated based on your query. Please review the results!',
        timestamp: new Date().toISOString()
      });
      this.isLoading = false;
    }, 300);
  }

  private isRateLimited(): boolean {
    const now = Date.now();
    this.messageSentTimestamps = this.messageSentTimestamps.filter(
      t => now - t < RATE_LIMIT_WINDOW_MS
    );
    return this.messageSentTimestamps.length >= RATE_LIMIT_COUNT;
  }

  private startCooldown(): void {
    this.rateLimitWarning = true;
    this.rateLimitCooldown = true;
    this.cooldownSeconds = COOLDOWN_SECONDS;

    this.cooldownInterval = setInterval(() => {
      this.cooldownSeconds--;
      if (this.cooldownSeconds <= 0) {
        clearInterval(this.cooldownInterval!);
        this.rateLimitWarning = false;
        this.rateLimitCooldown = false;
        this.messageSentTimestamps = [];
      }
    }, 1000);
  }

  private scrollToBottom(): void {
    try {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop =
          this.messagesContainer.nativeElement.scrollHeight;
      }
    } catch {
      // ignore
    }
  }
}
