package com.loan.origination.system.api.core.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ApplicationSubmissionRequestDTO(
    @NotNull(message = "Home ID is required") UUID homeId,
    @Valid @NotNull PersonalInfoDTO personal,
    @Valid @NotNull IdentityInfoDTO identity, // Replaced financial with identity
    @Valid @NotNull LoanDetailsDTO request) {}
