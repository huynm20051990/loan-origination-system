package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.assessment.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.repository.OutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxPersistenceAdapter implements OutboxRepositoryPort {

  private final OutboxRepository repository;

  public OutboxPersistenceAdapter(OutboxRepository repository) {
    this.repository = repository;
  }

  @Override
  public void save(DomainEvent event) {}
}
