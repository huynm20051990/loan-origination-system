package com.loan.origination.system.microservices.notification.infrastructure.input.messaging;

import com.loan.origination.system.contracts.domain.events.AssessmentNotificationEvent;
import com.loan.origination.system.contracts.domain.events.EventType;
import com.loan.origination.system.microservices.notification.application.port.input.SendNotificationUseCase;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSubmittedConsumer {

  private static final Logger log = LoggerFactory.getLogger(ApplicationSubmittedConsumer.class);
  private final SendNotificationUseCase notificationUseCase;

  public ApplicationSubmittedConsumer(SendNotificationUseCase notificationUseCase) {
    this.notificationUseCase = notificationUseCase;
  }

  @Bean
  public Consumer<AssessmentNotificationEvent> consumeAssessmentNotificationEvent() {
    return event -> {
      if (!EventType.ASSESSMENT_NOTIFICATION.equals(event.eventType())) {
        log.debug("Skipping event type: {}", event.eventType());
        return;
      }

      try {
        String recipientEmail = "customer@example.com";
        notificationUseCase.sendNotification(
            UUID.fromString(event.aggregateId()), event.applicationNumber(), recipientEmail);
        log.info("Successfully processed notification request for {}", event.applicationNumber());

      } catch (Exception e) {
        log.error(
            "Failed to process notification for application {}: {}",
            event.applicationNumber(),
            e.getMessage());
      }
    };
  }
}
