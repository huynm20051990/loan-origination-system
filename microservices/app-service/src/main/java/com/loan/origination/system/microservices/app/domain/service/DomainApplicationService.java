package com.loan.origination.system.microservices.app.domain.service;

import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import com.loan.origination.system.microservices.app.domain.vo.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure Domain Service containing business rules for loan creation. No dependencies on databases or
 * external frameworks.
 */
public class DomainApplicationService {

  /** Creates a new LoanApplication instance with a generated reference number. */
  public Application initiateApplication(
      UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose) {

    UUID id = UUID.randomUUID();
    String applicationNumber = generateApplicationNumber();

    Application application =
        new Application(
            id,
            applicationNumber,
            homeId,
            borrower,
            loanAmount,
            loanPurpose,
            ApplicationStatus.DRAFT, // Initial status upon successful submission
            LocalDateTime.now());
    application.submit();

    return application;
  }

  /**
   * Example Business Rule: Generates a human-readable ID. Logic could be expanded to include
   * year/sequence (e.g., APP-2026-X).
   */
  private String generateApplicationNumber() {
    return "APP-" + System.currentTimeMillis() % 100000;
  }
}
