package com.loan.origination.system.microservices.credit.adapter.out.persistence;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.credit.adapter.out.persistence.mapper.CreditPersistenceMapper;
import com.loan.origination.system.microservices.credit.adapter.out.persistence.repository.OutboxRepository;
import com.loan.origination.system.microservices.credit.domain.port.out.OutboxRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class OutboxPersistenceAdapter implements OutboxRepositoryPort {

  private final OutboxRepository repository;
  private final CreditPersistenceMapper mapper;

  public OutboxPersistenceAdapter(OutboxRepository repository, CreditPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void save(DomainEvent event) {
    repository.save(mapper.toOutboxEntity(event));
  }
}
