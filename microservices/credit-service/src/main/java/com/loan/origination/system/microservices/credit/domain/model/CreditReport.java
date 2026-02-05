package com.loan.origination.system.microservices.credit.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditReport(
    UUID id,
    String applicationNumber,
    String ssn,
    int creditScore,
    String riskTier,
    LocalDateTime checkedAt) {
  // A factory method to create a new report from raw data
  public static CreditReport create(String applicationNumber, String ssn, int score, String tier) {
    return new CreditReport(
        UUID.randomUUID(), applicationNumber, ssn, score, tier, LocalDateTime.now());
  }
}
