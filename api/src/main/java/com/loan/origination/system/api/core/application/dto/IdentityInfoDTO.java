package com.loan.origination.system.api.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record IdentityInfoDTO(
    @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dob,
    @NotBlank(message = "SSN is required")
        @Pattern(regexp = "^\\d{3}-?\\d{2}-?\\d{4}$", message = "Invalid SSN format")
        String ssn) {}
