package com.loan.origination.system.api.core.chat.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatSessionResponseDTO(UUID sessionId, String userId, Instant createdAt) {}
