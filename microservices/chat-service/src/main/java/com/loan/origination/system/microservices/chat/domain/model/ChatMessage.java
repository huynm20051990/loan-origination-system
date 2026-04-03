package com.loan.origination.system.microservices.chat.domain.model;

import com.loan.origination.system.microservices.chat.domain.vo.MessageRole;
import java.time.Instant;
import java.util.UUID;

public class ChatMessage {

  private final UUID id;
  private final UUID sessionId;
  private final MessageRole role;
  private final String content;
  private final Instant timestamp;

  public ChatMessage(UUID id, UUID sessionId, MessageRole role, String content, Instant timestamp) {
    if (content == null) {
      throw new IllegalArgumentException("content must not be null");
    }
    if (role == null) {
      throw new IllegalArgumentException("role must not be null");
    }
    this.id = id;
    this.sessionId = sessionId;
    this.role = role;
    this.content = content;
    this.timestamp = timestamp;
  }

  public UUID getId() {
    return id;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public MessageRole getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }

  public Instant getTimestamp() {
    return timestamp;
  }
}
