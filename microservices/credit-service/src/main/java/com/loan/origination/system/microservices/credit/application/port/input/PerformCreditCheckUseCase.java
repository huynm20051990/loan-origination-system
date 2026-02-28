package com.loan.origination.system.microservices.credit.application.port.input;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;

public interface PerformCreditCheckUseCase {
  void process(ApplicationSubmittedEvent event);
}
