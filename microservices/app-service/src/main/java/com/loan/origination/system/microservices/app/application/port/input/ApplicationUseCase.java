package com.loan.origination.system.microservices.app.application.port.input;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import java.math.BigDecimal;
import java.util.UUID;

public interface ApplicationUseCase {
  void execute(DomainEvent event);

  Application submit(UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose);

  void startAssessment(UUID applicationId);

  void updateStatus(UUID applicationId, String status, String comment);

  void markAsNotified(DomainEvent event);

  void delete(UUID id);
}
