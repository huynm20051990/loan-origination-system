package com.loan.origination.system.api.core.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequestDTO(
    @NotBlank(message = "Message content must not be blank")
        @Size(max = 1000, message = "Message content must not exceed 1000 characters")
        String content) {}
