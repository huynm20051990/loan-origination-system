package com.loan.origination.system.microservices.chat.domain.model;

import java.time.Instant;

/**
 * Represents a single conversational turn in the AI chat session.
 *
 * <p>A turn captures the user's query, the assistant's reply, the session it belongs to, and the
 * timestamp when it occurred. This is a pure domain record with zero framework dependencies —
 * keeping the domain layer portable and free from infrastructure concerns.
 *
 * @param sessionId unique identifier for the conversation session
 * @param query the user's natural-language input
 * @param reply the assistant's generated response
 * @param timestamp the moment this turn was recorded
 */
public record ChatTurn(String sessionId, String query, String reply, Instant timestamp) {}
