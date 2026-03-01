package com.loan.origination.system.microservices.assessment.application.port.output;

import com.loan.origination.system.microservices.assessment.domain.model.Assessment;

public interface AssessmentRepositoryPort {
  void save(Assessment assessment);
}
