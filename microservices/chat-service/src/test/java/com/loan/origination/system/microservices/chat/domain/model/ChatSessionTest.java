package com.loan.origination.system.microservices.chat.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChatSessionTest {

  @Test
  void sessionBelongsToExactlyOneUser() {
    var userId = "user-123";
    var session = new ChatSession(UUID.randomUUID(), userId, Instant.now());

    assertThat(session.getUserId()).isEqualTo(userId);
    assertThat(session.getId()).isNotNull();
    assertThat(session.getCreatedAt()).isNotNull();
  }

  @Test
  void sessionIdIsUnique() {
    var s1 = new ChatSession(UUID.randomUUID(), "user-a", Instant.now());
    var s2 = new ChatSession(UUID.randomUUID(), "user-b", Instant.now());

    assertThat(s1.getId()).isNotEqualTo(s2.getId());
  }
}
