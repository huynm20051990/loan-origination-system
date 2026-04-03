package com.loan.origination.system.microservices.chat.application.service;

import com.loan.origination.system.api.core.chat.dto.ChatSessionSummaryDTO;
import com.loan.origination.system.microservices.chat.application.port.input.ChatUseCase;
import com.loan.origination.system.microservices.chat.application.port.output.ChatAIPort;
import com.loan.origination.system.microservices.chat.application.port.output.ChatMessageRepositoryPort;
import com.loan.origination.system.microservices.chat.application.port.output.ChatSessionRepositoryPort;
import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import com.loan.origination.system.microservices.chat.domain.vo.MessageRole;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatApplicationService implements ChatUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(ChatApplicationService.class);

  private final ChatSessionRepositoryPort sessionRepo;
  private final ChatMessageRepositoryPort messageRepo;
  private final ChatAIPort aiPort;

  public ChatApplicationService(
      ChatSessionRepositoryPort sessionRepo,
      ChatMessageRepositoryPort messageRepo,
      ChatAIPort aiPort) {
    this.sessionRepo = sessionRepo;
    this.messageRepo = messageRepo;
    this.aiPort = aiPort;
  }

  @Override
  public ChatSession createSession(String userId) {
    var session = new ChatSession(UUID.randomUUID(), userId, Instant.now());
    return sessionRepo.save(session);
  }

  @Override
  public List<ChatSessionSummaryDTO> listSessions(String userId) {
    return sessionRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(
            s ->
                new ChatSessionSummaryDTO(
                    s.getId(), s.getCreatedAt(), messageRepo.countBySessionId(s.getId())))
        .toList();
  }

  @Override
  @Timed(value = "chat.ai.response", description = "Time to stream a full AI response")
  @Counted(value = "chat.messages.sent", description = "Total user messages sent")
  public void sendMessage(
      UUID sessionId, String userId, String content, Consumer<String> tokenSink, Runnable onComplete) {
    var session = requireSession(sessionId, userId);

    // Persist user message
    var userMsg = new ChatMessage(UUID.randomUUID(), session.getId(), MessageRole.USER, content, Instant.now());
    messageRepo.save(userMsg);
    LOG.info("Persisted user message for session {}", sessionId);

    // Accumulate assistant response tokens
    AtomicReference<StringBuilder> buffer = new AtomicReference<>(new StringBuilder());

    aiPort.streamChat(
        sessionId,
        content,
        token -> {
          buffer.get().append(token);
          tokenSink.accept(token);
        },
        () -> {
          // Persist assembled assistant message
          var assistantMsg =
              new ChatMessage(
                  UUID.randomUUID(),
                  session.getId(),
                  MessageRole.ASSISTANT,
                  buffer.get().toString(),
                  Instant.now());
          messageRepo.save(assistantMsg);
          LOG.info("Persisted assistant message for session {}", sessionId);
          onComplete.run();
        });
  }

  @Override
  public List<ChatMessage> getMessages(UUID sessionId, String userId) {
    requireSession(sessionId, userId);
    return messageRepo.findBySessionIdOrderByTimestamp(sessionId);
  }

  @Override
  public void deleteSession(UUID sessionId, String userId) {
    requireSession(sessionId, userId);
    sessionRepo.deleteById(sessionId);
    LOG.info("Deleted session {} for user {}", sessionId, userId);
  }

  private ChatSession requireSession(UUID sessionId, String userId) {
    var session =
        sessionRepo
            .findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    if (!session.isOwnedBy(userId)) {
      throw new SecurityException("Session " + sessionId + " does not belong to user " + userId);
    }
    return session;
  }
}
