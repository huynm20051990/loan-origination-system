package com.loan.origination.system.microservices.app.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
public class ApplicationEntity {

  @Id private UUID id;

  @Column(name = "application_number", nullable = false, unique = true)
  private String applicationNumber;

  @Column(name = "home_id", nullable = false)
  private UUID homeId;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  // Flattened Personal & Identity Information
  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String email;

  private String phone;

  @Column(name = "date_of_birth", nullable = false)
  private LocalDate dateOfBirth; // Added to match new stepper

  @Column(name = "ssn", nullable = false)
  private String ssn;

  // Loan Request Details
  @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal loanAmount;

  @Column(name = "loan_purpose")
  private String loanPurpose;

  // Default constructor for JPA
  public ApplicationEntity() {}

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getApplicationNumber() {
    return applicationNumber;
  }

  public void setApplicationNumber(String applicationNumber) {
    this.applicationNumber = applicationNumber;
  }

  public UUID getHomeId() {
    return homeId;
  }

  public void setHomeId(UUID homeId) {
    this.homeId = homeId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public BigDecimal getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(BigDecimal loanAmount) {
    this.loanAmount = loanAmount;
  }

  public String getLoanPurpose() {
    return loanPurpose;
  }

  public void setLoanPurpose(String loanPurpose) {
    this.loanPurpose = loanPurpose;
  }
}
