package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditCheckedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId,
    int creditScore,
    String decision, // e.g., APPROVED, REJECTED, MANUAL_REVIEW
    LocalDateTime createdAt)
    implements DomainEvent {
  @Override
  public EventType eventType() {
    return EventType.CREDIT_CHECKED;
  }
}
