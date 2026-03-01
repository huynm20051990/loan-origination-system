package com.loan.origination.system.microservices.assessment.application.port.input;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;

public interface ProcessAssessmentUseCase {
  void process(ApplicationSubmittedEvent event);
}
