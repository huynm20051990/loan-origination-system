package com.loan.origination.system.contracts.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UnderwritingCompletedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // Application UUID
    String applicationNumber,
    String decision, // e.g., APPROVED, DECLINED, REFERRED
    String decisionReason, // e.g., "High DTI ratio", "Excellent Credit"
    BigDecimal approvedAmount,
    LocalDateTime createdAt)
    implements DomainEvent {

  public static UnderwritingCompletedEvent of(
      String aggregateId,
      String applicationNumber,
      String decision,
      String reason,
      BigDecimal amount) {
    return new UnderwritingCompletedEvent(
        UUID.randomUUID(),
        EventType.UNDERWRITING_COMPLETED.getTopicSuffix(), // Aggregate Type
        aggregateId,
        applicationNumber,
        decision,
        reason,
        amount,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.UNDERWRITING_COMPLETED; // Ensure this exists in your EventType enum
  }
}
