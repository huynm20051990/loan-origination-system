package com.loan.origination.system.microservices.chat.application.port.output;

import java.util.UUID;
import java.util.function.Consumer;

public interface ChatAIPort {

  /**
   * Stream an AI response for the given user message scoped to a session.
   * Each token is delivered to {@code tokenSink}; {@code onComplete} is called when streaming ends.
   */
  void streamChat(UUID sessionId, String userMessage, Consumer<String> tokenSink, Runnable onComplete);
}
