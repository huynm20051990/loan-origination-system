package com.loan.origination.system.microservices.chat.application.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.loan.origination.system.microservices.chat.infrastructure.output.client.HomeSearchAdapter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.cassandra.CassandraChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.client.RestClient;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Full-stack integration test for {@link ChatApplicationService}.
 *
 * <p>Exercises the complete flow end-to-end using real infrastructure:
 * <ul>
 *   <li><strong>Cassandra (Testcontainers)</strong> — real {@link CassandraChatMemoryRepository}
 *       backed by an ephemeral {@code cassandra:4.1.5} container; verifies that the user query
 *       and assembled assistant reply are durably persisted after the stream completes.</li>
 *   <li><strong>WireMock</strong> — simulates the home-service HTTP API so the test is
 *       deterministic and isolated from the real service.</li>
 *   <li><strong>Mocked AI model</strong> — the underlying {@link StreamingChatModel} is replaced
 *       with a Mockito mock that emits two deterministic tokens; the real {@link ChatClient} and
 *       {@link MessageChatMemoryAdvisor} run normally so Cassandra write-back is exercised.</li>
 * </ul>
 *
 * <p>This test is written in the <em>Red</em> phase and will fail to compile until
 * {@link ChatApplicationService} is created in T023 and
 * {@code HomeSearchAdapter} / {@code HomeSearchUnavailableException} are created in T024/T025.
 */
@Testcontainers
class ChatApplicationServiceIT {

    /** Ephemeral Cassandra node; started once per JVM by {@link Testcontainers}. */
    @Container
    static final CassandraContainer<?> cassandra =
            new CassandraContainer<>("cassandra:4.1.5");

    /** JSON payload returned by the WireMock home-service stub for all search calls. */
    private static final String HOMES_JSON = """
            [
              {
                "id": "home-it-1",
                "price": 490000,
                "beds": 3,
                "baths": 2.0,
                "sqft": 1750,
                "imageUrl": "https://img.example.com/it-1",
                "address": {
                  "street": "10 Test Lane",
                  "city": "Austin",
                  "state": "TX",
                  "zip": "78705"
                },
                "status": "active",
                "description": "IT test home"
              }
            ]
            """;

    static WireMockServer wireMock;
    static CassandraChatMemoryRepository cassandraRepo;
    static ChatApplicationService chatApplicationService;

    /**
     * One-time setup: starts WireMock, initialises the Cassandra schema, and wires together
     * the full object graph under test.
     *
     * <p>The AI model is replaced with a Mockito mock that streams two deterministic tokens
     * ({@code "Results "} and {@code "updated. Please review above."}).
     * All other components — {@link MessageChatMemoryAdvisor}, {@link CassandraChatMemoryRepository},
     * and the {@link HomeSearchAdapter} — are real instances so that the integration path
     * including Cassandra write-back is fully exercised.
     */
    @BeforeAll
    @SuppressWarnings("unchecked")
    static void setUpAll() {
        // --- WireMock: simulates the home-service search endpoint ---
        wireMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMock.start();

        // --- Cassandra: bootstrap keyspace in the Testcontainers node ---
        InetSocketAddress contactPoint = cassandra.getContactPoint();
        String localDc = cassandra.getLocalDatacenter();

        try (CqlSession bootstrapSession = CqlSession.builder()
                .addContactPoint(contactPoint)
                .withLocalDatacenter(localDc)
                .build()) {
            bootstrapSession.execute(
                    "CREATE KEYSPACE IF NOT EXISTS chat_keyspace "
                            + "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}");
        }

        // --- CassandraChatMemoryRepository: scoped to chat_keyspace; schema auto-initialised ---
        CqlSession memorySession = CqlSession.builder()
                .addContactPoint(contactPoint)
                .withLocalDatacenter(localDc)
                .withKeyspace("chat_keyspace")
                .build();

        cassandraRepo = CassandraChatMemoryRepository.builder()
                .cqlSession(memorySession)
                .initializeSchema(true)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(cassandraRepo)
                .maxMessages(20)
                .build();

        // --- AI model mock: implements both ChatModel and StreamingChatModel ---
        ChatModel mockModel = mock(ChatModel.class,
                withSettings().extraInterfaces(StreamingChatModel.class));
        StreamingChatModel streamingModel = (StreamingChatModel) mockModel;
        when(streamingModel.stream(any(Prompt.class))).thenReturn(
                Flux.just(
                        new ChatResponse(List.of(new Generation("Results "))),
                        new ChatResponse(List.of(new Generation("updated. Please review above.")))));

        // Real ChatClient with real MessageChatMemoryAdvisor so Cassandra write-back is tested
        ChatClient chatClient = ChatClient.builder(mockModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        // --- HomeSearchAdapter: points to WireMock ---
        RestClient homeRestClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMock.port())
                .build();
        HomeSearchAdapter homeSearchAdapter = new HomeSearchAdapter(homeRestClient);

        // --- System under test ---
        chatApplicationService = new ChatApplicationService(homeSearchAdapter, chatClient);
    }

