package com.loan.origination.system.microservices.app.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import com.loan.origination.system.microservices.app.domain.port.in.SubmitApplicationUseCase;
import com.loan.origination.system.microservices.app.domain.port.out.ApplicationRepositoryPort;
import com.loan.origination.system.microservices.app.domain.port.out.OutboxRepositoryPort;
import com.loan.origination.system.microservices.app.domain.service.ApplicationDomainService;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService implements SubmitApplicationUseCase {

  private final ApplicationDomainService loanDomainService;
  private final ApplicationRepositoryPort loanRepository;
  private final OutboxRepositoryPort outboxRepository;
  private final ObjectMapper objectMapper;

  @Value("${outbox.aggregate-type}")
  private String aggregateType;

  public ApplicationService(
      ApplicationDomainService loanDomainService,
      ApplicationRepositoryPort loanRepository,
      OutboxRepositoryPort outboxRepository,
      ObjectMapper objectMapper) {
    this.loanDomainService = loanDomainService;
    this.loanRepository = loanRepository;
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
  } // For JSON conversion

  @Override
  @Transactional
  public Application submit(
      UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose) {

    // 1. Use Domain Service to create the Loan Application aggregate
    Application application =
        loanDomainService.initiateApplication(homeId, borrower, loanAmount, loanPurpose);

    // 2. Persist the Loan Application
    loanRepository.save(application);

    // 3. Create and persist the Outbox Event for Kafka
    ApplicationSubmittedEvent event =
        ApplicationSubmittedEvent.of(
            aggregateType,
            application.getId(),
            application.getApplicationNumber(),
            application.getBorrower().email(),
            application.getLoanAmount(),
            application.getBorrower().ssn());
    outboxRepository.save(event);

    return application;
  }
}
