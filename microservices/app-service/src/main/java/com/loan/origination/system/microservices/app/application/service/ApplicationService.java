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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService implements ApplicationUseCase {

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

    // 3. Create and persist the Outbox Event for Kafka
    ApplicationSubmittedEvent event =
        ApplicationSubmittedEvent.of(
            application.getId(),
            application.getApplicationNumber(),
            application.getBorrower().email(),
            application.getLoanAmount(),
            application.getBorrower().ssn());
    outboxRepository.save(event);

    return application;
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
