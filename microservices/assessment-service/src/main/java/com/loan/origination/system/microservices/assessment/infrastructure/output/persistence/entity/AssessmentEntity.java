package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "assessments")
public class AssessmentEntity {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "application_id", nullable = false)
  private UUID applicationId;

  @Column(nullable = false)
  private String status;

  @Column private String decision;

  public AssessmentEntity() {}

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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDecision() {
    return decision;
  }

  public void setDecision(String decision) {
    this.decision = decision;
  }
}
