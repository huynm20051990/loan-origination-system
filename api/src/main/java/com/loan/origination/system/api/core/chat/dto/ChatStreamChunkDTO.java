package com.loan.origination.system.api.core.chat.dto;

import java.util.UUID;

public record ChatStreamChunkDTO(String token, boolean done, UUID messageId) {

  public static ChatStreamChunkDTO token(String token) {
    return new ChatStreamChunkDTO(token, false, null);
  }

  public static ChatStreamChunkDTO done(UUID messageId) {
    return new ChatStreamChunkDTO("", true, messageId);
  }
}
