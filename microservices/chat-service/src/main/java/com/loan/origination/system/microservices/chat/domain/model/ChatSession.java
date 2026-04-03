package com.loan.origination.system.microservices.chat.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ChatSession {

  private final UUID id;
  private final String userId;
  private final Instant createdAt;

  public ChatSession(UUID id, String userId, Instant createdAt) {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("userId must not be blank");
    }
    this.id = id;
    this.userId = userId;
    this.createdAt = createdAt;
  }

  public boolean isOwnedBy(String requestingUserId) {
    return this.userId.equals(requestingUserId);
  }

  public UUID getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
