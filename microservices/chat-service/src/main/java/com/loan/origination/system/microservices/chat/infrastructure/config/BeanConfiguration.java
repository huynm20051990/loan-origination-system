package com.loan.origination.system.microservices.chat.infrastructure.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.cassandra.CassandraChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Spring bean wiring for the chat-service.
 *
 * <p>Centralises infrastructure bean construction:
 * <ul>
 *   <li>{@link ChatMemory} — Cassandra-backed sliding window (20 messages) for per-session
 *       conversation history.</li>
 *   <li>{@link ChatClient} — pre-configured with {@link MessageChatMemoryAdvisor} (injects
 *       history from Cassandra per request) and {@link SimpleLoggerAdvisor} (logs prompt/response
 *       at DEBUG level).</li>
 *   <li>{@link RestClient} — base-URL-configured client for calling home-service search
 *       endpoints.</li>
 * </ul>
 */
@Configuration
public class BeanConfiguration {

  /**
   * Creates a {@link ChatMemory} backed by {@link CassandraChatMemoryRepository} with a sliding
   * window of 20 messages.
   *
   * <p>The window size of 20 was chosen to fit within the Gemini 2.5 Flash context window while
   * keeping Cassandra read amplification low per request.
   *
   * @param repository the Spring AI auto-configured Cassandra repository
   * @return a {@link MessageWindowChatMemory} retaining the last 20 messages per session
   */
  @Bean
  public ChatMemory chatMemory(CassandraChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(20)
        .build();
  }

  /**
   * Creates a {@link ChatClient} with default advisors that handle per-request memory injection
   * and structured logging.
   *
   * <p>{@link MessageChatMemoryAdvisor} enriches every prompt with the session's stored message
   * history before the request reaches the model. {@link SimpleLoggerAdvisor} logs the full
   * request/response at DEBUG level for observability without custom instrumentation.
   *
   * @param builder     the Spring AI auto-configured builder (model, API key, timeouts)
   * @param chatMemory  the Cassandra-backed memory bean
   * @return a fully configured {@link ChatClient}
   */
  @Bean
  public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
    return builder
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build(),
            new SimpleLoggerAdvisor())
        .build();
  }

  /**
   * Creates a {@link RestClient} pre-configured with the home-service base URL.
   *
   * <p>The base URL is resolved from {@code app.home-service.url} in the externalized config
   * ({@code chat.yml}), allowing environment-specific overrides without code changes.
   *
   * @param url the home-service base URL (e.g. {@code http://home-service})
   * @return a {@link RestClient} with the home-service base URL set
   */
  @Bean
  public RestClient homeRestClient(@Value("${app.home-service.url}") String url) {
    return RestClient.builder().baseUrl(url).build();
  }
}
