package com.loan.origination.system.microservices.credit.adapter.in.kafka;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.microservices.credit.domain.port.in.PerformCreditCheckUseCase;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationSubmissionConsumer {

  private static final Logger log = LoggerFactory.getLogger(ApplicationSubmissionConsumer.class);
  private final PerformCreditCheckUseCase creditCheckUseCase;

  public ApplicationSubmissionConsumer(PerformCreditCheckUseCase creditCheckUseCase) {
    this.creditCheckUseCase = creditCheckUseCase;
  }

  /**
   * This bean is picked up by Spring Cloud Stream. It listens to the 'loan-application-submitted'
   * topic defined in application.yml.
   */
  @Bean
  public Consumer<ApplicationSubmittedEvent> processApplicationSubmission() {
    return event -> {
      log.info("Received Loan Submission Event for Application: {}", event.applicationNumber());
      try {
        creditCheckUseCase.process(event);
      } catch (Exception e) {
        log.error(
            "Error processing credit check for application {}: {}",
            event.applicationNumber(),
            e.getMessage());
        // In a production scenario, you might throw this to trigger a retry/DLQ
        throw e;
      }
    };
  }
}
