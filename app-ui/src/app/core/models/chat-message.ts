export interface ChatMessage {
  messageId: string;
  role: 'USER' | 'ASSISTANT';
  content: string;
  timestamp: string;
}
