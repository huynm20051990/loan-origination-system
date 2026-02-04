package com.loan.origination.system.microservices.app.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
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

  // Flattened Borrower Information
  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String email;

  private String phone;

  @Column(name = "ssn", nullable = false)
  private String ssn;

  @Column(name = "annual_income", nullable = false, precision = 15, scale = 2)
  private BigDecimal annualIncome;

  @Column(name = "employer_name")
  private String employerName;

  // Loan Request Details
  @Column(name = "loan_amount", nullable = false, precision = 15, scale = 2)
  private BigDecimal loanAmount;

  @Column(name = "loan_purpose")
  private String loanPurpose;

  // Default constructor for JPA
  public ApplicationEntity() {}

  // Getters and Setters (standard for JPA Entities)
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

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public BigDecimal getAnnualIncome() {
    return annualIncome;
  }

  public void setAnnualIncome(BigDecimal annualIncome) {
    this.annualIncome = annualIncome;
  }

  public String getEmployerName() {
    return employerName;
  }

  public void setEmployerName(String employerName) {
    this.employerName = employerName;
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
