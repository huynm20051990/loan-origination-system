package com.loan.origination.system.microservices.chat.infrastructure.input.rest.mapper;

import com.loan.origination.system.api.core.chat.dto.ChatMessageResponseDTO;
import com.loan.origination.system.api.core.chat.dto.ChatSessionResponseDTO;
import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatWebMapper {

  public ChatSessionResponseDTO toResponse(ChatSession session) {
    return new ChatSessionResponseDTO(session.getId(), session.getUserId(), session.getCreatedAt());
  }

  public ChatMessageResponseDTO toResponse(ChatMessage message) {
    return new ChatMessageResponseDTO(
        message.getId(),
        message.getRole().name(),
        message.getContent(),
        message.getTimestamp());
  }
}
