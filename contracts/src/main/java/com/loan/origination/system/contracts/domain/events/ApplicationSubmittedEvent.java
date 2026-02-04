package com.loan.origination.system.contracts.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationSubmittedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // This will be the Application ID
    String applicationNumber,
    String customerEmail,
    BigDecimal loanAmount,
    String ssn,
    LocalDateTime createdAt)
    implements DomainEvent {

  public static ApplicationSubmittedEvent of(
      String aggregateType,
      UUID appId,
      String appNum,
      String email,
      BigDecimal amount,
      String ssn) {
    return new ApplicationSubmittedEvent(
        UUID.randomUUID(),
        aggregateType,
        appId.toString(),
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
