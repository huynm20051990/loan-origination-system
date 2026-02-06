package com.loan.origination.system.microservices.app.domain.port.in;

import com.loan.origination.system.contracts.domain.events.DomainEvent;

public interface UpdateNotificationStatusUseCase {

  /**
   * Marks the application as successfully notified. * @param appId The unique identifier of the
   * loan application.
   */
  void markAsNotified(DomainEvent event);
}
