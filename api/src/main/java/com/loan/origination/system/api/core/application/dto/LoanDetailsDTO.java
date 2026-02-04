package com.loan.origination.system.api.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record LoanDetailsDTO(
    @NotNull(message = "Loan amount is required")
        @Positive(message = "Loan amount must be greater than zero")
        BigDecimal loanAmount,
    @NotBlank(message = "Loan purpose is required") String loanPurpose) {}
