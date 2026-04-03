# task.md — Chat Feature Implementation Tasks

Each task is atomic, independently verifiable, and maps to a specific layer or concern. Complete tasks in order within each phase. Mark each task done before starting the next.

---

## Phase 0 — Specification Validation (Ralph Loop #1)

> Gate: All specs reviewed and agreed before any code is written.

- [ ] **T-00-01** Read `constitutions.md` in full and confirm no conflicts with existing codebase patterns
- [ ] **T-00-02** Read `spec.md` and verify all API endpoints are reachable through the existing API Gateway route config
- [ ] **T-00-03** Read `plan.md` and confirm port `7008` and DB port `5436` are not already in use in `docker-compose.yml`
- [ ] **T-00-04** Confirm `cassandra` service is available in `docker-compose.yml` (shared with `assessment-service`)
- [ ] **T-00-05** Confirm `gemini-embedding-001` and `gemini-2.5-flash` model names match what is used in `home-service` and `assessment-service` configs

---

## Phase 1 — Shared API Contracts (`api/` module)

> Gate: DTOs and interface compile cleanly. No implementation yet.

- [ ] **T-01-01** Create package `com.loan.origination.system.api.core.chat.v1` in `api/` module
- [ ] **T-01-02** Add `ChatAPI.java` interface with `@RequestMapping("/api/v1/chat")` and all 5 endpoint method signatures
- [ ] **T-01-03** Add `ChatSessionResponseDTO` record (sessionId, userId, createdAt)
- [ ] **T-01-04** Add `ChatSessionSummaryDTO` record (sessionId, createdAt, messageCount)
- [ ] **T-01-05** Add `ChatMessageRequestDTO` record with `@NotBlank String content`
- [ ] **T-01-06** Add `ChatMessageResponseDTO` record (messageId, role, content, timestamp)
- [ ] **T-01-07** Add `ChatStreamChunkDTO` record (token, done, messageId)
- [ ] **T-01-08** Run `./gradlew :api:build` — must pass with no errors

---

## Phase 2 — Database Setup

> Gate: Database initializes cleanly with Docker Compose.

- [ ] **T-02-01** Create `database/init-chat/01.schema.sql` with `chat_sessions`, `chat_messages`, and `chat_home_embeddings` tables plus all indexes (from `plan.md §2.1`)
- [ ] **T-02-02** Create `database/init-chat-cassandra/init.cql` with `chat_memory` keyspace and `messages` table
- [ ] **T-02-03** Add `chat-pg` service to `docker-compose.yml` with healthcheck, port `5436:5432`, and volume mount
- [ ] **T-02-04** Verify: `docker-compose up chat-pg -d` starts cleanly and `pg_isready` healthcheck passes
- [ ] **T-02-05** Verify: SQL schema applied correctly — connect and `\dt` shows all three tables

---

## Phase 3 — `chat-service` Gradle Module

> Gate: Empty Spring Boot app builds and starts without errors.

- [ ] **T-03-01** Create directory `microservices/chat-service/` with `build.gradle.kts` matching dependencies from `plan.md §1.3`
- [ ] **T-03-02** Register `:microservices:chat-service` in root `settings.gradle.kts`
- [ ] **T-03-03** Create `ChatApplication.java` with `@SpringBootApplication`
- [ ] **T-03-04** Create `Dockerfile` (copy from `home-service`, no changes needed)
- [ ] **T-03-05** Create `config-repo/chat.yml` with all configuration from `plan.md §5`
- [ ] **T-03-06** Run `./gradlew :microservices:chat-service:build` — must pass

---

## Phase 4 — Domain Layer

> Gate: Domain classes compile with zero Spring/JPA imports.

- [ ] **T-04-01** Create `MessageRole.java` enum with `USER`, `ASSISTANT`
- [ ] **T-04-02** Create `ChatSession.java` domain model (id, userId, createdAt) — plain Java, no annotations
- [ ] **T-04-03** Create `ChatMessage.java` domain model (id, sessionId, role, content, timestamp) — plain Java, no annotations
- [ ] **T-04-04** Verify: grep for `import org.springframework` in domain package — must return nothing

---

## Phase 5 — Application Layer (Ports & Use Case)

> Gate: Interfaces and service compile. No infrastructure dependencies.

