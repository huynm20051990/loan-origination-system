package com.loan.origination.system.microservices.notification.domain.port.out;

import com.loan.origination.system.microservices.notification.domain.model.Notification;

public interface NotificationRepositoryPort {
  void save(Notification notification);
}
