package com.loan.origination.system.microservices.credit.adapter.out.persistence.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.credit.adapter.out.persistence.entity.CreditReportEntity;
import com.loan.origination.system.microservices.credit.adapter.out.persistence.entity.OutboxEntity;
import com.loan.origination.system.microservices.credit.domain.model.CreditReport;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreditPersistenceMapper {

  private final ObjectMapper objectMapper;

  public CreditPersistenceMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // This was the missing piece causing your compile error
  public CreditReportEntity toEntity(CreditReport domain, String aggregateId) {
    CreditReportEntity entity = new CreditReportEntity();

    entity.setId(domain.id());
    // Converting String aggregateId from the event into the UUID required by the entity
    entity.setApplicationId(UUID.fromString(aggregateId));
    entity.setApplicationNumber(domain.applicationNumber());
    entity.setSsnHash(domain.ssn()); // Assuming SSN is hashed or handled
    entity.setCreditScore(domain.creditScore());
    entity.setRiskTier(domain.riskTier());
    entity.setCheckedAt(domain.checkedAt());

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
