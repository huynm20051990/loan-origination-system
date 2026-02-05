package com.loan.origination.system.microservices.credit.application.service;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.contracts.domain.events.CreditCheckedEvent;
import com.loan.origination.system.microservices.credit.domain.model.CreditReport;
import com.loan.origination.system.microservices.credit.domain.port.in.PerformCreditCheckUseCase;
import com.loan.origination.system.microservices.credit.domain.port.out.CreditBureauPort;
import com.loan.origination.system.microservices.credit.domain.port.out.CreditRepositoryPort;
import com.loan.origination.system.microservices.credit.domain.port.out.OutboxRepositoryPort;
import com.loan.origination.system.microservices.credit.domain.service.ScoringDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditCheckApplicationService implements PerformCreditCheckUseCase {

  // 1. Manual Logger Declaration
  private static final Logger log = LoggerFactory.getLogger(CreditCheckApplicationService.class);

  private final CreditBureauPort creditBureauPort;
  private final CreditRepositoryPort creditRepositoryPort;
  private final OutboxRepositoryPort outboxRepositoryPort;
  private final ScoringDomainService scoringDomainService;

  @Value("${outbox.aggregate-type}")
  private String aggregateType;

  // 2. Manual Constructor for Spring Injection
  public CreditCheckApplicationService(
      CreditBureauPort creditBureauPort,
      CreditRepositoryPort creditRepositoryPort,
      OutboxRepositoryPort outboxRepositoryPort,
      ScoringDomainService scoringDomainService) {
    this.creditBureauPort = creditBureauPort;
    this.creditRepositoryPort = creditRepositoryPort;
    this.outboxRepositoryPort = outboxRepositoryPort;
    this.scoringDomainService = scoringDomainService;
  }

  @Override
  @Transactional
  public void process(ApplicationSubmittedEvent event) {
    log.info("Processing credit check for application: {}", event.applicationNumber());

    // 1. Get the raw score from the Bureau (Port)
    int score = creditBureauPort.getCreditScore(event.ssn());

    // 2. Determine Risk Tier using pure Domain Logic (Domain Service)
    String tier = scoringDomainService.determineRiskTier(score);

    // 3. Create Domain Model
    CreditReport report = CreditReport.create(event.applicationNumber(), event.ssn(), score, tier);

    // 4. Persist to Credit Service Database (Port)
    creditRepositoryPort.save(report, event.aggregateId());

    // 5. Publish result to Kafka (Port)
    CreditCheckedEvent outputEvent =
        CreditCheckedEvent.of(
            aggregateType, // aggregateType (propagated from the submission)
            event.aggregateId(),
            event.applicationNumber(), // applicationNumber (the human-readable ID)
            score, // creditScore
            tier // riskTier (maps to your 'decision' or tier logi
            );

    outboxRepositoryPort.save(outputEvent);

    log.info(
        "Credit check completed for {}. Score: {}, Tier: {}",
        event.applicationNumber(),
        score,
        tier);
  }
}
