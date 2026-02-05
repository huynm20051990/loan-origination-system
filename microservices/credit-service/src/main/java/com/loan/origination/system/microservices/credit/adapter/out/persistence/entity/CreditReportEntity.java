package com.loan.origination.system.microservices.credit.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_reports")
public class CreditReportEntity {

  @Id private UUID id;

  @Column(name = "application_id", nullable = false)
  private UUID applicationId;

  @Column(name = "application_number", nullable = false)
  private String applicationNumber;

  @Column(name = "ssn_hash", nullable = false)
  private String ssnHash;

  @Column(name = "credit_score", nullable = false)
  private int creditScore;

  @Column(name = "risk_tier", nullable = false)
  private String riskTier;

  @Column(name = "checked_at", nullable = false)
  private LocalDateTime checkedAt;

  // Standard Default Constructor for JPA
  public CreditReportEntity() {}

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(UUID applicationId) {
    this.applicationId = applicationId;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public void setApplicationNumber(String applicationNumber) {
    this.applicationNumber = applicationNumber;
  }

  public String getSsnHash() {
    return ssnHash;
  }

  public void setSsnHash(String ssnHash) {
    this.ssnHash = ssnHash;
  }

  public int getCreditScore() {
    return creditScore;
  }

  public void setCreditScore(int creditScore) {
    this.creditScore = creditScore;
  }

  public String getRiskTier() {
    return riskTier;
  }

  public void setRiskTier(String riskTier) {
    this.riskTier = riskTier;
  }

  public LocalDateTime getCheckedAt() {
    return checkedAt;
  }

  public void setCheckedAt(LocalDateTime checkedAt) {
    this.checkedAt = checkedAt;
  }
}
