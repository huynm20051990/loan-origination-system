package com.loan.origination.system.microservices.chat.infrastructure.output.persistence;

import com.loan.origination.system.microservices.chat.application.port.output.ChatMessageRepositoryPort;
import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.mapper.ChatPersistenceMapper;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.repository.ChatMessageRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ChatMessagePersistenceAdapter implements ChatMessageRepositoryPort {

  private final ChatMessageRepository repository;
  private final ChatPersistenceMapper mapper;

  public ChatMessagePersistenceAdapter(ChatMessageRepository repository, ChatPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public ChatMessage save(ChatMessage message) {
    return mapper.toDomain(repository.save(mapper.toEntity(message)));
  }

  @Override
  public List<ChatMessage> findBySessionIdOrderByTimestamp(UUID sessionId) {
    return repository.findBySessionIdOrderByTimestampAsc(sessionId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public int countBySessionId(UUID sessionId) {
    return repository.countBySessionId(sessionId);
  }
}
