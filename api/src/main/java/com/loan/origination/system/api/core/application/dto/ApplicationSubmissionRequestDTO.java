package com.loan.origination.system.api.core.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ApplicationSubmissionRequestDTO(
    @NotNull(message = "Home ID is required") UUID homeId,
    @Valid @NotNull PersonalInfoDTO personal,
    @Valid @NotNull FinancialInfoDTO financial,
    @Valid @NotNull LoanDetailsDTO request) {}
