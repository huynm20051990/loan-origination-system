package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSentEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId,
    String applicationNumber,
    String channel, // e.g., EMAIL, SMS
    LocalDateTime createdAt)
    implements DomainEvent {

  public static NotificationSentEvent of(
      String aggregateId, String applicationNumber, String channel) {
    return new NotificationSentEvent(
        UUID.randomUUID(),
        EventType.NOTIFICATION_SENT.getTopicSuffix(),
        aggregateId,
        applicationNumber,
        channel,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.NOTIFICATION_SENT;
  }
}
