package com.loan.origination.system.microservices.notification.infrastructure.output.persistence;

import com.loan.origination.system.microservices.notification.application.port.output.NotificationRepositoryPort;
import com.loan.origination.system.microservices.notification.domain.model.Notification;
import com.loan.origination.system.microservices.notification.infrastructure.output.persistence.mapper.NotificationPersistenceMapper;
import com.loan.origination.system.microservices.notification.infrastructure.output.persistence.repository.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {
  private final NotificationRepository repository;
  private final NotificationPersistenceMapper mapper;

  public NotificationRepositoryAdapter(
      NotificationRepository repository, NotificationPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void save(Notification notification) {
    repository.save(mapper.toEntity(notification));
  }
}
