package com.loan.origination.system.microservices.chat.infrastructure.output.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.ai.chat.client.advisor.api.Advisor;

import com.loan.origination.system.microservices.chat.infrastructure.output.ai.tools.HomeSearchChatTools;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class ChatAIAdapterTest {

  @Mock
  private ChatClient.Builder chatClientBuilder;

  @Mock
  private ChatMemory chatMemory;

  @Mock
  private HomeSearchChatTools homeSearchChatTools;

  @Test
  void streamChat_sendsTokensToSink() {
    // Arrange: build a full mock chain for ChatClient fluent API
    ChatClient chatClient = mock(ChatClient.class);
    ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.ChatClientRequestSpec systemSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.ChatClientRequestSpec userSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.ChatClientRequestSpec advisorSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.StreamResponseSpec streamSpec = mock(ChatClient.StreamResponseSpec.class);

    when(chatClientBuilder.defaultTools(any(HomeSearchChatTools.class))).thenReturn(chatClientBuilder);
    when(chatClientBuilder.defaultAdvisors(any(Advisor.class))).thenReturn(chatClientBuilder);
    when(chatClientBuilder.build()).thenReturn(chatClient);

    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.system(any(java.util.function.Consumer.class))).thenReturn(systemSpec);
    when(systemSpec.user(any(String.class))).thenReturn(userSpec);
    when(userSpec.advisors(any(java.util.function.Consumer.class))).thenReturn(advisorSpec);
    when(advisorSpec.stream()).thenReturn(streamSpec);
    when(streamSpec.content()).thenReturn(Flux.just("Hello", " world"));

    Resource fakePrompt = new ClassPathResource("test-prompt.st");
    ChatAIAdapter adapter = new ChatAIAdapter(chatClientBuilder, chatMemory, homeSearchChatTools, fakePrompt);

    UUID sessionId = UUID.randomUUID();
    List<String> tokens = new ArrayList<>();
    AtomicBoolean completed = new AtomicBoolean(false);

    // Act
    adapter.streamChat(sessionId, "Find me a 3-bed home", tokens::add, () -> completed.set(true));

    // Assert
    assertThat(tokens).containsExactly("Hello", " world");
    assertThat(completed.get()).isTrue();
  }

  @Test
  void streamChat_callsOnCompleteWhenStreamFinishes() {
    ChatClient chatClient = mock(ChatClient.class);
    ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.ChatClientRequestSpec systemSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.ChatClientRequestSpec userSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.ChatClientRequestSpec advisorSpec = mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.StreamResponseSpec streamSpec = mock(ChatClient.StreamResponseSpec.class);

    when(chatClientBuilder.defaultTools(any(HomeSearchChatTools.class))).thenReturn(chatClientBuilder);
    when(chatClientBuilder.defaultAdvisors(any(Advisor.class))).thenReturn(chatClientBuilder);
    when(chatClientBuilder.build()).thenReturn(chatClient);
    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.system(any(java.util.function.Consumer.class))).thenReturn(systemSpec);
    when(systemSpec.user(any(String.class))).thenReturn(userSpec);
    when(userSpec.advisors(any(java.util.function.Consumer.class))).thenReturn(advisorSpec);
    when(advisorSpec.stream()).thenReturn(streamSpec);
    when(streamSpec.content()).thenReturn(Flux.empty());

    Resource fakePrompt = new ClassPathResource("test-prompt.st");
    ChatAIAdapter adapter = new ChatAIAdapter(chatClientBuilder, chatMemory, homeSearchChatTools, fakePrompt);

    AtomicBoolean completed = new AtomicBoolean(false);
    adapter.streamChat(UUID.randomUUID(), "hello", token -> {}, () -> completed.set(true));

    assertThat(completed.get()).isTrue();
  }
}