    /** Stops WireMock after all tests have finished. */
    @AfterAll
    static void tearDownAll() {
        if (wireMock != null) {
            wireMock.stop();
        }
    }

    /**
     * Resets WireMock stubs before each test to ensure isolation, then registers the default
     * happy-path stub that returns one matching listing.
     */
    @BeforeEach
    void resetStubs() {
        wireMock.resetAll();
        wireMock.stubFor(
                get(urlPathEqualTo("/api/v1/homes/search"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(HOMES_JSON)));
    }

    /**
     * Full flow: query → home results fetched via WireMock → AI tokens streamed from mocked model
     * → {@code done} event.
     *
     * <p>Asserts two things:
     * <ol>
     *   <li><strong>SSE event ordering</strong> — {@code listings} arrives first (containing
     *       {@code home-it-1}), at least one {@code token} follows, and the stream ends with
     *       {@code done}.</li>
     *   <li><strong>Cassandra persistence</strong> — after the stream completes,
     *       {@link CassandraChatMemoryRepository#findByConversationId} returns exactly two
     *       messages: the user query and the assembled assistant reply, in that order.</li>
     * </ol>
     */
    @Test
    @DisplayName("stream() emits listings → tokens → done and persists user + assistant messages in Cassandra")
    void stream_shouldEmitFullEventSequenceAndPersistMessagesInCassandra() {
        // Given: a unique session to avoid cross-test Cassandra state pollution
        String sessionId = "it-cassandra-" + System.nanoTime();
        String query = "3 beds under $500k in Austin";

        // When: consume the full SSE stream synchronously
        List<ServerSentEvent<String>> events = chatApplicationService.stream(sessionId, query)
                .collectList()
                .block(Duration.ofSeconds(15));

        // Then — SSE event ordering contract
        assertThat(events)
                .as("stream must emit at least 3 events: listings, ≥1 token, done")
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(3);

        assertThat(events.get(0).event())
                .as("first event type must be 'listings'")
                .isEqualTo("listings");
        assertThat(events.get(0).data())
                .as("listings payload must reference the WireMock stub result")
                .contains("home-it-1");

        long tokenCount = events.stream()
                .filter(e -> "token".equals(e.event()))
                .count();
        assertThat(tokenCount)
                .as("at least one 'token' event must be emitted from the AI model")
                .isGreaterThanOrEqualTo(1);

        assertThat(events.get(events.size() - 1).event())
                .as("final event type must be 'done'")
                .isEqualTo("done");

        // Then — Cassandra persistence contract
        List<Message> stored = cassandraRepo.findByConversationId(sessionId);
        assertThat(stored)
                .as("Cassandra must store exactly 2 messages: the user query and the assistant reply")
                .hasSize(2);
        assertThat(stored.get(0).getMessageType())
                .as("first stored message must be the user query")
                .isEqualTo(MessageType.USER);
        assertThat(stored.get(1).getMessageType())
                .as("second stored message must be the assistant reply")
                .isEqualTo(MessageType.ASSISTANT);
    }
}
