# Research: AI Chat Box

**Phase**: 0 | **Date**: 2026-04-08 | **Branch**: `003-ai-chat-box`

---

## R-001: SSE Streaming from Spring MVC (servlet stack) with Spring AI

**Decision**: Return `Flux<ServerSentEvent<String>>` from a `@RestController` method in a
Spring MVC (non-reactive) application by adding `spring-webflux` as a compile dependency
(not as the web runtime). Spring Boot's auto-configuration detects both and bridges them via
`ReactorHttpHandlerAdapter`.

**Rationale**: All existing services (`home-service`, `assessment-service`) use
`spring-boot-starter-web` (servlet). Switching to `spring-boot-starter-webflux` would require
re-testing all existing blocking code paths. The co-existence pattern (webflux on classpath,
web as the active runtime) is the Spring AI recommended approach for streaming endpoints in
servlet-based apps and is production-supported since Spring Boot 3.2.

**Alternatives considered**:
- Full reactive stack (`spring-boot-starter-webflux`): rejected — inconsistent with existing
  services; introduces reactive chain discipline changes across the whole service.
- `SseEmitter` (Spring MVC native): rejected — does not compose natively with Spring AI's
  `Flux<String>` chatClient streaming; requires manual bridging that is fragile under
  virtual-thread execution.

**Implementation note**: Controller method signature:

```java
@PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> stream(@RequestBody ChatRequestDTO request) { … }
```

`spring-webflux` is added as `implementation` (not `runtimeOnly`) because
`ServerSentEvent` is a webflux type used in the method signature.

---

## R-002: SSE Event Protocol (Frontend ↔ chat-service)

**Decision**: Use two named SSE event types in the stream:

| Event type | Data payload | When emitted |
|------------|--------------|--------------|
| `listings` | JSON array of `HomeResultDTO` | Once, immediately after home-service responds |
| `token`    | Plain string (AI text chunk) | Per AI streaming token |
| `error`    | Plain string error message | On home-service failure or AI failure |
| `done`     | Empty string | When streaming completes normally |

**Rationale**: Separating `listings` from `token` events lets the Angular frontend update the
listings panel without waiting for the full AI text stream to complete — achieving the
"simultaneously updates the listings panel" UX requirement. A single merged event type would
force the frontend to parse JSON vs. text per message, increasing coupling.

**Angular consumption**:

```typescript
const es = new EventSource(url, { withCredentials: false });
es.addEventListener('listings', (e) => updateListings(JSON.parse(e.data)));
es.addEventListener('token',    (e) => appendToken(e.data));
es.addEventListener('done',     ()  => es.close());
es.addEventListener('error',    (e) => showError(e.data));
```

`EventSource` natively supports named event types via `addEventListener` — no custom parsing needed.

---

## R-003: Spring AI Cassandra Chat Memory (following assessment-service pattern)

**Decision**: Use `CassandraChatMemoryRepository` (from
`spring-ai-starter-model-chat-memory-repository-cassandra`) wrapped in
`MessageWindowChatMemory` with `maxMessages=20`, identical to `assessment-service`.

**Configuration** (`chat.yml`):

```yaml
spring:
  ai:
    chat:
      memory:
        cassandra:
          keyspace: chat_keyspace
          messages-column: chat_messages
          table: chat_memory
  cassandra:
    contact-points: easy-apply-chat-cassandra
    port: 9042
    local-datacenter: dc1
    schema-action: create_if_not_exists
    request:
      timeout: 10s
```

**Bean** (`BeanConfiguration.java`):

```java
@Bean
public ChatMemory chatMemory(CassandraChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(20)
        .build();
}
```

**Session key**: `ChatMemory.CONVERSATION_ID` parameter set to the frontend-generated
`sessionId` (UUID string). This key is passed per-request in the `MessageChatMemoryAdvisor`.

**Rationale**: Zero-deviation from `assessment-service` ensures operational parity (same
monitoring, same Cassandra operator runbooks). Spring AI manages schema creation
(`schema-action: create_if_not_exists`), so no hand-rolled CQL is needed beyond keyspace creation.

