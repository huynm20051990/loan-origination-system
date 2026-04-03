package com.loan.origination.system.microservices.chat.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loan.origination.system.microservices.chat.domain.vo.MessageRole;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChatMessageTest {

  @Test
  void messageHasRoleAndNonNullContent() {
    var msg = new ChatMessage(UUID.randomUUID(), UUID.randomUUID(), MessageRole.USER, "Hello", Instant.now());

    assertThat(msg.getRole()).isEqualTo(MessageRole.USER);
    assertThat(msg.getContent()).isNotBlank();
  }

  @Test
  void assistantRoleIsDistinctFromUser() {
    var user = new ChatMessage(UUID.randomUUID(), UUID.randomUUID(), MessageRole.USER, "q", Instant.now());
    var asst = new ChatMessage(UUID.randomUUID(), UUID.randomUUID(), MessageRole.ASSISTANT, "a", Instant.now());

    assertThat(user.getRole()).isNotEqualTo(asst.getRole());
  }

  @Test
  void contentMustNotBeNull() {
    assertThatThrownBy(
            () -> new ChatMessage(UUID.randomUUID(), UUID.randomUUID(), MessageRole.USER, null, Instant.now()))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
