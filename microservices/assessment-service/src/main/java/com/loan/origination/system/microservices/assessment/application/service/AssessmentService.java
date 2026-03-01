package com.loan.origination.system.microservices.assessment.application.service;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.contracts.domain.events.AssessmentCompletedEvent;
import com.loan.origination.system.microservices.assessment.application.port.input.ProcessAssessmentUseCase;
import com.loan.origination.system.microservices.assessment.application.port.output.AssessmentRepositoryPort;
import com.loan.origination.system.microservices.assessment.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AssessmentService implements ProcessAssessmentUseCase {

  private final AssessmentRepositoryPort assessmentRepository;
  private final OutboxRepositoryPort outboxRepository;

  public AssessmentService(
      AssessmentRepositoryPort assessmentRepository, OutboxRepositoryPort outboxRepository) {
    this.assessmentRepository = assessmentRepository;
    this.outboxRepository = outboxRepository;
  }

  @Override
  public void process(ApplicationSubmittedEvent event) {
    // Currently, only make mock data for 1 to 7
    // 1. call external API for identity check,
    // 2. Call external API for credit report,
    // 3. Call external API for property evaluation,
    // 4. Call external API for income and employment history,
    // 5. Call external API financial data.
    // 6. Underwriting.
    // 7. Decision.
    // 8. Assessment result
    // Save Assessment result
    // Save data to outbox table
    Assessment assessment = new Assessment(UUID.fromString(event.aggregateId()));
    assessment.recordDecision("APPROVED", "Hardcoded mock approval");
    assessmentRepository.save(assessment);

    AssessmentCompletedEvent assessmentCompletedEvent =
        AssessmentCompletedEvent.of(
            event.aggregateId(),
            event.applicationNumber(),
            assessment.getDecision(),
            assessment.getRemarks());
    outboxRepository.save(assessmentCompletedEvent);
  }
}