- [ ] **T-05-01** Create `ChatUseCase.java` (inbound port) with all methods matching spec endpoints:
  - `createSession(String userId): ChatSession`
  - `sendMessage(UUID sessionId, String userId, String content): Flux<String>`
  - `getMessages(UUID sessionId, String userId): List<ChatMessage>`
  - `getSessions(String userId): List<ChatSessionSummary>`
  - `deleteSession(UUID sessionId, String userId): void`
- [ ] **T-05-02** Create `ChatSessionRepositoryPort.java` (outbound port) with: save, findById, findByUserId, deleteById, countMessagesBySessionId
- [ ] **T-05-03** Create `ChatMessageRepositoryPort.java` (outbound port) with: save, findBySessionIdOrderByTimestamp
- [ ] **T-05-04** Create `ChatAIPort.java` (outbound port): `streamChat(UUID sessionId, String userMessage): Flux<String>`
- [ ] **T-05-05** Create `ChatApplicationService.java` implementing `ChatUseCase`:
  - Validates session ownership (throws `ForbiddenException` if `userId` mismatch)
  - Delegates persistence to repository ports
  - Delegates streaming to `ChatAIPort`
  - Saves user message before calling AI, saves assembled assistant message on stream completion
- [ ] **T-05-06** Verify: `import` statements in `ChatApplicationService` contain only `java.*`, `reactor.*`, and port interfaces — no Spring, JPA, or AI library imports

---

## Phase 6 — Infrastructure: Persistence Adapters

> Gate: CRUD operations work against `chat-pg`.

- [ ] **T-06-01** Create `ChatSessionEntity.java` JPA entity for `chat_sessions` table
- [ ] **T-06-02** Create `ChatMessageEntity.java` JPA entity for `chat_messages` table
- [ ] **T-06-03** Create `ChatSessionRepository.java` Spring Data JPA repository with `findAllByUserIdOrderByCreatedAtDesc(String userId)`
- [ ] **T-06-04** Create `ChatMessageRepository.java` Spring Data JPA repository with `findBySessionIdOrderByTimestampAsc(UUID sessionId)` and `countBySessionId(UUID sessionId)`
- [ ] **T-06-05** Create `ChatPersistenceMapper.java` with entity ↔ domain mapping methods
- [ ] **T-06-06** Create `ChatSessionPersistenceAdapter.java` implementing `ChatSessionRepositoryPort`
- [ ] **T-06-07** Create `ChatMessagePersistenceAdapter.java` implementing `ChatMessageRepositoryPort`
- [ ] **T-06-08** Integration test: start `chat-pg` via Docker Compose, run a test that creates a session and message, reads them back, and verifies content

---

## Phase 7 — Infrastructure: AI Adapter

> Gate: AI streaming works end-to-end in isolation.

- [ ] **T-07-01** Create `agentic-ai/prompts/chat-agent.st` with system prompt from `plan.md §9`
- [ ] **T-07-02** Create `HomeSearchChatTools.java` with `@Tool searchHomesForChat(String query, String filterString)` using `VectorStore` and `FilterExpressionTextParser`
- [ ] **T-07-03** Create `ChatAIAdapter.java` implementing `ChatAIPort`:
  - Reads system prompt from `chat-agent.st` file path
  - Builds `ChatClient` with `CassandraChatMemory` keyed by `sessionId`
  - Registers `HomeSearchChatTools` as tool
  - Returns `Flux<String>` from `chatClient.prompt().stream().content()`
- [ ] **T-07-04** Create scheduled job `HomeEmbeddingSyncJob.java`:
  - Calls `GET http://home-service/api/v1/homes` via `RestClient`
  - Converts responses to Documents (same format as `home-service` `mapToDocument()`)
  - Upserts into `chat_home_embeddings` via `VectorStore.add()`
  - Schedule: `@Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)`
- [ ] **T-07-05** Manual test: Start service with valid `GEMINI_API_KEY`, call the AI adapter directly with a test prompt, verify token stream returned

---

## Phase 8 — Infrastructure: REST Controller

> Gate: All 5 API endpoints pass Postman/curl tests with correct status codes.

