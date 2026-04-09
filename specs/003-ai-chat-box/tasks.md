# Tasks: AI Chat Box

**Input**: Design documents from `/specs/003-ai-chat-box/`
**Branch**: `003-ai-chat-box` | **Date**: 2026-04-08
**Prerequisites**: plan.md ✅ spec.md ✅ research.md ✅ data-model.md ✅ contracts/ ✅ quickstart.md ✅

**Tests**: Included — Constitution §III mandates Red-Green-Refactor. All test tasks must FAIL before implementation begins.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no shared dependencies)
- **[Story]**: Maps to user story in spec.md (US1, US2, US3)

---

## Phase 1: Setup (Project Scaffolding)

**Purpose**: Register the new module and create all infrastructure files before any Java/TypeScript source is written.

- [x] T001 Add `include("microservices:chat-service")` to `settings.gradle.kts`
- [x] T002 Create `microservices/chat-service/build.gradle.kts` — Spring Boot 3.5.x, Spring AI 1.1.2, `spring-boot-starter-webflux` (SSE support), `spring-ai-starter-model-google-genai`, `spring-ai-starter-model-chat-memory-repository-cassandra`, Micrometer, Brave tracing. No JPA dependency.
- [x] T003 Create `microservices/chat-service/src/main/java/com/loan/origination/system/microservices/chat/ChatServiceApplication.java`
- [x] T004 [P] Add `ChatAPI.java` to `api/src/main/java/com/loan/origination/system/api/core/chat/v1/ChatAPI.java` — copy from `specs/003-ai-chat-box/contracts/ChatAPI.java`
- [x] T005 [P] Add `ChatRequestDTO.java` to `api/src/main/java/com/loan/origination/system/api/core/chat/dto/ChatRequestDTO.java` — copy from `specs/003-ai-chat-box/contracts/ChatRequestDTO.java`
- [x] T006 [P] Create `config-repo/chat.yml` — port 7007, `spring.threads.virtual.enabled: true`, Gemini 2.5 Flash, Cassandra chat memory (`chat_keyspace`, `chat_memory` table), `app.home-service.url: http://home-service`
- [x] T007 [P] Create `database/init-chat-cassandra/init.cql` — `CREATE KEYSPACE IF NOT EXISTS chat_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};`
- [x] T008 Add `chat-cassandra` service to `docker-compose.yml` — `cassandra:latest`, `chat_keyspace` init volume, healthcheck `cqlsh -e 'describe keyspaces'`
- [x] T009 Add `chat-service` service to `docker-compose.yml` — depends on `chat-cassandra` (healthy) and `auth-server` (healthy), mounts `config-repo/`, env vars `SPRING_AI_GOOGLE_GENAI_API_KEY`
- [x] T010 [P] Add gateway route to `config-repo/gateway.yml` — `id: chat-service`, `uri: http://chat-service`, `Path=/api/v1/chat/**` (insert before the `app-ui` catch-all route)

**Checkpoint**: Module registered, all infra files created. Run `./gradlew :microservices:chat-service:compileJava` — should compile an empty application.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain types, ports, beans, and Angular shared services that ALL user stories depend on. No user story work begins until this phase is complete.

**⚠️ CRITICAL**: These tasks block all Phase 3+ work.

- [x] T011 [P] Create `microservices/chat-service/src/main/java/.../chat/domain/model/ChatTurn.java` — pure POJO record: `sessionId`, `query`, `reply`, `timestamp`. Zero framework annotations.
- [x] T012 [P] Create `microservices/chat-service/src/main/java/.../chat/application/port/output/HomeResult.java` — plain Java record: `id`, `price`, `beds`, `baths`, `sqft`, `imageUrl`, `address` (nested record: `street`, `city`, `state`, `zip`), `status`, `description`. Not a domain entity — port contract type only, never persisted.
- [x] T013 Create `microservices/chat-service/src/main/java/.../chat/application/port/output/HomeSearchPort.java` — interface: `List<HomeResult> search(String query)` (depends on T012)
- [x] T014 [P] Create `microservices/chat-service/src/main/java/.../chat/application/port/input/ChatUseCase.java` — interface: `Flux<ServerSentEvent<String>> stream(String sessionId, String query)`
- [x] T015 Create `microservices/chat-service/src/main/java/.../chat/infrastructure/config/BeanConfiguration.java` — `@Bean ChatMemory chatMemory(CassandraChatMemoryRepository)` → `MessageWindowChatMemory(maxMessages=20)`; `@Bean ChatClient`  with `MessageChatMemoryAdvisor` and `SimpleLoggerAdvisor`; `@Bean RestClient homeRestClient(@Value("${app.home-service.url}") String url)`
- [x] T016 [P] Create `app-ui/src/app/core/models/chat.ts` — `ChatMessage` interface: `role: 'user' | 'assistant'`, `content: string`, `isStreaming: boolean`
- [x] T017 Create `app-ui/src/app/core/services/home-search-state.ts` — `HomeSearchStateService` with `homes = signal<Home[]>([])` and `isLoading = signal<boolean>(false)`; methods `updateHomes(homes: Home[])`, `setLoading(v: boolean)`, `reset()` (calls `HomeService.getHomes()` and updates signal)

