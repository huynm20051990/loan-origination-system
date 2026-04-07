# Tasks: AI Assistant Chat Box

**Input**: Design documents from `/specs/002-ai-chat-box/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/openapi.yaml ✅, quickstart.md ✅

**Constitution**: Test-First Engineering is MANDATORY — every test task must be written and confirmed FAILING before the corresponding implementation task begins (Red → Green → Refactor).

**Organization**: Tasks are grouped by user story (US1, US2, US3) to enable independent MVP delivery.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no shared dependencies on in-progress tasks)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Exact file paths are included in every description

---

## Phase 1: Setup (Shared Infrastructure) ✅

**Purpose**: Module registration, configuration files, database init scripts, Docker Compose wiring.
These tasks create the scaffolding. No application logic is written here.

- [X] T001 Register `:microservices:chat-service` in `settings.gradle.kts`
- [X] T002 Create `microservices/chat-service/build.gradle.kts` with all dependencies from plan.md (Spring Boot, Spring AI Gemini chat + embedding, pgvector, Cassandra, observability, resilience) and a `Dockerfile` mirroring `home-service`
- [X] T003 [P] Add shared chat DTO records and `ChatAPI.java` interface to `api/src/main/java/com/loan/origination/system/api/core/chat/v1/` (`ChatSessionResponseDTO`, `ChatSessionSummaryDTO`, `ChatMessageRequestDTO`, `ChatMessageResponseDTO`, `ChatStreamChunkDTO`)
- [X] T004 [P] Create `database/init-chat/01.schema.sql` with `chat_sessions`, `chat_messages`, `chat_home_embeddings` tables and HNSW index as defined in `data-model.md`
- [X] T005 [P] Create `database/init-chat-cassandra/init.cql` with `chat_memory` keyspace and `messages` table as defined in `data-model.md`
- [X] T006 [P] Create `config-repo/chat.yml` with server port 7008, PostgreSQL (`chat-pg:5436/chat-db`), Cassandra (`chat_memory` keyspace), Spring AI Gemini, virtual threads, HikariCP, and `home-service.base-url` config as defined in plan.md §5
- [X] T007 [P] Add `chat-service` route (`lb://chat`, `Path=/api/v1/chat/**`, `TokenRelay=`) to `config-repo/gateway.yml`
- [X] T008 Add `chat-service` and `chat-pg` service definitions to `docker-compose.yml` as defined in plan.md §6
- [X] T009 [P] Create `agentic-ai/prompts/chat-agent.st` with system prompt guarding scope to home listings, home buying process, and loan eligibility; includes `searchHomesForChat` tool reference and polite out-of-scope redirect

---

## Phase 2: Foundational (Blocking Prerequisites) ✅

**Purpose**: Hexagonal skeleton — domain models, port interfaces, persistence adapters, Spring AI wiring. MUST be complete before any user story can be implemented.

### Tests — Write First (Red)

- [X] T010 Write failing unit test for `ChatSession` domain model asserting business rule: a session belongs to exactly one userId — `microservices/chat-service/src/test/java/.../domain/model/ChatSessionTest.java`
- [X] T011 [P] Write failing unit test for `ChatMessage` domain model asserting role enum and non-null content — `microservices/chat-service/src/test/java/.../domain/model/ChatMessageTest.java`
- [X] T012 Write failing unit test for `ChatApplicationService` with all port interfaces mocked (Mockito): `createSession()` persists via `ChatSessionRepositoryPort` — `microservices/chat-service/src/test/java/.../application/service/ChatApplicationServiceTest.java`

### Implementation (Green → Refactor)

