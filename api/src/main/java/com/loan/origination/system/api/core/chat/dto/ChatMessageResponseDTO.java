package com.loan.origination.system.api.core.chat.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponseDTO(UUID messageId, String role, String content, Instant timestamp) {}