**Checkpoint**: Compile check — `./gradlew :microservices:chat-service:compileJava`. All ports and config beans must compile without errors.

---

## Phase 3: User Story 1 — Filter Listings (Priority: P1) 🎯 MVP

**Goal**: User submits a natural language query via the chat sidebar → listings panel updates with filtered results → chat displays "Results updated. Please review above."

**Independent Test**: Submit `"3 beds under $500k in Austin"` via the chat box → listings panel shows filtered results without page reload → loading spinner appears then disappears → chat shows confirmation message.

### Tests for US1 — Write FIRST, verify they FAIL before implementing

- [x] T018 [P] [US1] Create `microservices/chat-service/src/test/java/.../chat/application/service/ChatApplicationServiceTest.java` — Mockito: mock `HomeSearchPort` returns 2 results; mock `ChatClient` streams 3 tokens; assert `listings` event emitted first, then `token` events, then `done`
- [x] T019 [P] [US1] Create `microservices/chat-service/src/test/java/.../chat/infrastructure/output/client/HomeSearchAdapterIT.java` — WireMock stubs `GET /api/v1/homes/search?query=…` returning JSON array; assert `HomeResult` list deserialized correctly; assert `HomeSearchUnavailableException` thrown on 503
- [x] T020 [P] [US1] Create `microservices/chat-service/src/test/java/.../chat/infrastructure/input/rest/ChatControllerIT.java` — `@SpringBootTest` + `WebTestClient`; POST `/api/v1/chat/stream`; assert SSE event sequence: `listings` arrives before any `token`, stream ends with `done`
- [x] T021 [P] [US1] Create `microservices/chat-service/src/test/java/.../chat/application/service/ChatApplicationServiceIT.java` — Testcontainers Cassandra + WireMock; full flow: query → home results → tokens → `done`; verify `CassandraChatMemoryRepository` contains the user + assistant messages after stream completes
- [ ] T022 [P] [US1] Create `app-ui/src/app/core/services/chat.spec.ts` — mock `EventSource`; assert `listings` event triggers `Observable` emission with parsed `Home[]`; assert `token` events emit string chunks; assert `done` closes connection

### Implementation for US1

- [ ] T023 [US1] Create `microservices/chat-service/src/main/java/.../chat/application/service/ChatApplicationService.java` — implements `ChatUseCase`; calls `HomeSearchPort.search(query)` → emits `listings` `ServerSentEvent`; builds prompt with results; calls `ChatClient` stream with `MessageChatMemoryAdvisor(sessionId)` → emits `token` events; emits `done`; on `HomeSearchUnavailableException` emits `error` event
- [ ] T024 [P] [US1] Create `microservices/chat-service/src/main/java/.../chat/infrastructure/output/client/HomeSearchAdapter.java` — implements `HomeSearchPort`; `RestClient GET {home-service-url}/api/v1/homes/search?query={query}` → deserialize response body directly to `List<HomeResult>`; catch `RestClientException` and rethrow as `HomeSearchUnavailableException`
- [ ] T025 [US1] Create `microservices/chat-service/src/main/java/.../chat/infrastructure/output/client/HomeSearchUnavailableException.java` — unchecked domain exception; message: `"home-service unavailable"`
- [ ] T026 [US1] Create `microservices/chat-service/src/main/java/.../chat/infrastructure/input/rest/ChatController.java` — implements `ChatAPI`; `@RestController`; delegates `stream(request)` to `ChatUseCase`; validates `@Valid @RequestBody`; annotated with `SLF4J LOG`
- [ ] T027 [P] [US1] Create `app-ui/src/app/core/services/chat.ts` — `ChatService`; `stream(sessionId, query)` generates `POST /api/v1/chat/stream` URL, creates `EventSource` (or `fetch` SSE); returns `Observable<{type: string, data: string}>`; listeners for `listings`, `token`, `done`, `error`; closes `EventSource` on `done`/`error`/unsubscribe
- [ ] T028 [P] [US1] Create `app-ui/src/app/components/chat-box/chat-box.ts` — standalone `ChatBoxComponent`; `messages = signal<ChatMessage[]>([])`; `isLoading = signal(false)`; injects `ChatService`, `HomeSearchStateService`; `onSubmit()`: disables submit, calls `ChatService.stream()`, on `listings` → calls `HomeSearchStateService.updateHomes()`, on `token` → appends to last assistant message, on `done`/`error` → re-enables submit
- [ ] T029 [P] [US1] Create `app-ui/src/app/components/chat-box/chat-box.html` — message list (`@for` over `messages`), input field, submit button `[disabled]="isLoading()"`, loading spinner while `isLoading()`
- [ ] T030 [P] [US1] Create `app-ui/src/app/components/chat-box/chat-box.scss` — sidebar panel, scrollable message list, user/assistant message bubble styles
- [ ] T031 [US1] Update `app-ui/src/app/components/home-listings/home-listings.ts` — inject `HomeSearchStateService`; on `ngOnInit` call `HomeService.getHomes()` and push result to `HomeSearchStateService.updateHomes()`; bind `homes` to `HomeSearchStateService.homes` signal instead of local array
- [ ] T032 [US1] Update `app-ui/src/app/components/home-listings/home-listings.html` — wrap content in two-column CSS layout: listings grid (~70%) left column + `<app-chat-box>` (~30%) right column; add `ChatBoxComponent` to `imports[]` in component decorator

