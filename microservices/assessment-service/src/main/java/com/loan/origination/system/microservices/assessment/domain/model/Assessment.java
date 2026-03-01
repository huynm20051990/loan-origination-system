package com.loan.origination.system.microservices.assessment.domain.model;

import com.loan.origination.system.microservices.assessment.domain.vo.AssessmentStatus;
import java.util.UUID;

public class Assessment {
  private final UUID id;
  private final UUID applicationId;
  private AssessmentStatus status;
  private String decision;
  private String remarks;

  public Assessment(UUID applicationId) {
    this.id = UUID.randomUUID();
    this.applicationId = applicationId;
    this.status = AssessmentStatus.INITIATED;
  }

  // Business Logic Methods
  public void updateStatus(AssessmentStatus newStatus) {
    this.status = newStatus;
  }

  public void recordDecision(String decision, String remarks) {
    this.decision = decision;
    this.remarks = remarks;
    this.status = AssessmentStatus.DECIDED;
  }

  // Getters
  public UUID getId() {
    return id;
  }

  public UUID getApplicationId() {
    return applicationId;
  }

  public AssessmentStatus getStatus() {
    return status;
  }

  public String getDecision() {
    return decision;
  }

  public String getRemarks() {
    return remarks;
  }
}
