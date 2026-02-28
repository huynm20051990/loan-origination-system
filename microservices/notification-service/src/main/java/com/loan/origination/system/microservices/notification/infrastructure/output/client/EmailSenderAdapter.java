package com.loan.origination.system.microservices.notification.infrastructure.output.client;

import com.loan.origination.system.microservices.notification.application.port.output.NotificationSenderPort;
import com.loan.origination.system.microservices.notification.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderAdapter implements NotificationSenderPort {

  private static final Logger log = LoggerFactory.getLogger(EmailSenderAdapter.class);

  @Override
  public void send(Notification notification) {
    // In a real scenario, this is where you'd call your Email API (e.g., SendGrid)
    log.info("----------------------------------------------------------");
    log.info("SENDING EMAIL VIA EXTERNAL PROVIDER:");
    log.info("TO:      {}", notification.recipientIdentifier());
    log.info("SUBJECT: {}", notification.subject());
    log.info("BODY:    {}", notification.content());
    log.info("STATUS:  SUCCESS (Simulated)");
    log.info("----------------------------------------------------------");

    // Example of where an exception might occur:
    // if (apiResponse.getStatusCode() != 200) {
    //     throw new RuntimeException("Email provider is down!");
    // }
  }
}
