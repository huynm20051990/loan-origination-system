package com.loan.origination.system.microservices.chat.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.loan.origination.system.microservices.chat.application.port.output.ChatAIPort;
import com.loan.origination.system.microservices.chat.application.port.output.ChatMessageRepositoryPort;
import com.loan.origination.system.microservices.chat.application.port.output.ChatSessionRepositoryPort;
import com.loan.origination.system.microservices.chat.domain.model.ChatMessage;
import com.loan.origination.system.microservices.chat.domain.model.ChatSession;
import com.loan.origination.system.microservices.chat.domain.vo.MessageRole;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatApplicationServiceTest {

  @Mock ChatSessionRepositoryPort sessionRepo;
  @Mock ChatMessageRepositoryPort messageRepo;
  @Mock ChatAIPort aiPort;

  ChatApplicationService service;

  @BeforeEach
  void setUp() {
    service = new ChatApplicationService(sessionRepo, messageRepo, aiPort);
  }

  @Test
  void createSessionPersistsViaRepositoryPort() {
    var userId = "user-abc";
    var expected = new ChatSession(UUID.randomUUID(), userId, Instant.now());
    when(sessionRepo.save(any())).thenReturn(expected);

    var result = service.createSession(userId);

    verify(sessionRepo).save(any(ChatSession.class));
    assertThat(result.getUserId()).isEqualTo(userId);
  }

  @Test
  void getMessagesEnforcesOwnershipCheck() {
    var sessionId = UUID.randomUUID();
    var ownerId = "owner";
    var session = new ChatSession(sessionId, ownerId, Instant.now());
    when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));
    when(messageRepo.findBySessionIdOrderByTimestamp(sessionId)).thenReturn(List.of());

    var messages = service.getMessages(sessionId, ownerId);
    assertThat(messages).isEmpty();
  }

  @Test
  void getMessagesThrowsWhenUserDoesNotOwnSession() {
    var sessionId = UUID.randomUUID();
    var session = new ChatSession(sessionId, "real-owner", Instant.now());
    when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

    assertThatThrownBy(() -> service.getMessages(sessionId, "other-user"))
        .isInstanceOf(SecurityException.class);
  }

  @Test
  void deleteSessionEnforcesOwnershipCheck() {
    var sessionId = UUID.randomUUID();
    var session = new ChatSession(sessionId, "real-owner", Instant.now());
    when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

    assertThatThrownBy(() -> service.deleteSession(sessionId, "other-user"))
        .isInstanceOf(SecurityException.class);
  }

  @Test
  void listSessionsReturnsSessionsForUser() {
    var userId = "user-x";
    when(sessionRepo.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of());

    var sessions = service.listSessions(userId);
    assertThat(sessions).isEmpty();
  }
}
