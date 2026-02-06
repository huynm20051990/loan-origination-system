package com.loan.origination.system.microservices.app.domain.model;

import java.time.LocalDate;

public record Borrower(
    String fullName,
    String email,
    String phone,
    LocalDate dob, // Added DOB
    String ssn) {

  public String ssnLastFour() {
    if (ssn == null || ssn.length() < 4) {
      return "****";
    }
    String digitsOnly = ssn.replaceAll("[^0-9]", "");
    return digitsOnly.length() >= 4 ? digitsOnly.substring(digitsOnly.length() - 4) : "****";
  }
}
