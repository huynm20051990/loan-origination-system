package com.loan.origination.system.microservices.notification.application.service;

import com.loan.origination.system.contracts.domain.events.NotificationSentEvent;
import com.loan.origination.system.microservices.notification.application.port.input.SendNotificationUseCase;
import com.loan.origination.system.microservices.notification.application.port.output.NotificationRepositoryPort;
import com.loan.origination.system.microservices.notification.application.port.output.NotificationSenderPort;
import com.loan.origination.system.microservices.notification.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.notification.domain.model.Notification;
import com.loan.origination.system.microservices.notification.domain.service.DomainNotificationService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationApplicationService implements SendNotificationUseCase {

  private final DomainNotificationService domainService;
  private final NotificationRepositoryPort repositoryPort;
  private final NotificationSenderPort senderPort;
  private final OutboxRepositoryPort outboxRepositoryPort;

  public NotificationApplicationService(
      DomainNotificationService domainService,
      NotificationRepositoryPort repositoryPort,
      NotificationSenderPort senderPort,
      OutboxRepositoryPort outboxRepositoryPort) {
    this.domainService = domainService;
    this.repositoryPort = repositoryPort;
    this.senderPort = senderPort;
    this.outboxRepositoryPort = outboxRepositoryPort;
  }

  @Override
  @Transactional
  public void sendNotification(UUID applicationId, String applicationNumber, String email) {

    // 1. Logic: Prepare the notification content
    Notification notification =
        domainService.prepareCreditCheckNotification(applicationNumber, email);

    // 2. Action: Send the actual email (External API)
    senderPort.send(notification);

    // 3. Persistence: Save to notification history
    repositoryPort.save(notification);

    // 4. Outbox: Record the event to be picked up by Debezium
    NotificationSentEvent event =
        NotificationSentEvent.of(
            applicationId, null, null, null, applicationNumber, notification.type().name());

    outboxRepositoryPort.save(event);
  }
}
