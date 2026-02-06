package com.loan.origination.system.microservices.notification.adapter.out.persistence.entity;

import com.loan.origination.system.microservices.notification.domain.model.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

  @Id private UUID id;

  @Column(name = "application_number", nullable = false)
  private String applicationNumber;

  @Column(name = "recipient_identifier", nullable = false)
  private String recipientIdentifier;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType type;

  @Column(name = "subject", nullable = false)
  private String subject;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  // Standard Default Constructor for JPA
  public NotificationEntity() {}

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public void setApplicationNumber(String applicationNumber) {
    this.applicationNumber = applicationNumber;
  }

  public String getRecipientIdentifier() {
    return recipientIdentifier;
  }

  public void setRecipientIdentifier(String recipientIdentifier) {
    this.recipientIdentifier = recipientIdentifier;
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
