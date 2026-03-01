package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentCompletedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // This will be the Application ID
    String applicationNumber, // Added to help downstream services identify the loan
    String decision,
    String remarks, // Changed from 'decision' to match our ScoringDomainService
    LocalDateTime createdAt)
    implements DomainEvent {

  /** Factory method for the Credit Service to easily create this event. */
  public static AssessmentCompletedEvent of(
      String aggregateId, String applicationNumber, String decision, String remarks) {
    return new AssessmentCompletedEvent(
        UUID.randomUUID(),
        EventType.ASSESSMENT_COMPLETED.getTopicSuffix(),
        aggregateId,
        applicationNumber,
        decision,
        remarks,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.ASSESSMENT_COMPLETED;
  }
}
