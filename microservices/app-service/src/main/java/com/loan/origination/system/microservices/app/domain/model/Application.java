package com.loan.origination.system.microservices.app.domain.model;

import com.loan.origination.system.microservices.app.domain.vo.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Application {
  private final UUID id;
  private final String applicationNumber;
  private final UUID homeId;
  private final Borrower borrower;
  private final BigDecimal loanAmount;
  private final String loanPurpose;
  private ApplicationStatus status;
  private final LocalDateTime createdAt;

  public Application(
      UUID id,
      String applicationNumber,
      UUID homeId,
      Borrower borrower,
      BigDecimal loanAmount,
      String loanPurpose,
      ApplicationStatus status,
      LocalDateTime createdAt) {
    this.id = id;
    this.applicationNumber = applicationNumber;
    this.homeId = homeId;
    this.borrower = borrower;
    this.loanAmount = loanAmount;
    this.loanPurpose = loanPurpose;
    this.status = status;
    this.createdAt = createdAt;
  }

  // Business Logic: Transitioning state
  public void submit() {
    if (this.status != ApplicationStatus.DRAFT) {
      throw new IllegalStateException("Only draft applications can be submitted.");
    }
    this.status = ApplicationStatus.SUBMITTED;
  }

  public void completeAssessment(ApplicationStatus newStatus) {
    // Business Rule: You can only complete an assessment if it's currently being assessed
    // (Or SUBMITTED, depending on your workflow)
    if (this.status != ApplicationStatus.SUBMITTED && this.status != ApplicationStatus.ASSESSING) {
      throw new IllegalStateException("Application is not in a state that can be completed.");
    }

    if (newStatus == ApplicationStatus.DRAFT || newStatus == ApplicationStatus.SUBMITTED) {
      throw new IllegalArgumentException("Cannot transition to an initial state from assessment.");
    }

    this.status = newStatus;
  }

  public void markAsAssessing() {
    this.status = ApplicationStatus.ASSESSING;
  }

  // Getters only (Immutability where possible)
  public UUID getId() {
    return id;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public UUID getHomeId() {
    return homeId;
  }

  public Borrower getBorrower() {
    return borrower;
  }

  public BigDecimal getLoanAmount() {
    return loanAmount;
  }

  public String getLoanPurpose() {
    return loanPurpose;
  }

  public ApplicationStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
