package com.loan.origination.system.microservices.chat.infrastructure.output.ai;

import com.loan.origination.system.microservices.chat.application.port.output.ChatAIPort;
import com.loan.origination.system.microservices.chat.infrastructure.output.ai.tools.HomeSearchChatTools;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ChatAIAdapter implements ChatAIPort {

  private static final Logger LOG = LoggerFactory.getLogger(ChatAIAdapter.class);

  private final ChatClient chatClient;
  private final Resource systemPromptResource;

  public ChatAIAdapter(
      ChatClient.Builder builder,
      ChatMemory chatMemory,
      HomeSearchChatTools homeSearchChatTools,
      @Value("file:/agentic-ai/prompts/chat-agent.st") Resource systemPromptResource) {
    this.systemPromptResource = systemPromptResource;
    this.chatClient =
        builder
            .defaultTools(homeSearchChatTools)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();
  }

  @Override
  public void streamChat(UUID sessionId, String userMessage, Consumer<String> tokenSink, Runnable onComplete) {
    LOG.info("Starting AI stream for session {}", sessionId);

    try {
      chatClient
          .prompt()
          .system(s -> s.text(systemPromptResource))
          .user(userMessage)
          .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId.toString()))
          .stream()
          .content()
          .doOnNext(tokenSink::accept)
          .doOnComplete(() -> {
            LOG.info("AI stream completed for session {}", sessionId);
            onComplete.run();
          })
          .doOnError(e -> LOG.error("AI stream error for session {}: {}", sessionId, e.getMessage()))
          .blockLast();
    } catch (Exception e) {
      LOG.error("Failed to stream AI response for session {}: {}", sessionId, e.getMessage());
      throw new RuntimeException("AI stream failed: " + e.getMessage(), e);
    }
  }
}
