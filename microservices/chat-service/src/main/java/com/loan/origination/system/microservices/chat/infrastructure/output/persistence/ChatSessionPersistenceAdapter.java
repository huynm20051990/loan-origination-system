package com.loan.origination.system.microservices.chat.infrastructure.output.persistence;

import com.loan.origination.system.microservices.chat.application.port.output.ChatSessionRepositoryPort;
import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.mapper.ChatPersistenceMapper;
import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.repository.ChatSessionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionPersistenceAdapter implements ChatSessionRepositoryPort {

  private final ChatSessionRepository repository;
  private final ChatPersistenceMapper mapper;

  public ChatSessionPersistenceAdapter(ChatSessionRepository repository, ChatPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public ChatSession save(ChatSession session) {
    return mapper.toDomain(repository.save(mapper.toEntity(session)));
  }

  @Override
  public Optional<ChatSession> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<ChatSession> findByUserIdOrderByCreatedAtDesc(String userId) {
    return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
