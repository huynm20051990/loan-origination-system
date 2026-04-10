package com.loan.origination.system.microservices.chat.infrastructure.input.rest;

import com.loan.origination.system.microservices.chat.application.port.input.ChatUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration test for {@link ChatController}.
 *
 * <p>Starts the full Spring Boot application context at a random port and uses
 * {@link WebTestClient} to exercise the SSE endpoint at {@code GET /api/v1/chat/stream}.
 *
 * <p>The {@link ChatUseCase} is replaced with a Mockito {@code @MockBean} so the test is
 * deterministic and does not require a live AI model, Cassandra instance, or home-service.
 * The {@code application-test.yml} profile supplies dummy property values that satisfy
 * Spring Boot auto-configuration without initiating real connections.
 *
 * <p>Verified behaviours:
 * <ol>
 *   <li>A valid request causes the controller to delegate to {@link ChatUseCase#stream}.</li>
 *   <li>The first SSE event received has type {@code listings}.</li>
 *   <li>Subsequent {@code token} events follow in emission order.</li>
 *   <li>The stream terminates with a single {@code done} event.</li>
 *   <li>A blank {@code query} parameter is rejected with {@code 400 Bad Request}.</li>
 * </ol>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ChatControllerIT {

    @MockBean
    private ChatUseCase chatUseCase;

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Happy path: mock use case emits {@code listings → token × 2 → done}.
     */
    @Test
    @DisplayName("GET /api/v1/chat/stream returns listings → token → done SSE event sequence")
    void stream_shouldReturnSseEventsInOrder() {
        Flux<ServerSentEvent<String>> mockStream = Flux.just(
                ServerSentEvent.<String>builder().event("listings").data("[{\"id\":\"home-1\"}]").build(),
                ServerSentEvent.<String>builder().event("token").data("Results ").build(),
                ServerSentEvent.<String>builder().event("token").data("updated.").build(),
                ServerSentEvent.<String>builder().event("done").data("").build()
        );
        when(chatUseCase.stream(anyString(), anyString())).thenReturn(mockStream);

        var events = webTestClient.get()
                .uri("/api/v1/chat/stream?sessionId=session-it-1&query=3+beds+under+%24500k+in+Austin")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(ServerSentEvent.class)
                .getResponseBody()
                .collectList()
                .block();

        assertThat(events).isNotNull().hasSize(4);

        assertThat(events.get(0).event())
                .as("first event type must be 'listings'")
                .isEqualTo("listings");
        assertThat(String.valueOf(events.get(0).data()))
                .as("listings payload must contain home id")
                .contains("home-1");

        assertThat(events.get(1).event()).as("second event must be 'token'").isEqualTo("token");
        assertThat(events.get(2).event()).as("third event must be 'token'").isEqualTo("token");
        assertThat(events.get(3).event()).as("final event must be 'done'").isEqualTo("done");
    }

    /**
     * Validation scenario: a blank {@code query} parameter is rejected before reaching the use case.
     */
    @Test
    @DisplayName("GET /api/v1/chat/stream returns 400 when query is blank")
    void stream_shouldReturn400WhenQueryIsBlank() {
        webTestClient.get()
                .uri("/api/v1/chat/stream?sessionId=session-it-2&query=")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
