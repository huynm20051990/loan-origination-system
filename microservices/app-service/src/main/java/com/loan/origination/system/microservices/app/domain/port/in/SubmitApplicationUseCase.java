package com.loan.origination.system.microservices.app.domain.port.in;

import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import java.math.BigDecimal;
import java.util.UUID;

/** The Driving Port for submitting a new loan application. */
public interface SubmitApplicationUseCase {

  /**
   * Orchestrates the process of creating a loan and preparing the outbox event. * @param homeId The
   * ID of the property being applied for
   *
   * @param borrower The domain model containing personal and financial data
   * @param loanAmount The amount requested
   * @param loanPurpose The reason for the loan
   * @return The newly created LoanApplication domain model
   */
  Application submit(UUID homeId, Borrower borrower, BigDecimal loanAmount, String loanPurpose);
}
