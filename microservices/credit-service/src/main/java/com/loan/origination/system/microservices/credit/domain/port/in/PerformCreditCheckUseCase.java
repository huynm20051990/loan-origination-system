package com.loan.origination.system.microservices.credit.domain.port.in;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;

public interface PerformCreditCheckUseCase {
  void process(ApplicationSubmittedEvent event);
}
