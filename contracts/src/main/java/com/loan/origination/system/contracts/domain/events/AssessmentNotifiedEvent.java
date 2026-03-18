package com.loan.origination.system.contracts.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentNotifiedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId,
    UUID applicationId,
    String userId,
    String userRole,
    String conversationId,
    String applicationNumber,
    String decision,
    String decisionReason,
    BigDecimal approvedAmount,
    LocalDateTime createdAt)
    implements DomainEvent {

  public static AssessmentNotifiedEvent of(
      UUID appId,
      String userId,
      String userRole,
      String conversationId,
      String applicationNumber,
      String decision,
      String reason,
      BigDecimal amount) {

    return new AssessmentNotifiedEvent(
        UUID.randomUUID(),
        EventType.ASSESSMENT_NOTIFIED.getTopicSuffix(),
        appId.toString(),
        appId,
        userId,
        userRole,
        conversationId,
        applicationNumber,
        decision,
        reason,
        amount,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.ASSESSMENT_NOTIFIED;
  }
}
