package com.loan.origination.system.microservices.chat.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.microservices.chat.application.port.input.ChatUseCase;
import com.loan.origination.system.microservices.chat.application.port.output.HomeResult;
import com.loan.origination.system.microservices.chat.application.port.output.HomeSearchPort;
import com.loan.origination.system.microservices.chat.infrastructure.output.client.HomeSearchUnavailableException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Application service that implements the {@link ChatUseCase} input port.
 *
 * <p>Orchestrates the full SSE streaming flow:
 * <ol>
 *   <li>Calls {@link HomeSearchPort} to retrieve matching listings for the user's query.</li>
 *   <li>Emits a {@code listings} SSE event containing the serialised JSON array of results.</li>
 *   <li>Builds a natural-language prompt that includes the listing context.</li>
 *   <li>Streams the AI model's reply through the {@link ChatClient}, emitting one {@code token}
 *       SSE event per text fragment.</li>
 *   <li>Emits a final {@code done} SSE event to signal stream completion.</li>
 *   <li>Emits an {@code error} SSE event if {@link HomeSearchUnavailableException} is thrown.</li>
 * </ol>
 *
 * <p>The {@code sessionId} is forwarded to the {@link MessageChatMemoryAdvisor} via its
 * {@code conversationId} advisor parameter so that Cassandra-backed memory is scoped
 * correctly per user session across multiple chat turns.
 */
@Service
public class ChatApplicationService implements ChatUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(ChatApplicationService.class);

    /**
     * Advisor context parameter key used by {@code BaseChatMemoryAdvisor} to resolve the
     * per-request conversation ID. The {@code MessageChatMemoryAdvisor} reads this key from the
     * advisor context map at request time, allowing each SSE call to be scoped to its own session.
     */
    private static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "chat_memory_conversation_id";

    private final HomeSearchPort homeSearchPort;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs the service with required collaborators.
     *
     * @param homeSearchPort the output port for querying home listings from home-service
     * @param chatClient     the Spring AI {@link ChatClient} pre-configured with memory and logging
     *                       advisors
     */
    public ChatApplicationService(HomeSearchPort homeSearchPort, ChatClient chatClient) {
        this.homeSearchPort = homeSearchPort;
        this.chatClient = chatClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Emits events in strict order: {@code listings} → one or more {@code token} → {@code done}.
     * If home-service is unavailable, emits a single {@code error} event instead and terminates.
     */
    @Override
    public Flux<ServerSentEvent<String>> stream(String sessionId, String query) {
        List<HomeResult> homes;
        try {
            homes = homeSearchPort.search(query);
        } catch (HomeSearchUnavailableException ex) {
            LOG.warn(
                    "home-service unavailable for sessionId={}, query={}; emitting error event",
                    sessionId,
                    query,
                    ex);
            return Flux.just(
                    ServerSentEvent.<String>builder()
                            .event("error")
                            .data("home-service unavailable")
                            .build());
        }

        String listingsJson = serializeHomes(homes);

        ServerSentEvent<String> listingsEvent =
                ServerSentEvent.<String>builder()
                        .event("listings")
                        .data(listingsJson)
                        .build();

        String prompt = buildPrompt(query, homes);

        Flux<ServerSentEvent<String>> tokenFlux =
                chatClient
                        .prompt()
                        .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
                        .user(prompt)
                        .stream()
                        .content()
                        .map(
                                token ->
                                        ServerSentEvent.<String>builder()
                                                .event("token")
                                                .data(token)
                                                .build());

        ServerSentEvent<String> doneEvent =
                ServerSentEvent.<String>builder().event("done").data("").build();

        return Flux.concat(Flux.just(listingsEvent), tokenFlux, Flux.just(doneEvent));
    }

    /**
     * Serialises the list of {@link HomeResult} records to a JSON string for the {@code listings}
     * SSE payload.
     *
     * <p>Falls back to an empty JSON array ({@code "[]"}) if serialisation fails, rather than
     * propagating a checked exception into the reactive pipeline.
     *
     * @param homes the list of home results returned by the search port
     * @return JSON string representation of the list
     */
    private String serializeHomes(List<HomeResult> homes) {
        try {
            return objectMapper.writeValueAsString(homes);
        } catch (JsonProcessingException ex) {
            LOG.error("Failed to serialise home results to JSON; falling back to empty array", ex);
            return "[]";
        }
    }

    /**
     * Builds a natural-language prompt that includes the listing search context.
     *
     * <p>When no results are found, the prompt instructs the model to inform the user that their
     * criteria matched nothing. When results are present, the prompt asks the model to summarise
     * the key details of each listing for the user.
     *
     * @param query the user's original natural-language query
     * @param homes the list of matching home results (may be empty)
     * @return the composed prompt string to send to the AI model
     */
    private String buildPrompt(String query, List<HomeResult> homes) {
        if (homes.isEmpty()) {
            return String.format(
                    "The user searched for: \"%s\". No matching listings were found. "
                            + "Inform the user that no listings matched their criteria and suggest "
                            + "they try adjusting their search terms.",
                    query);
        }
        return String.format(
                "The user searched for: \"%s\". %d matching listing(s) were found and have been "
                        + "displayed above. Briefly summarise the results and highlight key details "
                        + "such as price, bedrooms, and location. End with: "
                        + "\"Results updated. Please review above.\"",
                query, homes.size());
    }
}
