package com.loan.origination.system.microservices.chat.infrastructure.output.persistence.mapper;

import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import com.loan.origination.system.microservices.chat.domain.vo.MessageRole;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity.ChatMessageEntity;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity.ChatMessageEntity.Role;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity.ChatSessionEntity;
import org.springframework.stereotype.Component;

@Component
public class ChatPersistenceMapper {

  public ChatSessionEntity toEntity(ChatSession session) {
    return new ChatSessionEntity(session.getId(), session.getUserId(), session.getCreatedAt());
  }

  public ChatSession toDomain(ChatSessionEntity entity) {
    return new ChatSession(entity.getId(), entity.getUserId(), entity.getCreatedAt());
  }

  public ChatMessageEntity toEntity(ChatMessage message) {
    return new ChatMessageEntity(
        message.getId(),
        message.getSessionId(),
        message.getRole() == MessageRole.USER ? Role.USER : Role.ASSISTANT,
        message.getContent(),
        message.getTimestamp());
  }

  public ChatMessage toDomain(ChatMessageEntity entity) {
    return new ChatMessage(
        entity.getId(),
        entity.getSessionId(),
        entity.getRole() == Role.USER ? MessageRole.USER : MessageRole.ASSISTANT,
        entity.getContent(),
        entity.getTimestamp());
  }
}
