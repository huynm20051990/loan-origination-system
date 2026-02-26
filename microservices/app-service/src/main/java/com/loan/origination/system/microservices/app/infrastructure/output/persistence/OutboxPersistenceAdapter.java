package com.loan.origination.system.microservices.app.infrastructure.output.persistence;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.app.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.app.infrastructure.output.persistence.mapper.ApplicationPersistenceMapper;
import com.loan.origination.system.microservices.app.infrastructure.output.persistence.repository.OutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxPersistenceAdapter implements OutboxRepositoryPort {

  private final OutboxRepository repository;
  private final ApplicationPersistenceMapper mapper;

  public OutboxPersistenceAdapter(
      OutboxRepository repository, ApplicationPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void save(DomainEvent event) {
    repository.save(mapper.toOutboxEntity(event));
  }
}
