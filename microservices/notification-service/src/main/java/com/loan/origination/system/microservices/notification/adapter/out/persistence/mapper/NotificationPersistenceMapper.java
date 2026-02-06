package com.loan.origination.system.microservices.notification.adapter.out.persistence.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.notification.adapter.out.persistence.entity.NotificationEntity;
import com.loan.origination.system.microservices.notification.adapter.out.persistence.entity.OutboxEntity;
import com.loan.origination.system.microservices.notification.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {
  private final ObjectMapper objectMapper;

  public NotificationPersistenceMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public NotificationEntity toEntity(Notification domain) {
    NotificationEntity entity = new NotificationEntity();
    entity.setId(domain.id());
    entity.setApplicationNumber(domain.applicationNumber());
    entity.setRecipientIdentifier(domain.recipientIdentifier());
    entity.setType(domain.type());
    entity.setSubject(domain.subject());
    entity.setContent(domain.content());
    entity.setCreatedAt(domain.createdAt());
    return entity;
  }

  public OutboxEntity toOutboxEntity(DomainEvent event) {
    JsonNode payload = objectMapper.valueToTree(event);
    OutboxEntity entity = new OutboxEntity();
    entity.setId(event.eventId());
    entity.setAggregateType(event.aggregateType());
    entity.setAggregateId(event.aggregateId());
    entity.setType(event.eventType().name());
    entity.setPayload(payload);
    entity.setCreatedAt(event.createdAt());
    return entity;
  }
}
