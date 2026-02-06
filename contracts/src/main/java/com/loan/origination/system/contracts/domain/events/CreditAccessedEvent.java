package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditAccessedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // This will be the Application ID
    String applicationNumber, // Added to help downstream services identify the loan
    int creditScore,
    String riskTier, // Changed from 'decision' to match our ScoringDomainService
    LocalDateTime createdAt)
    implements DomainEvent {

  /** Factory method for the Credit Service to easily create this event. */
  public static CreditAccessedEvent of(
      String aggregateId, String applicationNumber, int score, String tier) {
    return new CreditAccessedEvent(
        UUID.randomUUID(),
        EventType.CREDIT_ACCESSED.getTopicSuffix(),
        aggregateId,
        applicationNumber,
        score,
        tier,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.CREDIT_ACCESSED;
  }
}