**Cassandra keyspace init** (`database/init-chat-cassandra/init.cql`):

```cql
CREATE KEYSPACE IF NOT EXISTS chat_keyspace
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
```

---

## R-004: Calling home-service from chat-service (Output Port)

**Decision**: Use Spring's `RestClient` (Spring Boot 3.2+ synchronous HTTP client) for
`HomeSearchAdapter`. Chat-service runs on virtual threads; synchronous blocking calls on
virtual threads are non-blocking at the OS level — no reactive client is needed.

**Endpoint called**: `GET http://home-service/api/v1/homes/search?query={query}`
Returns `List<HomeResponseDTO>` (JSON array).

**Bean wiring**:

```java
@Bean
RestClient homeRestClient(@Value("${app.home-service.url}") String homeServiceUrl) {
    return RestClient.builder().baseUrl(homeServiceUrl).build();
}
```

**Failure handling**: If `home-service` returns a non-2xx status or throws a connection
exception, `HomeSearchAdapter` catches and rethrows as a domain-visible
`HomeSearchUnavailableException`. `ChatApplicationService` catches this and emits a single
`error` SSE event ("Something went wrong. Please try again.") — listings panel unchanged.

**Rationale**: `RestClient` is the preferred Spring Boot 3.x successor to `RestTemplate`.
`WebClient` is not needed because virtual threads eliminate the cost of blocking I/O.
Feign is not in the existing dependency set; adding it would be speculative complexity.

---

## R-005: Angular SSE Implementation Strategy

**Decision**: Use the native browser `EventSource` API wrapped in an Angular service
(`ChatService`) that returns an `Observable<ChatEvent>` using `new Observable(observer => …)`.

**Why not `HttpClient`**: Angular's `HttpClient` with `observe: 'events'` does not support
SSE natively; it buffers the entire response. `EventSource` is the correct browser API for SSE.

**CORS**: `chat-service` must add the gateway origin to its CORS allowed origins. However,
since the Angular app communicates through the HTTPS gateway (`https://localhost:8443`), the
`EventSource` URL must also go through the gateway. The gateway routes
`/api/v1/chat/**` → `chat-service`. SSE passthrough works with Spring Cloud Gateway WebFlux.

**Session ID lifecycle**: Generated once on `HomeListingsComponent` `ngOnInit` using
`crypto.randomUUID()`. Passed with every SSE request. Cleared on component destroy (page
navigation away), which closes the `EventSource` connection.

**Submit lock**: `ChatBoxComponent` sets `isLoading = signal(false)`. Toggled to `true` on
submit; `false` on `done` or `error` event. Submit button's `[disabled]` binding reads this signal.

---

## R-006: Angular Layout — Sidebar Panel Integration

**Decision**: Modify `HomeListingsComponent`'s template to use a CSS Grid or Flexbox two-column
layout: listings grid (left, ~70% width) + chat sidebar (right, ~30% width). `ChatBoxComponent`
is mounted directly inside `HomeListingsComponent` (not a routed outlet).

**Listings update mechanism**: `HomeListingsComponent` exposes a `Signal<Home[]>` (or an
`@Input` / shared service approach). `ChatBoxComponent` calls a method on a shared
`HomeSearchStateService` to push new listings when the `listings` SSE event arrives.
`HomeListingsComponent` subscribes to the same state service.

**Mobile**: On viewports < 768 px, the sidebar stacks below the listings grid (CSS
`flex-direction: column`). Chat panel collapses to full-width.

---

## R-007: Gateway Route Addition

**Decision**: Add a new route to `config-repo/gateway.yml`:

```yaml
- id: chat-service
  uri: http://chat-service
  predicates:
    - Path=/api/v1/chat/**
```

Insert before the catch-all `app-ui` route. No authentication filter is needed on this route
(public endpoint).

**SSE note**: Spring Cloud Gateway WebFlux supports SSE passthrough natively — no additional
configuration required for chunked/streaming responses.
