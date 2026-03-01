package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence;

import com.loan.origination.system.microservices.assessment.application.port.output.AssessmentRepositoryPort;
import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.mapper.AssessmentMapper;
import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.repository.AssessmentRepository;

public class AssessmentPersistenceAdapter implements AssessmentRepositoryPort {

  private final AssessmentRepository assessmentRepository;
  private final AssessmentMapper assessmentMapper;

  public AssessmentPersistenceAdapter(
      AssessmentRepository assessmentRepository, AssessmentMapper assessmentMapper) {
    this.assessmentRepository = assessmentRepository;
    this.assessmentMapper = assessmentMapper;
  }

  @Override
  public void save(Assessment assessment) {
    assessmentRepository.save(assessmentMapper.toEntity(assessment));
  }
}
