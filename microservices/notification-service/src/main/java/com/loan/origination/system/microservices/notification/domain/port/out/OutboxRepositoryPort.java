package com.loan.origination.system.microservices.notification.domain.port.out;

import com.loan.origination.system.contracts.domain.events.DomainEvent;

public interface OutboxRepositoryPort {
  void save(DomainEvent event);
}
