package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentCompletedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // Kafka Partition Key / Outbox Aggregate
    UUID applicationId, // Domain-specific Application ID
    String userId,
    String userRole,
    String conversationId,
    String applicationNumber,
    String decision,
    String remarks,
    LocalDateTime createdAt)
    implements DomainEvent {

  /** Factory method for the Credit Service to easily create this event with context. */
  public static AssessmentCompletedEvent of(
      UUID appId,
      String userId,
      String userRole,
      String conversationId,
      String applicationNumber,
      String decision,
      String remarks) {

    return new AssessmentCompletedEvent(
        UUID.randomUUID(),
        EventType.ASSESSMENT_COMPLETED.getTopicSuffix(),
        appId.toString(), // Map UUID to aggregateId String
        appId,
        userId,
        userRole,
        conversationId,
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
