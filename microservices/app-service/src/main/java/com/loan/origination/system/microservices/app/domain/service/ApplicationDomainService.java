package com.loan.origination.system.microservices.app.domain.service;

import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.ApplicationStatus;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure Domain Service containing business rules for loan creation. No dependencies on databases or
 * external frameworks.
 */
public class ApplicationDomainService {

  /** Creates a new LoanApplication instance with a generated reference number. */
  public Application initiateApplication(
      UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose) {

    UUID id = UUID.randomUUID();
    String applicationNumber = generateApplicationNumber();

    return new Application(
        id,
        applicationNumber,
        homeId,
        borrower,
        loanAmount,
        loanPurpose,
        ApplicationStatus.SUBMITTED, // Initial status upon successful submission
        LocalDateTime.now());
  }

  /**
   * Example Business Rule: Generates a human-readable ID. Logic could be expanded to include
   * year/sequence (e.g., APP-2026-X).
   */
  private String generateApplicationNumber() {
    return "APP-" + System.currentTimeMillis() % 100000;
  }
}
