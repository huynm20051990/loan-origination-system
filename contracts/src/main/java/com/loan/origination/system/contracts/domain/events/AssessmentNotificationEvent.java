package com.loan.origination.system.contracts.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentNotificationEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // Application UUID
    String applicationNumber,
    String decision, // e.g., APPROVED, DECLINED, REFERRED
    String decisionReason, // e.g., "High DTI ratio", "Excellent Credit"
    BigDecimal approvedAmount,
    LocalDateTime createdAt)
    implements DomainEvent {

  public static AssessmentNotificationEvent of(
      String aggregateId,
      String applicationNumber,
      String decision,
      String reason,
      BigDecimal amount) {
    return new AssessmentNotificationEvent(
        UUID.randomUUID(),
        EventType.ASSESSMENT_NOTIFICATION.getTopicSuffix(), // Aggregate Type
        aggregateId,
        applicationNumber,
        decision,
        reason,
        amount,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.ASSESSMENT_NOTIFICATION; // Ensure this exists in your EventType enum
  }
}
