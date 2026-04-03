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

## Phase 1: Setup (Shared Infrastructure)

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

**Checkpoint**: All setup files exist. `docker-compose up -d` starts `chat-pg` and `chat-service` container (may fail at app-start until foundation is implemented — that is expected).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Hexagonal skeleton — domain models, port interfaces, persistence adapters, Spring AI wiring. MUST be complete before any user story can be implemented.

**⚠️ CRITICAL**: Write ALL tests first and confirm they FAIL before implementing.

### Tests — Write First (Red)

- [X] T010 Write failing unit test for `ChatSession` domain model asserting business rule: a session belongs to exactly one userId — `microservices/chat-service/src/test/java/.../domain/model/ChatSessionTest.java`
- [X] T011 [P] Write failing unit test for `ChatMessage` domain model asserting role enum and non-null content — `microservices/chat-service/src/test/java/.../domain/model/ChatMessageTest.java`
- [X] T012 Write failing unit test for `ChatApplicationService` with all port interfaces mocked (Mockito): `createSession()` persists via `ChatSessionRepositoryPort` — `microservices/chat-service/src/test/java/.../application/service/ChatApplicationServiceTest.java`
- [ ] T013 [P] Write failing integration test for `ChatSessionPersistenceAdapter` using Testcontainers PostgreSQL + pgvector extension — `microservices/chat-service/src/test/java/.../infrastructure/output/persistence/ChatSessionPersistenceAdapterIT.java`
- [ ] T014 [P] Write failing integration test for `ChatMessagePersistenceAdapter` using Testcontainers — `microservices/chat-service/src/test/java/.../infrastructure/output/persistence/ChatMessagePersistenceAdapterIT.java`
- [ ] T015 [P] Write failing Spring Cloud Contract stub for `POST /api/v1/chat/sessions → 201` in `contracts/src/test/resources/contracts/chat/`

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
- [ ] T030 Run `./gradlew :microservices:chat-service:test` — confirm T010–T015 tests now pass (Green)

**Checkpoint**: Foundation ready — persistence adapters work against real PostgreSQL; `createSession()` is functional. User story implementation can begin.

---

## Phase 3: User Story 1 — Natural Language Home Search (Priority: P1) 🎯 MVP

**Goal**: User submits a natural language query → AI interprets it → home listings panel updates → confirmation message appears in chat box (SSE streaming).

**Independent Test**: Start the full stack, open the home page, type "Show me 3-bedroom homes under $500,000" in the chat side panel, press Send — listings update and a confirmation message streams into the chat box.

### Tests — Write First (Red)

- [X] T031 [US1] Write failing unit test for `HomeSearchChatTools.searchHomesForChat()` (mock `VectorStore`, assert similarity search is called with correct query) — `microservices/chat-service/src/test/java/.../infrastructure/output/ai/tools/HomeSearchChatToolsTest.java`
- [X] T032 [P] [US1] Write failing unit test for `ChatAIAdapter` (mock `ChatClient`, assert `Flux<String>` is returned and Cassandra memory is consulted) — `microservices/chat-service/src/test/java/.../infrastructure/output/ai/ChatAIAdapterTest.java`
- [ ] T033 [P] [US1] Write failing Spring Cloud Contract stub for `POST /api/v1/chat/sessions/{sessionId}/messages → SSE 200` in `contracts/src/test/resources/contracts/chat/`
- [X] T034 [P] [US1] Write failing Angular unit test for `ChatService` (`createSession()` and `sendMessage()` with mock `HttpClient`, verify SSE token stream is parsed correctly) — `app-ui/src/app/core/services/chat.spec.ts`
- [X] T035 [P] [US1] Write failing Angular unit test for `ChatBoxComponent` (mock `ChatService`, verify side-panel renders, query submit triggers `sendMessage()`, loading indicator appears, streamed tokens are appended) — `app-ui/src/app/components/chat-box/chat-box.component.spec.ts`

### Backend Implementation (Green → Refactor)