**Checkpoint**: US1 fully functional. Chat sidebar visible, query returns filtered listings, tokens stream into chat, loading state works, Cassandra stores turn memory.

---

## Phase 4: User Story 2 — No Results Found (Priority: P2)

**Goal**: A query with zero matches shows an empty listings panel and the chat displays "No listings found. Try adjusting your criteria."

**Independent Test**: Submit `"40-bed mansion on the moon"` → listings panel shows empty state — `"No homes found"` message → chat displays the no-results message. US1 query still works.

### Tests for US2 — Write FIRST, verify they FAIL before implementing

- [ ] T033 [P] [US2] Extend `ChatApplicationServiceTest.java` — add test case: `HomeSearchPort` returns empty list; assert `listings` SSE event emitted with `data: []`; assert AI prompt includes context that no results were found
- [ ] T034 [P] [US2] Create `app-ui/src/app/components/chat-box/chat-box.spec.ts` — verify that when `listings` SSE event contains `[]`, `HomeSearchStateService.updateHomes([])` is called; verify `@empty` block renders in listings panel

### Implementation for US2

- [ ] T035 [US2] Update `ChatApplicationService.java` — when `HomeResult` list is empty: emit `listings` event with `[]`; build AI prompt variant that informs the model no listings matched (so AI generates "No listings found. Try adjusting your criteria." style response)
- [ ] T036 [US2] Verify `app-ui/src/app/components/home-listings/home-listings.html` — confirm existing `@empty` block ("No homes found") renders when `HomeSearchStateService.homes()` is `[]`. Adjust empty-state copy if needed to match AC: `"No listings found. Try adjusting your criteria."`

**Checkpoint**: US2 functional. Empty queries show empty state in both listings panel and chat. US1 still works.

---

## Phase 5: User Story 3 — Reset View (Priority: P3)

**Goal**: Clicking "Reset" restores the default unfiltered listings and clears the chat area.

**Independent Test**: Submit a query → listings filter → click "Reset" → listings restore to full set, chat messages cleared. US1 and US2 still work.

### Tests for US3 — Write FIRST, verify they FAIL before implementing

- [ ] T037 [P] [US3] Extend `app-ui/src/app/components/chat-box/chat-box.spec.ts` — verify "Reset" button calls `HomeSearchStateService.reset()` and clears `messages` signal to `[]`
- [ ] T038 [P] [US3] Extend `app-ui/src/app/core/services/home-search-state.spec.ts` — verify `reset()` calls `HomeService.getHomes()` and updates `homes` signal with full listing set

### Implementation for US3

- [ ] T039 [US3] Update `app-ui/src/app/core/services/home-search-state.ts` — implement `reset()`: calls `HomeService.getHomes()`, subscribes, calls `updateHomes(result)` and `setLoading(false)`
- [ ] T040 [US3] Update `app-ui/src/app/components/chat-box/chat-box.ts` — add `onReset()` method: calls `HomeSearchStateService.reset()`, clears `messages` signal to `[]`, clears input field value
- [ ] T041 [US3] Update `app-ui/src/app/components/chat-box/chat-box.html` — add "Reset" button (visible when `messages().length > 0`); binds to `onReset()`

**Checkpoint**: All three user stories functional. Full happy-path E2E: search → no-results → reset cycle works end-to-end.

