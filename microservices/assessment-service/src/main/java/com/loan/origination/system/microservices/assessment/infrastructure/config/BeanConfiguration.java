package com.loan.origination.system.microservices.assessment.infrastructure.config;

import io.modelcontextprotocol.client.McpSyncClient;
import java.util.List;
import org.springaicommunity.tool.search.ToolSearcher;
import org.springaicommunity.tool.searcher.LuceneToolSearcher;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.cassandra.CassandraChatMemoryRepository;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfiguration {
  @Bean
  public ChatMemory chatMemory(CassandraChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(20)
        .build();
  }

  @Bean
  ToolSearcher toolSearcher() {
    return new LuceneToolSearcher(0.4f);
  }

  @Bean
  ChatClient chatClient(ChatClient.Builder chatClientBuilder, List<McpSyncClient> mcpClients) {
    return chatClientBuilder
        .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpClients))
        .build();
  }

  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // The 'csrf' variable here IS the CsrfConfigurer
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
    return http.build();
  }

  /**
   * Overload Boot's default {@link WebClient.Builder}, so that we can inject an oauth2-enabled
   * {@link ExchangeFilterFunction} that adds OAuth2 tokens to requests sent to the MCP server.
   */
  @Bean
  WebClient.Builder webClientBuilder(McpSyncClientExchangeFilterFunction filterFunction) {
    return WebClient.builder().apply(filterFunction.configuration());
  }
}
