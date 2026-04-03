package com.loan.origination.system.microservices.chat.application.port.output;

import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepositoryPort {

  ChatSession save(ChatSession session);

  Optional<ChatSession> findById(UUID id);

  List<ChatSession> findByUserIdOrderByCreatedAtDesc(String userId);

  void deleteById(UUID id);
}
