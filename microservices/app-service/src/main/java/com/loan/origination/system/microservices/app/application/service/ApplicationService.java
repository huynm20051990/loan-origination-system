package com.loan.origination.system.microservices.app.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.app.application.port.input.ApplicationUseCase;
import com.loan.origination.system.microservices.app.application.port.output.ApplicationRepositoryPort;
import com.loan.origination.system.microservices.app.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import com.loan.origination.system.microservices.app.domain.service.DomainApplicationService;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService implements ApplicationUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);

  private final DomainApplicationService loanDomainService;
  private final ApplicationRepositoryPort applicationRepository;
  private final OutboxRepositoryPort outboxRepository;

  public ApplicationService(
      DomainApplicationService loanDomainService,
      ApplicationRepositoryPort applicationRepository,
      OutboxRepositoryPort outboxRepository,
      ObjectMapper objectMapper) {
    this.loanDomainService = loanDomainService;
    this.applicationRepository = applicationRepository;
    this.outboxRepository = outboxRepository;
  } // For JSON conversion

  @Override
  @Transactional
  public Application submit(
      UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose) {

    // 1. Use Domain Service to create the Loan Application aggregate
    Application application =
        loanDomainService.initiateApplication(homeId, borrower, loanAmount, loanPurpose);

    // 2. Persist the Loan Application
    applicationRepository.save(application);

    return application;
  }

  @Override
  public void startAssessment(UUID applicationId) {
    applicationRepository
        .findById(applicationId)
        .ifPresentOrElse(
            app -> {
              // Create the event using the data from the retrieved application
              ApplicationSubmittedEvent event =
                  ApplicationSubmittedEvent.of(
                      app.getId(),
                      app.getApplicationNumber(),
                      app.getBorrower().email(),
                      app.getLoanAmount(),
                      app.getBorrower().ssn());

              // Persist to Outbox - the Outbox Poller will handle the Kafka dispatch
              outboxRepository.save(event);

              LOG.info(
                  "Assessment triggered for Application ID: {} (Number: {})",
                  applicationId,
                  app.getApplicationNumber());
            },
            () -> {
              throw new RuntimeException(
                  "Cannot start assessment: Application not found with ID " + applicationId);
            });
  }

  @Override
  public void execute(DomainEvent event) {
    outboxRepository.save(event);
  }

  @Override
  public void markAsNotified(DomainEvent event) {}

  @Override
  public void delete(UUID id) {
    applicationRepository.deleteById(id);
  }
}
