package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.mapper;

import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import com.loan.origination.system.microservices.assessment.domain.vo.AssessmentStatus;
import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.entity.AssessmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AssessmentMapper {

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
}
