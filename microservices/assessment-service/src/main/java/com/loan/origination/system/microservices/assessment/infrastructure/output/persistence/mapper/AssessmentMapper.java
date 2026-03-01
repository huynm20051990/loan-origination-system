package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import com.loan.origination.system.microservices.assessment.domain.vo.AssessmentStatus;
import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.entity.AssessmentEntity;
import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.entity.OutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class AssessmentMapper {

  private final ObjectMapper objectMapper;

  public AssessmentMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public AssessmentEntity toEntity(Assessment assessment) {
    if (assessment == null) {
      return null;
    }

    AssessmentEntity entity = new AssessmentEntity();
    entity.setId(assessment.getId());
    entity.setApplicationId(assessment.getApplicationId());
    entity.setStatus(assessment.getStatus().name());
    entity.setDecision(assessment.getDecision());

    return entity;
  }

  public Assessment toDomain(AssessmentEntity entity) {
    if (entity == null) {
      return null;
    }
    Assessment assessment = new Assessment(entity.getApplicationId());
    assessment.updateStatus(AssessmentStatus.valueOf(entity.getStatus()));
    return assessment;
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
