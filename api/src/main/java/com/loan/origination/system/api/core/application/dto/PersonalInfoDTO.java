package com.loan.origination.system.api.core.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PersonalInfoDTO(
    @NotBlank(message = "Full name is required") String fullName,
    @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Phone number is required") String phone) {}
