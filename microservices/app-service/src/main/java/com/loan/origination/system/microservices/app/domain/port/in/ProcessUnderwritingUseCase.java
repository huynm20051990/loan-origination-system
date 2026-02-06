package com.loan.origination.system.microservices.app.domain.port.in;

import com.loan.origination.system.contracts.domain.events.DomainEvent;

public interface ProcessUnderwritingUseCase {

  /**
   * Executes the underwriting engine logic. * @param appId The unique identifier of the loan
   * application.
   *
   * @param event
   */
  void execute(DomainEvent event);
}
