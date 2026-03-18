package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSentEvent(
    UUID eventId,
    String aggregateType,
    String aggregateId,
    UUID applicationId,
    String userId,
    String userRole,
    String conversationId,
    String applicationNumber,
    String channel, // e.g., EMAIL, SMS, WEBSOCKET
    LocalDateTime createdAt)
    implements DomainEvent {

  public static NotificationSentEvent of(
      UUID appId,
      String userId,
      String userRole,
      String conversationId,
      String applicationNumber,
      String channel) {

    return new NotificationSentEvent(
        UUID.randomUUID(),
        EventType.NOTIFICATION_SENT.getTopicSuffix(),
        appId.toString(),
        appId,
        userId,
        userRole,
        conversationId,
        applicationNumber,
        channel,
        LocalDateTime.now());
  }

  @Override
  public EventType eventType() {
    return EventType.NOTIFICATION_SENT;
  }
}
