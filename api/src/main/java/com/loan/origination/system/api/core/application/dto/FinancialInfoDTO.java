package com.loan.origination.system.api.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record FinancialInfoDTO(
    @NotNull(message = "Annual income is required")
        @Positive(message = "Income must be greater than zero")
        BigDecimal annualIncome,
    @NotBlank(message = "Employer name is required") String employer) {}
