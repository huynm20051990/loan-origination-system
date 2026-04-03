package com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {

  public enum Role { USER, ASSISTANT }

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(name = "session_id", nullable = false)
  private UUID sessionId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private Instant timestamp;

  protected ChatMessageEntity() {}

  public ChatMessageEntity(UUID id, UUID sessionId, Role role, String content, Instant timestamp) {
    this.id = id;
    this.sessionId = sessionId;
    this.role = role;
    this.content = content;
    this.timestamp = timestamp;
  }

  public UUID getId() { return id; }
  public UUID getSessionId() { return sessionId; }
  public Role getRole() { return role; }
  public String getContent() { return content; }
  public Instant getTimestamp() { return timestamp; }
}
