package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowerNotifiedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId,
    String channel, // e.g., EMAIL, SMS
    String templateUsed,
    LocalDateTime createdAt)
    implements DomainEvent {
  @Override
  public EventType eventType() {
    return EventType.BORROWER_NOTIFIED;
  }
}
