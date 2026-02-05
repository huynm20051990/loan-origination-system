package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditCheckedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // This will be the Application ID
    String applicationNumber, // Added to help downstream services identify the loan
    int creditScore,
    String riskTier, // Changed from 'decision' to match our ScoringDomainService
    LocalDateTime createdAt)
    implements DomainEvent {

  /** Factory method for the Credit Service to easily create this event. */
  public static CreditCheckedEvent of(
      String aggregateType, String aggregateId, String applicationNumber, int score, String tier) {
    return new CreditCheckedEvent(
        UUID.randomUUID(),
        aggregateType,
        aggregateId,
        applicationNumber,
        score,
        tier,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.CREDIT_CHECKED;
  }
}
