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

  /**
   * 1. Consumes results from the Credit Service. Logic: Credit Result -> Run Underwriting -> Save
   * to Outbox.
   */
  @Bean
  public Consumer<CreditAccessedEvent> consumeCreditAssessed() {
    return event -> {
      log.info(
          "App Service received Credit Result for App: {} | Score: {}",
          event.applicationNumber(),
          event.creditScore());

      if (!EventType.CREDIT_ACCESSED.equals(event.eventType())) {
        return;
      }

      try {
        // This UseCase should handle the logic for "What happens next?"
        // Usually: Update DB status and determine if it moves to Underwriting.

        DomainEvent underwritingCompletedEvent =
            UnderwritingCompletedEvent.of(
                event.aggregateId(), event.applicationNumber(), null, null, null);
        loanApplicationUseCase.execute(underwritingCompletedEvent);

        log.info("Successfully processed credit result for {}", event.applicationNumber());
      } catch (Exception e) {
        log.error(
            "Error processing credit result for {}: {}", event.applicationNumber(), e.getMessage());
        throw e;
      }
    };
  }

  /**
   * 2. Consumes feedback from the Notification Service. Logic: Notification Sent -> Update
   * Application record to 'Contacted'.
   */
  @Bean
  public Consumer<NotificationSentEvent> consumeNotificationSent() {
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
