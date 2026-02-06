package com.loan.origination.system.microservices.notification.adapter.out.persistence;

import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.notification.adapter.out.persistence.entity.OutboxEntity;
import com.loan.origination.system.microservices.notification.adapter.out.persistence.mapper.NotificationPersistenceMapper;
import com.loan.origination.system.microservices.notification.domain.port.out.OutboxRepositoryPort;
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