- [ ] **T-08-01** Create `ChatWebMapper.java` with domain ↔ DTO mapping methods
- [ ] **T-08-02** Create `ChatWebAdapterController.java` implementing `ChatAPI`:
  - Extract `userId` from `Authentication` principal (JWT subject)
  - `POST /sessions` → `createSession` → 201
  - `POST /sessions/{id}/messages` → `sendMessage` → 200 SSE stream (`text/event-stream`)
  - `GET /sessions/{id}/messages` → `getMessages` → 200
  - `GET /sessions` → `getSessions` → 200
  - `DELETE /sessions/{id}` → `deleteSession` → 204
- [ ] **T-08-03** Verify SSE stream: `curl -N -H "Authorization: Bearer $TOKEN" -X POST .../sessions/{id}/messages -d '{"content":"test"}'` — tokens stream in real time
- [ ] **T-08-04** Verify 403: calling `/sessions/{otherId}/messages` with wrong user returns 403
- [ ] **T-08-05** Verify 404: calling `/sessions/nonexistent/messages` returns 404

---

## Phase 9 — Wiring & Configuration

> Gate: Service starts fully wired with no bean definition errors.

- [ ] **T-09-01** Create `BeanConfiguration.java` defining all beans: mappers, application service, REST client for home-service
- [ ] **T-09-02** Add `chat-service` service definition to `docker-compose.yml` from `plan.md §6.1`
- [ ] **T-09-03** Add route to `config-repo/gateway.yml` for `/api/v1/chat/**` → `lb://chat`
- [ ] **T-09-04** Start full stack: `docker-compose up -d` — all services healthy
- [ ] **T-09-05** Verify gateway routing: `curl -k https://localhost:8443/api/v1/chat/sessions -H "Authorization: Bearer $TOKEN"` returns 200

---

## Phase 10 — Frontend: ChatService & ChatBoxComponent

> Gate: Chat box renders, sends a message, and displays a streaming response.

- [ ] **T-10-01** Create `app-ui/src/app/core/services/chat.ts` — `ChatService` with `createSession()`, `sendMessage()` (SSE), `getMessages()`
- [ ] **T-10-02** Create `app-ui/src/app/features/chat/models/` — TypeScript interfaces for `ChatSession` and `ChatMessage`
- [ ] **T-10-03** Create `ChatBoxComponent` with toggle button (floating bottom-right), message list, text input, and send button
- [ ] **T-10-04** Wire `ChatBoxComponent` to `ChatService`: on first open → `createSession()`, on send → `sendMessage()` with SSE token append
- [ ] **T-10-05** Add `ChatBoxComponent` to `HomeComponent` template (lazy-loaded)
- [ ] **T-10-06** Run `npm test` in `app-ui/` — all existing tests still pass
- [ ] **T-10-07** Manual E2E test: open browser at https://localhost:8443, log in, see chat bubble, open chat, ask "What homes are available in Austin?", see streaming response with listing details

---

## Phase 11 — Observability

> Gate: Traces visible in Zipkin; metrics visible in Prometheus.

- [ ] **T-11-01** Verify Zipkin traces: after sending a chat message, find trace in http://localhost:9411 showing `chat-service` span
- [ ] **T-11-02** Verify Prometheus metrics: `curl http://localhost:7008/actuator/prometheus | grep chat` shows `chat_sessions_created_total` and `chat_messages_total`
- [ ] **T-11-03** Add `chat-service` scrape config to `config-repo/prometheus.yml`

---

## Phase 12 — Final Validation (Ralph Loop #4)

> Gate: All spec requirements verified. No deviations without spec update.

- [ ] **T-12-01** Walk through every user story in `spec.md §2` and verify each passes manually
- [ ] **T-12-02** Verify every API endpoint in `spec.md §3` for correct status codes, response shapes, and error cases
- [ ] **T-12-03** Verify session isolation: create two sessions with two different tokens, confirm neither can access the other's messages
- [ ] **T-12-04** Verify AI scope restriction: ask the chat agent an out-of-scope question (e.g., "What is the capital of France?") and confirm polite refusal
- [ ] **T-12-05** Verify constitution compliance: run `grep -r "import org.springframework" microservices/chat-service/src/main/java/**/domain` — must return nothing
- [ ] **T-12-06** Update `CLAUDE.md` service map to include `chat-service` (port 7008, PostgreSQL:5436, Cassandra)