- [X] T016 [P] Implement `ChatSession` aggregate root domain model — `microservices/chat-service/src/main/java/.../domain/model/ChatSession.java`
- [X] T017 [P] Implement `ChatMessage` entity domain model — `microservices/chat-service/src/main/java/.../domain/model/ChatMessage.java`
- [X] T018 [P] Implement `MessageRole` enum (`USER`, `ASSISTANT`) — `microservices/chat-service/src/main/java/.../domain/vo/MessageRole.java`
- [X] T019 Implement port interfaces: `ChatUseCase` (input), `ChatSessionRepositoryPort`, `ChatMessageRepositoryPort`, `ChatAIPort` (output) — `microservices/chat-service/src/main/java/.../application/port/`
- [X] T020 Implement `ChatApplicationService` skeleton with `createSession()` wired to `ChatSessionRepositoryPort` — `microservices/chat-service/src/main/java/.../application/service/ChatApplicationService.java`
- [X] T021 [P] Implement `ChatSessionEntity` JPA entity (`@Entity`, `@Table("chat_sessions")`) — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/entity/ChatSessionEntity.java`
- [X] T022 [P] Implement `ChatMessageEntity` JPA entity with role `@Enumerated(STRING)` and FK to session — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/entity/ChatMessageEntity.java`
- [X] T023 Implement `ChatPersistenceMapper` (domain ↔ entity) — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/mapper/ChatPersistenceMapper.java`
- [X] T024 [P] Implement `ChatSessionRepository` Spring Data JPA interface with `findByUserId` query — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/repository/ChatSessionRepository.java`
- [X] T025 [P] Implement `ChatMessageRepository` Spring Data JPA interface with `findBySessionIdOrderByTimestampAsc` query — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/repository/ChatMessageRepository.java`
- [X] T026 Implement `ChatSessionPersistenceAdapter` (implements `ChatSessionRepositoryPort`) — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/ChatSessionPersistenceAdapter.java`
- [X] T027 [P] Implement `ChatMessagePersistenceAdapter` (implements `ChatMessageRepositoryPort`) — `microservices/chat-service/src/main/java/.../infrastructure/output/persistence/ChatMessagePersistenceAdapter.java`
- [X] T028 Implement `ChatWebMapper` (domain → DTO for `ChatSessionResponseDTO`, `ChatMessageResponseDTO`) — `microservices/chat-service/src/main/java/.../infrastructure/input/rest/mapper/ChatWebMapper.java`
- [X] T029 Implement `ChatApplication` main class and `BeanConfiguration` (wire `ChatUseCase` bean) — `microservices/chat-service/src/main/java/.../ChatApplication.java` + `infrastructure/config/BeanConfiguration.java`

---

## Phase 3: User Story 1 — Natural Language Home Search (Priority: P1) ✅ MVP

**Goal**: User submits a natural language query → AI interprets it → home listings panel updates → confirmation message appears in chat box (SSE streaming).

### Tests — Write First (Red)

- [X] T031 [US1] Write failing unit test for `HomeSearchChatTools.searchHomesForChat()` (mock `VectorStore`, assert similarity search is called with correct query) — `microservices/chat-service/src/test/java/.../infrastructure/output/ai/tools/HomeSearchChatToolsTest.java`
- [X] T032 [P] [US1] Write failing unit test for `ChatAIAdapter` (mock `ChatClient`, assert `Flux<String>` is returned and Cassandra memory is consulted) — `microservices/chat-service/src/test/java/.../infrastructure/output/ai/ChatAIAdapterTest.java`
- [X] T034 [P] [US1] Write failing Angular unit test for `ChatService` (`createSession()` and `sendMessage()` with mock `HttpClient`, verify SSE token stream is parsed correctly) — `app-ui/src/app/core/services/chat.spec.ts`
- [X] T035 [P] [US1] Write failing Angular unit test for `ChatBoxComponent` (mock `ChatService`, verify side-panel renders, query submit triggers `sendMessage()`, loading indicator appears, streamed tokens are appended) — `app-ui/src/app/components/chat-box/chat-box.component.spec.ts`

### Backend Implementation (Green → Refactor)

- [X] T036 [US1] Implement `HomeSearchChatTools` with `@Tool` method calling `home-service` REST API (`GET /api/v1/homes/search` via `RestClient`) — `microservices/chat-service/src/main/java/.../infrastructure/output/ai/tools/HomeSearchChatTools.java`
- [X] T037 [US1] Implement `ChatAIAdapter` (implements `ChatAIPort`) using Spring AI `ChatClient` with Gemini streaming, `MessageChatMemoryAdvisor` scoped by `sessionId`, and `HomeSearchChatTools` — `microservices/chat-service/src/main/java/.../infrastructure/output/ai/ChatAIAdapter.java`
- [X] T039 [US1] Implement `ChatApplicationService.sendMessage()`: persist user `ChatMessage`, call `ChatAIPort.streamChat()`, persist assembled assistant `ChatMessage` on stream completion — `microservices/chat-service/src/main/java/.../application/service/ChatApplicationService.java`
- [X] T040 [US1] Implement `ChatWebAdapterController` with `POST /sessions` (→ `ChatSessionResponseDTO`) and `POST /sessions/{sessionId}/messages` (→ SSE `SseEmitter`, 403 on ownership mismatch) — `microservices/chat-service/src/main/java/.../infrastructure/input/rest/ChatWebAdapterController.java`

