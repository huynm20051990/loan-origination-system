package com.loan.origination.system.microservices.notification.domain.port.in;

import java.util.UUID;

public interface SendNotificationUseCase {

  // Command object or direct parameters to trigger the notification flow
  void sendCreditCheckNotification(UUID applicationId, String applicationNumber, String email);
}
