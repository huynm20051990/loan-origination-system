package com.loan.origination.system.contracts.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationSubmittedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId,
    UUID applicationId,
    String userId,
    String userRole,
    String conversationId,
    String applicationNumber,
    String customerEmail,
    BigDecimal loanAmount,
    String ssn,
    LocalDateTime createdAt)
    implements DomainEvent {

  public static ApplicationSubmittedEvent of(
      UUID appId,
      String userId,
      String userRole,
      String conversationId,
      String appNum,
      String email,
      BigDecimal amount,
      String ssn) {

    return new ApplicationSubmittedEvent(
        UUID.randomUUID(),
        EventType.APPLICATION_SUBMITTED.getTopicSuffix(),
        appId.toString(),
        appId,
        userId,
        userRole,
        conversationId,
        appNum,
        email,
        amount,
        ssn,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.APPLICATION_SUBMITTED;
  }
}
