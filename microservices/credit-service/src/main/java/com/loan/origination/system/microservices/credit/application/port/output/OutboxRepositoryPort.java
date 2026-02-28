package com.loan.origination.system.microservices.credit.application.port.output;

import com.loan.origination.system.contracts.domain.events.DomainEvent;

public interface OutboxRepositoryPort {
  void save(DomainEvent event);
}
