package com.loan.origination.system.microservices.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Notification(
    UUID id,
    String applicationNumber,
    String recipientIdentifier, // Email or Phone Number
    NotificationType type,
    String subject,
    String content,
    LocalDateTime createdAt) {
  public static Notification create(
      String appNumber, String recipient, NotificationType type, String subject, String content) {
    return new Notification(
        UUID.randomUUID(), appNumber, recipient, type, subject, content, LocalDateTime.now());
  }
}