### Frontend Implementation (Green → Refactor)

- [X] T041 [P] [US1] Create TypeScript models `ChatSession` and `ChatMessage` — `app-ui/src/app/core/models/chat-session.ts` + `chat-message.ts`
- [X] T042 [US1] Implement `ChatService` with `createSession(): Observable<ChatSession>` and `sendMessage(sessionId, content)` (SSE parsing via fetch API) — `app-ui/src/app/core/services/chat.ts`
- [X] T043 [US1] Implement `ChatBoxComponent` HTML: fixed right side-panel layout, message bubbles (USER/ASSISTANT), scrollable history area, text input, Send button, loading spinner — `app-ui/src/app/components/chat-box/chat-box.component.html` + `chat-box.component.scss`
- [X] T044 [US1] Implement `ChatBoxComponent` TypeScript: session creation on init, `sendMessage()` wiring SSE token stream to appended assistant bubble, listings panel update trigger on stream completion, loading indicator state — `app-ui/src/app/components/chat-box/chat-box.component.ts`
- [X] T045 [US1] Add `ChatBoxComponent` to `HomeListingsComponent` template — `app-ui/src/app/components/home-listings/home-listings.html` + `home-listings.ts`
- [X] T046 [US1] Verify `auth-interceptor.ts` Bearer token injection covers `/api/v1/chat/**` — already covered by existing interceptor logic

---

## Phase 4: User Story 2 — Persistent Chat Interaction (Priority: P2) ✅

**Goal**: Message history is displayed and preserved within the page session. Follow-up queries refine results without losing prior context.

### Tests — Write First (Red)

- [X] T048 [US2] Write failing unit test for `ChatApplicationService.getMessages()` (assert `ChatMessageRepositoryPort.findBySessionId` is called with ownership check) — add scenario to `ChatApplicationServiceTest.java`

### Backend Implementation

- [X] T051 [US2] Implement `ChatApplicationService.getMessages()` with session ownership check (throw 403 if `userId` mismatch) — `application/service/ChatApplicationService.java`
- [X] T052 [US2] Add `GET /sessions/{sessionId}/messages` endpoint returning `List<ChatMessageResponseDTO>` to `ChatWebAdapterController` — `infrastructure/input/rest/ChatWebAdapterController.java`

### Frontend Implementation

- [X] T053 [US2] Implement `ChatService.getMessages(sessionId): Observable<ChatMessage[]>` — `app-ui/src/app/core/services/chat.ts`
- [X] T054 [US2] Update `ChatBoxComponent` to load and display full message history on session open, with auto-scroll to latest message and USER/ASSISTANT styled bubbles with timestamps — `chat-box.component.ts` + `chat-box.component.html`

---

## Phase 5: User Story 3 — Clear / Reset Chat (Priority: P3) ✅

**Goal**: User clicks a Clear/Reset button, chat history is cleared, and home listings return to the unfiltered default state.

### Tests — Write First (Red)

- [X] T055 [US3] Write failing unit test for `ChatApplicationService.deleteSession()` (assert ownership check + `ChatSessionRepositoryPort.delete()` called, cascade deletes messages) — add scenario to `ChatApplicationServiceTest.java`
- [X] T057 [P] [US3] Write failing Angular component test for Clear/Reset button: clicking it calls `ChatService.deleteSession()`, empties message list, and emits listings-reset event — add scenario to `chat-box.component.spec.ts`

### Backend Implementation

- [X] T058 [US3] Implement `ChatApplicationService.deleteSession()` with ownership check and cascade-delete via `ChatSessionRepositoryPort` — `application/service/ChatApplicationService.java`
- [X] T059 [US3] Add `DELETE /sessions/{sessionId}` endpoint (→ `204 No Content`) to `ChatWebAdapterController` — `infrastructure/input/rest/ChatWebAdapterController.java`

### Frontend Implementation

- [X] T060 [US3] Implement `ChatService.deleteSession(sessionId): Observable<void>` — `app-ui/src/app/core/services/chat.ts`
- [X] T061 [US3] Add Clear/Reset button to `ChatBoxComponent` — on click: call `deleteSession()`, clear message list, emit event to reset listings panel to default unfiltered state, create a new session — `chat-box.component.ts` + `chat-box.component.html`

---

## Phase 6: Polish & Cross-Cutting Concerns ✅

