package com.loan.origination.system.microservices.chat.application.service;

import com.loan.origination.system.microservices.chat.application.port.output.HomeResult;
import com.loan.origination.system.microservices.chat.application.port.output.HomeSearchPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ChatApplicationService}.
 *
 * <p>Verifies the SSE event ordering contract: a {@code listings} event (containing the
 * serialised home results) must arrive before any {@code token} events, and the stream must
 * terminate with a single {@code done} event. All external collaborators — {@link HomeSearchPort}
 * and {@link ChatClient} — are replaced with Mockito mocks so that no real I/O occurs.
 *
 * <p>These tests are written FIRST (Red phase). They will fail to compile until
 * {@link ChatApplicationService} is created in T023.
 */
@ExtendWith(MockitoExtension.class)
class ChatApplicationServiceTest {

    @Mock
    private HomeSearchPort homeSearchPort;

    @Mock
    private ChatClient chatClient;

    private ChatApplicationService chatApplicationService;

    @BeforeEach
    void setUp() {
        chatApplicationService = new ChatApplicationService(homeSearchPort, chatClient);
    }

    /**
     * Happy path: two home results returned; AI streams three tokens.
     *
     * <p>Assert that the first SSE event has type {@code listings} and its data payload contains
     * the id of the first result, followed by exactly three {@code token} events (one per streamed
     * chunk), followed by a single {@code done} event after which the stream completes.
     */
    @Test
    @DisplayName("stream() emits listings → 3 token events → done when HomeSearchPort returns 2 results")
    @SuppressWarnings("unchecked")
    void stream_shouldEmitListingsFirstThenTokensThenDone() {
        // Given: HomeSearchPort returns two matching listings
        List<HomeResult> homes = List.of(
                new HomeResult(
                        "home-1",
                        450_000,
                        3,
                        2.0,
                        1_800,
                        "https://img.example.com/1",
                        new HomeResult.Address("123 Main St", "Austin", "TX", "78701"),
                        "active",
                        "Cozy 3-bed home in central Austin"),
                new HomeResult(
                        "home-2",
                        480_000,
                        3,
                        2.5,
                        2_000,
                        "https://img.example.com/2",
                        new HomeResult.Address("456 Oak Ave", "Austin", "TX", "78702"),
                        "active",
                        "Spacious corner-lot home with pool"));
        when(homeSearchPort.search(anyString())).thenReturn(homes);

        // Mock ChatClient fluent chain:
        //   chatClient.prompt()
        //     .advisors(Consumer<AdvisorSpec>)   ← sets conversation-id for memory
        //     .user(String)                      ← sets the user message / prompt
        //     .stream()
        //     .content()                         ← returns Flux<String> of AI tokens
        ChatClient.ChatClientRequestSpec requestSpec =
                mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.StreamResponseSpec streamResponseSpec =
                mock(ChatClient.StreamResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(Flux.just("token-a", " token-b", " token-c"));

        // When
        Flux<ServerSentEvent<String>> result =
                chatApplicationService.stream("session-42", "3 beds under $500k in Austin");

        // Then: strict event ordering — listings before any token, stream ends with done
        StepVerifier.create(result)
                .assertNext(sse -> {
                    assertThat(sse.event())
                            .as("first event type must be 'listings'")
                            .isEqualTo("listings");
                    assertThat(sse.data())
                            .as("listings payload must contain the first home's id")
                            .contains("home-1");
                })
                .assertNext(sse ->
                        assertThat(sse.event())
                                .as("second event type must be 'token'")
                                .isEqualTo("token"))
                .assertNext(sse ->
                        assertThat(sse.event())
                                .as("third event type must be 'token'")
                                .isEqualTo("token"))
                .assertNext(sse ->
                        assertThat(sse.event())
                                .as("fourth event type must be 'token'")
                                .isEqualTo("token"))
                .assertNext(sse ->
                        assertThat(sse.event())
                                .as("final event type must be 'done'")
                                .isEqualTo("done"))
                .verifyComplete();
    }
}