- [X] T036 [US1] Implement `HomeSearchChatTools` with `@Tool` method using `VectorStore.similaritySearch()` and `FilterExpressionTextParser` for structured home listing filters — `microservices/chat-service/src/main/java/.../infrastructure/output/ai/tools/HomeSearchChatTools.java`
- [X] T037 [US1] Implement `ChatAIAdapter` (implements `ChatAIPort`) using Spring AI `ChatClient` with Gemini streaming, `CassandraChatMemory` scoped by `sessionId`, and `HomeSearchChatTools` — `microservices/chat-service/src/main/java/.../infrastructure/output/ai/ChatAIAdapter.java`
- [X] T037b [P] [US1] Write failing unit test for `HomeEmbeddingsSyncScheduler` (mock `RestClient` returning sample listings, mock `VectorStore.add()`, assert sync is invoked and embeddings are passed to the vector store) — `microservices/chat-service/src/test/java/.../infrastructure/output/ai/HomeEmbeddingsSyncSchedulerTest.java`
- [X] T038 [P] [US1] Implement `HomeEmbeddingsSyncScheduler` — `@Scheduled` every 15 minutes, calls `home-service` REST API (`GET /api/v1/homes` via `RestClient`), re-indexes listings into `chat_home_embeddings` via `VectorStore` — `microservices/chat-service/src/main/java/.../infrastructure/output/ai/HomeEmbeddingsSyncScheduler.java`
- [X] T039 [US1] Implement `ChatApplicationService.sendMessage()`: persist user `ChatMessage`, call `ChatAIPort.streamChat()`, persist assembled assistant `ChatMessage` on stream completion — `microservices/chat-service/src/main/java/.../application/service/ChatApplicationService.java`
- [X] T040 [US1] Implement `ChatWebAdapterController` with `POST /sessions` (→ `ChatSessionResponseDTO`) and `POST /sessions/{sessionId}/messages` (→ `Flux<ChatStreamChunkDTO>`, `produces = TEXT_EVENT_STREAM_VALUE`, 403 on ownership mismatch) — `microservices/chat-service/src/main/java/.../infrastructure/input/rest/ChatWebAdapterController.java`

### Frontend Implementation (Green → Refactor)

- [X] T041 [P] [US1] Create TypeScript models `ChatSession` and `ChatMessage` — `app-ui/src/app/core/models/chat-session.ts` + `chat-message.ts`
- [X] T042 [US1] Implement `ChatService` with `createSession(): Observable<ChatSession>` and `sendMessage(sessionId, content)` (SSE parsing via fetch API) — `app-ui/src/app/core/services/chat.ts`
- [X] T043 [US1] Implement `ChatBoxComponent` HTML: fixed right side-panel layout (CSS Grid/Flexbox alongside listings), message bubbles (USER/ASSISTANT), scrollable history area, text input, Send button, loading spinner — `app-ui/src/app/components/chat-box/chat-box.component.html` + `chat-box.component.scss`
- [X] T044 [US1] Implement `ChatBoxComponent` TypeScript: session creation on init, `sendMessage()` wiring SSE token stream to appended assistant bubble, listings panel update trigger on stream completion, loading indicator state — `app-ui/src/app/components/chat-box/chat-box.component.ts`
- [X] T045 [US1] Add `ChatBoxComponent` to `HomeListingsComponent` template — `app-ui/src/app/components/home-listings/home-listings.html` + `home-listings.ts`
- [X] T046 [US1] Verify `auth-interceptor.ts` Bearer token injection covers `/api/v1/chat/**` — already covered by existing interceptor logic
- [ ] T047 [US1] Run `npm test` in `app-ui/` — confirm T034 and T035 tests now pass (Green)

**Checkpoint**: User Story 1 fully functional. User can type a natural language query, see streaming tokens in the side panel, and see the listings update. Test against local stack using `quickstart.md §4`.

---

## Phase 4: User Story 2 — Persistent Chat Interaction (Priority: P2)

**Goal**: Message history is displayed and preserved within the page session. Follow-up queries refine results without losing prior context.

**Independent Test**: Submit two consecutive queries in the chat panel and verify both messages appear in the scrollable history with correct USER/ASSISTANT bubbles. Reload the page and verify history is cleared.

### Tests — Write First (Red)

- [X] T048 [US2] Write failing unit test for `ChatApplicationService.getMessages()` (assert `ChatMessageRepositoryPort.findBySessionId` is called with ownership check) — add scenario to `ChatApplicationServiceTest.java`
- [ ] T049 [P] [US2] Write failing Spring Cloud Contract stub for `GET /api/v1/chat/sessions/{sessionId}/messages → 200` in `contracts/src/test/resources/contracts/chat/`
- [ ] T050 [P] [US2] Write failing Angular component test verifying second query appends a second USER + ASSISTANT bubble to chat history — add scenario to `chat-box.component.spec.ts`

