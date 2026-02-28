package com.loan.origination.system.microservices.notification.infrastructure.output.persistence;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.notification.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.notification.infrastructure.output.persistence.entity.OutboxEntity;
import com.loan.origination.system.microservices.notification.infrastructure.output.persistence.mapper.NotificationPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class NotificationOutboxAdapter implements OutboxRepositoryPort {
  private final EntityManager entityManager;
  private final NotificationPersistenceMapper mapper;

  public NotificationOutboxAdapter(
      EntityManager entityManager, NotificationPersistenceMapper mapper) {
    this.entityManager = entityManager;
    this.mapper = mapper;
  }

  @Override
  public void save(DomainEvent event) {
    OutboxEntity outbox = mapper.toOutboxEntity(event);
    entityManager.persist(outbox); // Persists to the outbox table in the same transaction
  }
}
