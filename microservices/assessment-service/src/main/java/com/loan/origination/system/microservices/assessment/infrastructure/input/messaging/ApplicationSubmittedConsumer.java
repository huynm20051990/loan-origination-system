package com.loan.origination.system.microservices.assessment.infrastructure.input.messaging;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.microservices.assessment.application.port.input.ProcessAssessmentUseCase;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationSubmittedConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationSubmittedConsumer.class);
  private final ProcessAssessmentUseCase processAssessmentUseCase;

  public ApplicationSubmittedConsumer(ProcessAssessmentUseCase processAssessmentUseCase) {
    this.processAssessmentUseCase = processAssessmentUseCase;
  }

  /**
   * This bean is picked up by Spring Cloud Stream. It listens to the 'loan-application-submitted'
   * topic defined in application.yml.
   */
  @Bean
  public Consumer<ApplicationSubmittedEvent> consumeApplicationSubmittedEvent() {
    return processAssessmentUseCase::process;
  }
}
