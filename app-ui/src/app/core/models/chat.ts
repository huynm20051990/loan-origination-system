/**
 * Represents a single message in the chat conversation.
 *
 * @property role - Indicates whether the message was sent by the user or the AI assistant.
 * @property content - The text content of the message.
 * @property isStreaming - True while the assistant message is still receiving token chunks via SSE.
 */
export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  isStreaming: boolean;
}
