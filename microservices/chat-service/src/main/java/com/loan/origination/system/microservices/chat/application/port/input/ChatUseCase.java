package com.loan.origination.system.microservices.chat.application.port.input;

import com.loan.origination.system.api.core.chat.dto.ChatSessionSummaryDTO;
import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ChatUseCase {

  ChatSession createSession(String userId);

  List<ChatSessionSummaryDTO> listSessions(String userId);

  void sendMessage(UUID sessionId, String userId, String content, Consumer<String> tokenSink, Runnable onComplete);

  List<ChatMessage> getMessages(UUID sessionId, String userId);

  void deleteSession(UUID sessionId, String userId);
}
