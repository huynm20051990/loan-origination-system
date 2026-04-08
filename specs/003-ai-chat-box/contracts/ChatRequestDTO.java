package com.loan.origination.system.api.core.chat.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for {@code POST /api/v1/chat/stream}.
 *
 * @param sessionId frontend-generated UUID identifying the page session; used as the
 *     Spring AI {@code ChatMemory.CONVERSATION_ID} key
 * @param query non-blank natural language home-search query (e.g. "3 beds under $500k in Austin")
 */
public record ChatRequestDTO(
    @NotBlank String sessionId,
    @NotBlank String query) {}