---

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T042 Remove the existing inline AI search bar from `app-ui/src/app/components/home-listings/home-listings.html` — delete the `<div class="search-hero-section">` block (superseded by the chat sidebar). Update `home-listings.ts` to remove `@ViewChild('searchInput')` and `onAiSearch()` / `executeSearch()` if no longer needed.
- [ ] T043 [P] Add `aria-label` attributes to `chat-box.html` (input field, submit button, reset button, message list region) for accessibility
- [ ] T044 [P] Add mobile responsive CSS breakpoints to `app-ui/src/app/components/home-listings/home-listings.scss` and `chat-box.scss` — below 768 px: switch to single-column stacked layout (chat below listings)
- [ ] T045 [P] Add `@CorrelationId` / trace context propagation to `HomeSearchAdapter.java` — ensure outgoing `RestClient` call carries W3C `traceparent` header for distributed tracing (Istio/Jaeger)
- [ ] T046 [P] Add `management.endpoints.web.exposure.include: health,prometheus` to `config-repo/chat.yml`
- [ ] T047 Run full build: `./gradlew build` — all modules including `:api` and `:microservices:chat-service` must pass
- [ ] T048 Docker compose smoke test: `docker compose up -d chat-cassandra chat-service` — verify `chat-service` starts healthy and `GET /actuator/health` returns 200
- [ ] T049 Manual SSE smoke test: `curl -N -X POST http://localhost:7007/api/v1/chat/stream -H "Content-Type: application/json" -d '{"sessionId":"smoke-test-1","query":"3 beds under 500k"}'` — confirm event sequence: `listings` → multiple `token` → `done`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 — **blocks all user stories**
- **Phase 3 (US1)**: Depends on Phase 2 — MVP deliverable
- **Phase 4 (US2)**: Depends on Phase 2; integrates with Phase 3 output
- **Phase 5 (US3)**: Depends on Phase 2; integrates with Phase 3 output
- **Phase 6 (Polish)**: Depends on all stories complete

### User Story Dependencies

| Story | Depends on | Notes |
|-------|------------|-------|
| US1 (P1) | Phase 2 complete | Core flow — all other stories build on it |
| US2 (P2) | Phase 2 complete; US1 `ChatApplicationService` exists | Extends empty-list handling in service |
| US3 (P3) | Phase 2 complete; US1 `ChatBoxComponent` and `HomeSearchStateService` exist | Adds reset method and button |

### Within Each Phase

1. Write tests (T018–T022, T033–T034, T037–T038) and verify they **FAIL**
2. Implement domain/port types before services
3. Implement services before controllers/adapters
4. Implement backend before Angular wiring
5. Mark task complete only after its test passes

### Parallel Opportunities per Phase

**Phase 1**: T004, T005, T006, T007 can all run in parallel after T001–T003.

**Phase 2**: T011, T012, T014, T016 can run in parallel. T013 follows T012. T015 follows T014. T017 follows T016.

**Phase 3**: All test tasks (T018–T022) are parallel. After tests fail: T024, T027, T028, T029, T030 are parallel. T023 follows T024. T026 follows T023. T031–T032 follow T028.

---

## Parallel Example: Phase 3 (US1)

```
# Step 1 — write all tests in parallel, verify each FAILS:
Task T018: ChatApplicationServiceTest.java
Task T019: HomeSearchAdapterIT.java
Task T020: ChatControllerIT.java
Task T021: ChatApplicationServiceIT.java
Task T022: chat.spec.ts

# Step 2 — implement infrastructure adapters in parallel:
Task T024: HomeSearchAdapter.java
Task T027: chat.ts (Angular service)
Task T028: chat-box.ts
Task T029: chat-box.html
Task T030: chat-box.scss

# Step 3 — wire use case (depends on T024):
Task T023: ChatApplicationService.java
Task T026: ChatController.java

# Step 4 — integrate into home page (depends on T028):
Task T031: home-listings.ts
Task T032: home-listings.html
```

---

## Implementation Strategy

### MVP (US1 only — Phases 1–3)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: US1 — Filter Listings
4. **STOP and validate**: submit a query via the sidebar, confirm listings update and tokens stream
5. Demo / review before proceeding to US2

### Incremental Delivery

| Milestone | Phases | Deliverable |
|-----------|--------|-------------|
| Foundation | 1–2 | chat-service boots, ports defined, Angular services ready |
| MVP | 1–3 | Chat sidebar filters listings via SSE — P1 story complete |
| Full feature | 1–5 | All 3 user stories working |
| Production-ready | 1–6 | Mobile layout, observability, smoke-tested |
