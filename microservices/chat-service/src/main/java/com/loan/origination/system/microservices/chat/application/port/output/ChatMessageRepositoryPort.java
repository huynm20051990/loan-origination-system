package com.loan.origination.system.microservices.chat.application.port.output;

import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepositoryPort {

  ChatMessage save(ChatMessage message);

  List<ChatMessage> findBySessionIdOrderByTimestamp(UUID sessionId);

  int countBySessionId(UUID sessionId);
}
