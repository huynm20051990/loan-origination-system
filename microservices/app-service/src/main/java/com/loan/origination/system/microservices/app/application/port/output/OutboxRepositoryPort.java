package com.loan.origination.system.microservices.app.application.port.output;

import com.loan.origination.system.contracts.domain.events.DomainEvent;

public interface OutboxRepositoryPort {
  /**
   * Persists the outbox event. In the adapter implementation, this must happen in the same
   * transaction as the LoanApplication save.
   */
  void save(DomainEvent event);
}
