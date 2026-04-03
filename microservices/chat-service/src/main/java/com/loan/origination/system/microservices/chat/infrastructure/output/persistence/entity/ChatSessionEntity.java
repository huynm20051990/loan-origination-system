package com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
public class ChatSessionEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected ChatSessionEntity() {}

  public ChatSessionEntity(UUID id, String userId, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public String getUserId() { return userId; }
  public Instant getCreatedAt() { return createdAt; }
}
