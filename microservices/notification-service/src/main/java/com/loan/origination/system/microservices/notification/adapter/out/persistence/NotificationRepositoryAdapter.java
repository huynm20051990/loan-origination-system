package com.loan.origination.system.microservices.notification.adapter.out.persistence;

import com.loan.origination.system.microservices.notification.adapter.out.persistence.mapper.NotificationPersistenceMapper;
import com.loan.origination.system.microservices.notification.adapter.out.persistence.repository.NotificationRepository;
import com.loan.origination.system.microservices.notification.domain.model.Notification;
import com.loan.origination.system.microservices.notification.domain.port.out.NotificationRepositoryPort;
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