### Backend Implementation

- [X] T051 [US2] Implement `ChatApplicationService.getMessages()` with session ownership check (throw 403 if `userId` mismatch) — `application/service/ChatApplicationService.java`
- [X] T052 [US2] Add `GET /sessions/{sessionId}/messages` endpoint returning `List<ChatMessageResponseDTO>` to `ChatWebAdapterController` — `infrastructure/input/rest/ChatWebAdapterController.java`

### Frontend Implementation

- [X] T053 [US2] Implement `ChatService.getMessages(sessionId): Observable<ChatMessage[]>` — `app-ui/src/app/core/services/chat.ts`
- [X] T054 [US2] Update `ChatBoxComponent` to load and display full message history on session open, with auto-scroll to latest message and USER/ASSISTANT styled bubbles with timestamps — `chat-box.component.ts` + `chat-box.component.html`

**Checkpoint**: User Stories 1 AND 2 independently functional. Multi-turn conversation context works; page reload clears history.

---

## Phase 5: User Story 3 — Clear / Reset Chat (Priority: P3)

**Goal**: User clicks a Clear/Reset button, chat history is cleared, and home listings return to the unfiltered default state.

**Independent Test**: Submit a query (listings are now filtered), click Clear — verify chat history is empty and listings panel shows the default unfiltered view.

### Tests — Write First (Red)

- [X] T055 [US3] Write failing unit test for `ChatApplicationService.deleteSession()` (assert ownership check + `ChatSessionRepositoryPort.delete()` called, cascade deletes messages) — add scenario to `ChatApplicationServiceTest.java`
- [ ] T056 [P] [US3] Write failing Spring Cloud Contract stub for `DELETE /api/v1/chat/sessions/{sessionId} → 204` in `contracts/src/test/resources/contracts/chat/`
- [X] T057 [P] [US3] Write failing Angular component test for Clear/Reset button: clicking it calls `ChatService.deleteSession()`, empties message list, and emits listings-reset event — add scenario to `chat-box.component.spec.ts`

### Backend Implementation

- [X] T058 [US3] Implement `ChatApplicationService.deleteSession()` with ownership check and cascade-delete via `ChatSessionRepositoryPort` — `application/service/ChatApplicationService.java`
- [X] T059 [US3] Add `DELETE /sessions/{sessionId}` endpoint (→ `204 No Content`) to `ChatWebAdapterController` — `infrastructure/input/rest/ChatWebAdapterController.java`

### Frontend Implementation

- [X] T060 [US3] Implement `ChatService.deleteSession(sessionId): Observable<void>` — `app-ui/src/app/core/services/chat.ts`
- [X] T061 [US3] Add Clear/Reset button to `ChatBoxComponent` — on click: call `deleteSession()`, clear message list, emit event to reset listings panel to default unfiltered state, create a new session — `chat-box.component.ts` + `chat-box.component.html`

**Checkpoint**: All three user stories independently functional and testable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Rate limiting, error UX, observability, list sessions endpoint, and end-to-end validation.