**Purpose**: Rate limiting, error UX, observability, list sessions endpoint.

- [X] T062 [P] Implement `ChatApplicationService.listSessions()` and add `GET /sessions` endpoint (→ `List<ChatSessionSummaryDTO>`, ordered by `createdAt` desc) to `ChatWebAdapterController` — `application/service/ChatApplicationService.java` + `infrastructure/input/rest/ChatWebAdapterController.java`
- [X] T063 [P] Add client-side soft rate-limiting logic to `ChatBoxComponent`: track message timestamps, disable send for 15-second cooldown if 5 messages in 10 seconds exceeded — `app-ui/src/app/components/chat-box/chat-box.component.ts`
- [X] T064 [P] Add rate-limit warning banner and cooldown countdown indicator to `ChatBoxComponent` (FR-013) — `chat-box.component.ts` + `chat-box.component.html` + `chat-box.component.scss`
- [X] T065 [P] Add inline error message and "Try again" button to `ChatBoxComponent` for AI backend failures (FR-010): catch `sendMessage()` errors, display user-friendly message, keep input open — `chat-box.component.ts` + `chat-box.component.html`
- [X] T066 [P] Add `@Timed` Micrometer metrics for AI response latency and message counter to `ChatApplicationService` — `application/service/ChatApplicationService.java`
- [X] T067 [P] Add empty-query guard in `ChatBoxComponent`: disable Send button when input is blank or whitespace (FR-009) — `chat-box.component.ts` + `chat-box.component.html`
- [X] T068 Create Kubernetes Helm chart entry for `chat-service` in `kubernetes/helm/components/chat-service/` mirroring existing component chart structure

---

## Not Implemented (Backlog)

The following planned tasks were not implemented and remain as backlog items:

- [ ] T013 [P] Write integration test for `ChatSessionPersistenceAdapter` using Testcontainers PostgreSQL + pgvector — `microservices/chat-service/src/test/java/.../infrastructure/output/persistence/ChatSessionPersistenceAdapterIT.java`
- [ ] T014 [P] Write integration test for `ChatMessagePersistenceAdapter` using Testcontainers — `microservices/chat-service/src/test/java/.../infrastructure/output/persistence/ChatMessagePersistenceAdapterIT.java`
- [ ] T015 [P] Write Spring Cloud Contract stub for `POST /api/v1/chat/sessions → 201` in `contracts/src/test/resources/contracts/chat/`
- [ ] T033 [P] [US1] Write Spring Cloud Contract stub for `POST /api/v1/chat/sessions/{sessionId}/messages → SSE 200` in `contracts/src/test/resources/contracts/chat/`
- [ ] T037b [P] [US1] Write unit test for `HomeEmbeddingsSyncScheduler` — `microservices/chat-service/src/test/java/.../infrastructure/output/ai/HomeEmbeddingsSyncSchedulerTest.java`
- [ ] T038 [P] [US1] Implement `HomeEmbeddingsSyncScheduler` — `@Scheduled` every 15 minutes, calls `home-service` REST API (`GET /api/v1/homes` via `RestClient`), re-indexes listings into `chat_home_embeddings` via `VectorStore` — `microservices/chat-service/src/main/java/.../infrastructure/output/ai/HomeEmbeddingsSyncScheduler.java`
- [ ] T049 [P] [US2] Write Spring Cloud Contract stub for `GET /api/v1/chat/sessions/{sessionId}/messages → 200` in `contracts/src/test/resources/contracts/chat/`
- [ ] T050 [P] [US2] Write Angular component test verifying second query appends a second USER + ASSISTANT bubble to chat history — add scenario to `chat-box.component.spec.ts`
- [ ] T056 [P] [US3] Write Spring Cloud Contract stub for `DELETE /api/v1/chat/sessions/{sessionId} → 204` in `contracts/src/test/resources/contracts/chat/`
- [ ] T068b [P] Run load test for SC-006: use k6 to simulate 100 concurrent users submitting `POST /sessions/{id}/messages`; assert p95 ≤ 5 s and zero errors — `specs/002-ai-chat-box/load-test/chat-load-test.js`

---

## Notes

- `[P]` = safe to parallelize with other `[P]` tasks in the same phase (different files, no in-progress dependencies)
- `[USX]` maps each task to a specific user story for traceability to `spec.md`
- Constitution hard gates apply at every PR: no Spring annotations in `domain/`, SSE streaming mandatory
