package com.loan.origination.system.microservices.notification.infrastructure.input.messaging;

import com.loan.origination.system.contracts.domain.events.EventType;
import com.loan.origination.system.contracts.domain.events.UnderwritingCompletedEvent;
import com.loan.origination.system.microservices.notification.application.port.input.SendNotificationUseCase;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ApplicationSubmissionConsumer {

  private static final Logger log = LoggerFactory.getLogger(ApplicationSubmissionConsumer.class);
  private final SendNotificationUseCase notificationUseCase;

  public ApplicationSubmissionConsumer(SendNotificationUseCase notificationUseCase) {
    this.notificationUseCase = notificationUseCase;
  }

  @Bean
  public Consumer<UnderwritingCompletedEvent> consumeUnderwritingCompleted() {
    return event -> {
      log.info("Received Credit Checked Event for Application: {}", event.applicationNumber());

      // 1. Validation/Filtering
      // We only trigger notifications if the event type matches what we expect
      if (!EventType.UNDERWRITING_COMPLETED.equals(event.eventType())) {
        log.debug("Skipping event type: {}", event.eventType());
        return;
      }

      try {
        // 2. Map and Execute
        // In a real app, the email might come from the event payload or a User Service
        String recipientEmail = "customer@example.com";

        notificationUseCase.sendCreditCheckNotification(
            UUID.fromString(event.aggregateId()), event.applicationNumber(), recipientEmail);

        log.info("Successfully processed notification request for {}", event.applicationNumber());

      } catch (Exception e) {
        log.error(
            "Failed to process notification for application {}: {}",
            event.applicationNumber(),
            e.getMessage());
        // Spring Cloud Stream will handle retries based on your YML config
      }
    };
  }
}