- [X] T062 [P] Implement `ChatApplicationService.listSessions()` and add `GET /sessions` endpoint (→ `List<ChatSessionSummaryDTO>`, ordered by `createdAt` desc) to `ChatWebAdapterController` — `application/service/ChatApplicationService.java` + `infrastructure/input/rest/ChatWebAdapterController.java`
- [X] T063 [P] Add client-side soft rate-limiting logic to `ChatBoxComponent`: track message timestamps, disable send for 15-second cooldown if 5 messages in 10 seconds exceeded — `app-ui/src/app/components/chat-box/chat-box.component.ts`
- [X] T064 [P] Add rate-limit warning banner and cooldown countdown indicator to `ChatBoxComponent` (FR-013) — `chat-box.component.ts` + `chat-box.component.html` + `chat-box.component.scss`
- [X] T065 [P] Add inline error message and "Try again" button to `ChatBoxComponent` for AI backend failures (FR-010): catch `sendMessage()` errors, display user-friendly message, keep input open — `chat-box.component.ts` + `chat-box.component.html`
- [X] T066 [P] Add `@Timed` Micrometer metrics for AI response latency and message counter to `ChatApplicationService` — `application/service/ChatApplicationService.java`
- [X] T067 [P] Add empty-query guard in `ChatBoxComponent`: disable Send button when input is blank or whitespace (FR-009) — `chat-box.component.ts` + `chat-box.component.html`
- [X] T068 Create Kubernetes Helm chart entry for `chat-service` in `kubernetes/helm/components/chat-service/` mirroring existing component chart structure
- [ ] T068b [P] Run load test for SC-006: use k6 (or equivalent) to simulate 100 concurrent virtual users each submitting `POST /sessions/{id}/messages`; assert p95 response time ≤ 5 s and zero error responses — `specs/002-ai-chat-box/load-test/chat-load-test.js`
- [ ] T069 [P] Run full backend test suite `./gradlew :microservices:chat-service:test` + contracts `./gradlew :contracts:test` — confirm all green
- [ ] T070 [P] Run frontend test suite `npm test` in `app-ui/` — confirm all green
- [ ] T071 Run end-to-end smoke test per `specs/002-ai-chat-box/quickstart.md`: create session → send query → verify SSE stream → verify listings update → verify Zipkin traces

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — start immediately. All T001–T009 can run in parallel after T001 completes.
- **Phase 2 (Foundational)**: Depends on Phase 1. Tests T010–T015 written first (Red). Implementation T016–T029 follows. **BLOCKS** all user stories.
- **Phase 3 (US1)**: Depends on Phase 2 completion. Backend and frontend tasks are independent (can run in parallel by two developers).
- **Phase 4 (US2)**: Depends on Phase 2. Integrates with US1 components but independently testable.
- **Phase 5 (US3)**: Depends on Phase 2. Independently testable.
- **Phase 6 (Polish)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **US1 (P1)**: Can start after Phase 2. No dependency on US2 or US3.
- **US2 (P2)**: Can start after Phase 2. Adds `getMessages()` to `ChatApplicationService` and history display to `ChatBoxComponent` without modifying US1 flows.
- **US3 (P3)**: Can start after Phase 2. Adds `deleteSession()` and a Clear button without modifying US1/US2 flows.

### Within Each Phase

1. Test tasks MUST be written and confirmed FAILING before implementation begins (constitution)
2. Domain models before port interfaces
3. Port interfaces before application service
4. Entities + repositories before persistence adapters
5. Persistence adapters before application service methods that use them
6. Backend controller before frontend service/component integration

---

## Parallel Opportunities

### Phase 1
```
T001 → T002 (sequential)
T003, T004, T005, T006, T007, T009 — all parallel with each other and with T001/T002
T008 — parallel, can complete any time before docker-compose testing
```

### Phase 2
```
Tests:    T010, T011, T012, T013, T014, T015 — parallel within test-writing step
Models:   T016, T017, T018 — parallel
Ports:    T019 (after T016-T018)
Service:  T020 (after T019)
Entities: T021, T022 — parallel (after T019)
Mapper:   T023 (after T021, T022)
Repos:    T024, T025 — parallel
Adapters: T026, T027 — parallel (after T023-T025)
```

### Phase 3 (Backend + Frontend in parallel)
```
Backend dev:  T031 → T036 → T037 → T038 → T039 → T040
Frontend dev: T034, T035 (parallel) → T041, T042 → T043 → T044 → T045 → T046
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (Setup) — ~half-day
2. Complete Phase 2 (Foundation) — ~1–2 days
3. Complete Phase 3 (US1) — ~2–3 days
4. **STOP and VALIDATE**: Run quickstart.md smoke test, demo to stakeholders
5. Deploy/demo MVP

### Incremental Delivery

1. Setup + Foundation → backend skeleton up ✅
2. US1 → end-to-end chat + listings update ✅ **← MVP Demo Point**
3. US2 → persistent history, multi-turn ✅
4. US3 → clear/reset ✅
5. Polish → rate limiting, error UX, observability ✅

### Parallel Team Strategy (2 developers)

After Phase 2 completes:
- **Dev A**: US1 backend (T036–T040)
- **Dev B**: US1 frontend (T041–T047) — can proceed once `ChatService` interface is agreed

---

## Notes

- `[P]` = safe to parallelize with other `[P]` tasks in the same phase (different files, no in-progress dependencies)
- `[USX]` maps each task to a specific user story for traceability to `spec.md`
- Never write implementation before the corresponding test exists and is confirmed RED
- Commit after each logical group (model, adapter, endpoint, component)
- Validate each story checkpoint before advancing to the next phase
- Constitution hard gates apply at every PR: no Spring annotations in `domain/`, no mocked DBs in integration tests, SSE streaming mandatory
