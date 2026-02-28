package com.loan.origination.system.microservices.credit.domain.service;

import org.springframework.stereotype.Service;

@Service
public class DomainScoringService {

  /**
   * Determines the risk tier based on the numerical credit score. These are internal business
   * rules.
   */
  public String determineRiskTier(int score) {
    if (score >= 740) return "PRIME";
    if (score >= 670) return "NEAR_PRIME";
    if (score >= 580) return "SUBPRIME";
    return "POOR";
  }
}
