package com.loan.origination.system.microservices.notification.application.port.output;

import com.loan.origination.system.microservices.notification.domain.model.Notification;

public interface NotificationSenderPort {
  void send(Notification notification);
}
