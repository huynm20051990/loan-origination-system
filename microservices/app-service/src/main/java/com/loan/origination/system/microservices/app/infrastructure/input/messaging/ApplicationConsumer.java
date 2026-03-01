package com.loan.origination.system.microservices.app.infrastructure.input.messaging;

import com.loan.origination.system.contracts.domain.events.*;
import com.loan.origination.system.microservices.app.application.port.input.LoanApplicationUseCase;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConsumer {

  private static final Logger log = LoggerFactory.getLogger(ApplicationConsumer.class);

  private final LoanApplicationUseCase loanApplicationUseCase;

  public ApplicationConsumer(LoanApplicationUseCase loanApplicationUseCase) {
    this.loanApplicationUseCase = loanApplicationUseCase;
  }

  @Bean
  public Consumer<AssessmentCompletedEvent> consumeAssessmentCompletedEvent() {
    return event -> {
      log.info("App Service received AssessmentCompletedEvent: " + event);

      if (!EventType.ASSESSMENT_COMPLETED.equals(event.eventType())) {
        return;
      }

      AssessmentNotifiedEvent assessmentNotifiedEvent =
          AssessmentNotifiedEvent.of(
              event.aggregateId(), event.aggregateType(), event.decision(), event.remarks(), null);
      try {
        loanApplicationUseCase.execute(assessmentNotifiedEvent);
        log.info("Successfully processed assessment result for {}", event.applicationNumber());
      } catch (Exception e) {
        log.error(
            "Error processing assessment result for {}: {}",
            event.applicationNumber(),
            e.getMessage());
        throw e;
      }
    };
  }

  /**
   * 2. Consumes feedback from the Notification Service. Logic: Notification Sent -> Update
   * Application record to 'Contacted'.
   */
  @Bean
  public Consumer<NotificationSentEvent> consumeNotificationSentEvent() {
    return event -> {
      log.info(
          "App Service received Notification Confirmation for App: {}", event.applicationNumber());

      if (!EventType.NOTIFICATION_SENT.equals(event.eventType())) {
        return;
      }

      try {
        // Marks the application in the DB as "Customer Notified"
        loanApplicationUseCase.markAsNotified(event);

        log.info("Application {} status updated to NOTIFIED", event.applicationNumber());
      } catch (Exception e) {
        log.error(
            "Error updating notification status for {}: {}",
            event.applicationNumber(),
            e.getMessage());
        throw e;
      }
    };
  }
}
