package com.loan.origination.system.api.core.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponseDTO(
    UUID id,
    String applicationNumber,
    String status,
    LocalDateTime createdAt,
    // Include minimal home/personal info for the "Review/Finish" screens
    String fullName,
    BigDecimal loanAmount) {}
