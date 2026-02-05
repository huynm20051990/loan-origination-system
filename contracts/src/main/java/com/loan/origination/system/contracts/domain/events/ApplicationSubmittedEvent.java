package com.loan.origination.system.contracts.domain.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationSubmittedEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId, // This is the Application ID as a String
    String applicationNumber,
    String customerEmail,
    BigDecimal loanAmount,
    String ssn,
    LocalDateTime createdAt)
    implements DomainEvent {

  /** Factory method to create the event. Maps the incoming parameters to the record components. */
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

  /**
   * Implementation of DomainEvent interface. Returns the specific enum value for this event type.
   */
  @Override
  public EventType eventType() {
    return EventType.APPLICATION_SUBMITTED;
  }
}
