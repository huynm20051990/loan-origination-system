package com.loan.origination.system.microservices.app.domain.model;

import java.math.BigDecimal;

/** Value Object representing the person applying for the loan. Immutable by design. */
public record Borrower(
    String fullName,
    String email,
    String phone,
    String ssn,
    BigDecimal annualIncome,
    String employerName) {
  // Domain logic: You could add a helper here to get only the last 4 of SSN
  public String ssnLastFour() {
    if (ssn == null || ssn.length() < 4) return "****";
    return ssn.substring(ssn.length() - 4);
  }
}
