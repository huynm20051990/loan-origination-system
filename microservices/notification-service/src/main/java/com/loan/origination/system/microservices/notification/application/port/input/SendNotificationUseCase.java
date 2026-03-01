package com.loan.origination.system.microservices.notification.application.port.input;

import java.util.UUID;

public interface SendNotificationUseCase {

  void sendNotification(UUID applicationId, String applicationNumber, String email);
}
