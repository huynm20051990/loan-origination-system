package com.loan.origination.system.microservices.app.application.port.input;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import java.math.BigDecimal;
import java.util.UUID;

public interface LoanApplicationUseCase {
  void execute(DomainEvent event);

  Application submit(UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose);

  void markAsNotified(DomainEvent event);
}
