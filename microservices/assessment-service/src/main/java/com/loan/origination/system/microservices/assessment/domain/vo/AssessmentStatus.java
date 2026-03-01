package com.loan.origination.system.microservices.assessment.domain.vo;

public enum AssessmentStatus {
  INITIATED,
  IDENTITY_VERIFIED,
  CREDIT_CHECKED,
  PROPERTY_EVALUATED,
  INCOME_EMPLOYMENT_CONFIRMED,
  FINANCIALS_ANALYZED,
  UNDERWRITING_COMPLETED,
  DECIDED,
  COMPLETED,
  FAILED
}
