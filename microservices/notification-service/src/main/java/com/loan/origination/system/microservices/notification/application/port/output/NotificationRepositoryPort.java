package com.loan.origination.system.microservices.notification.application.port.output;

import com.loan.origination.system.microservices.notification.domain.model.Notification;

public interface NotificationRepositoryPort {
  void save(Notification notification);
}
