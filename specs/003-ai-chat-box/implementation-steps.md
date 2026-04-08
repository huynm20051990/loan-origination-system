# Implementation Steps: AI Chat Box

**Branch**: `003-ai-chat-box` | **Date**: 2026-04-08
**Total**: 36 steps across 10 phases

---

## Phase 1 — Project Scaffolding & Infrastructure

1. Add `include("microservices:chat-service")` to `settings.gradle.kts`
2. Create `microservices/chat-service/build.gradle.kts` (Spring Boot 3.5.x + Spring AI + webflux + Cassandra memory — **no JPA**)
3. Create `ChatServiceApplication.java` (main class)
4. Add `ChatAPI.java` and `ChatRequestDTO.java` to `:api` module (`api/src/main/java/.../api/core/chat/`)
5. Create `config-repo/chat.yml` (port 7007, Gemini 2.5 Flash, Cassandra chat memory config)
6. Create `database/init-chat-cassandra/init.cql` (CREATE KEYSPACE chat_keyspace)
7. Add `chat-cassandra` and `chat-service` entries to `docker-compose.yml`
8. Add `/api/v1/chat/**` → `chat-service` route to `config-repo/gateway.yml`

---

## Phase 2 — Backend: Domain Layer

9. `domain/model/ChatTurn.java` — pure POJO: sessionId, query, reply, timestamp. Zero framework annotations.

---

## Phase 3 — Backend: Application Layer (Ports + Use Case)

10. `application/port/output/HomeResult.java` — plain Java record (port contract type, never persisted)
11. `application/port/output/HomeSearchPort.java` — interface: `List<HomeResult> search(String query)`
12. `application/port/input/ChatUseCase.java` — interface: `Flux<ServerSentEvent<String>> stream(String sessionId, String query)`
13. `application/service/ChatApplicationService.java` — orchestrates: call `HomeSearchPort` → emit `listings` event → call Spring AI `ChatClient` → stream `token` events → emit `done` or `error`

---

## Phase 4 — Backend: Infrastructure Layer

14. `infrastructure/output/client/HomeSearchAdapter.java` — `RestClient` calls `home-service GET /api/v1/homes/search?query=…`, deserializes JSON directly into `HomeResult`, implements `HomeSearchPort`
15. `infrastructure/input/rest/ChatController.java` — implements `ChatAPI`, delegates to `ChatUseCase`, returns `Flux<ServerSentEvent<String>>` with `produces = TEXT_EVENT_STREAM_VALUE`
16. `infrastructure/input/rest/dto/ChatRequestDTO.java` — `@NotBlank sessionId`, `@NotBlank query` (or reuse from `:api` module)
17. `infrastructure/config/BeanConfiguration.java` — wires `CassandraChatMemoryRepository` → `MessageWindowChatMemory(maxMessages=20)`, `ChatClient` with `MessageChatMemoryAdvisor`, `RestClient` for home-service

---

## Phase 5 — Backend: Tests (Red-Green-Refactor)

18. **Unit**: `ChatApplicationServiceTest` — Mockito mocks for `HomeSearchPort` + `ChatClient`; verifies `listings` event emitted before tokens, `error` event on port failure
19. **Unit**: `ChatTurnTest` — domain model validation
20. **Integration**: `HomeSearchAdapterIT` — WireMock stubs `home-service`; verifies `HomeResult` deserialization, error propagation on non-2xx
21. **Integration**: `ChatApplicationServiceIT` — Testcontainers Cassandra + WireMock; full flow: query → home results → AI stream → Cassandra memory written
22. **Contract**: `ChatControllerIT` — `@SpringBootTest` with `WebTestClient`; asserts SSE event types and ordering (`listings` → `token`... → `done`)

---

## Phase 6 — Frontend: Angular Models & Services

23. `app-ui/src/app/core/models/chat.ts` — `ChatMessage` interface (`role`, `content`, `isStreaming`)
24. `app-ui/src/app/core/services/home-search-state.ts` — `HomeSearchStateService` with `Signal<Home[]>` and `Signal<boolean>` for loading; shared between `HomeListingsComponent` and `ChatBoxComponent`
25. `app-ui/src/app/core/services/chat.ts` — `ChatService` wrapping `EventSource`, returns `Observable<{type, data}>`, handles `listings` / `token` / `done` / `error` events; closes connection on `done`/`error`

---

## Phase 7 — Frontend: Chat UI Component

26. `app-ui/src/app/components/chat-box/chat-box.ts` — standalone component; `signal<ChatMessage[]>` for message list; `signal<boolean>` for submit lock; injects `ChatService` + `HomeSearchStateService`; on `listings` event calls `HomeSearchStateService.updateHomes()`; on `token` appends to last assistant message
27. `app-ui/src/app/components/chat-box/chat-box.html` — message list, text input, submit button (`[disabled]="isLoading()"`)
28. `app-ui/src/app/components/chat-box/chat-box.scss` — sidebar panel styling, scrollable message list

---

## Phase 8 — Frontend: Integration into Home Page

29. Update `HomeListingsComponent` — switch from direct `HomeService.getHomes()` to reading `HomeSearchStateService.homes` signal; initialize signal on `ngOnInit` from `HomeService.getHomes()`; add two-column CSS layout (listings ~70% + chat sidebar ~30%)
30. Mount `ChatBoxComponent` inside `HomeListingsComponent` template; add to `imports[]`
31. Remove the existing inline AI search bar from `home-listings.html` (the `mat-form-field` search hero section) — superseded by the chat sidebar

---

## Phase 9 — Frontend: Tests

32. `ChatService` unit test (Jest) — mock `EventSource`, verify observable emissions per event type
33. `ChatBoxComponent` unit test (Jest + Angular Testing Library) — submit lock behavior, message list accumulation, reset on new query

---

## Phase 10 — End-to-End Verification

34. `./gradlew build` — full build including `:api`, `:chat-service`
35. `docker compose up` smoke test — verify `chat-cassandra` healthy, `chat-service` starts, gateway routes `/api/v1/chat/**` correctly
36. Manual SSE smoke test: `curl -N POST /api/v1/chat/stream` — confirm `listings` → `token`... → `done` event sequence

---

## Ordering Constraints

| Constraint | Reason |
|------------|--------|
| Steps 1–8 before all backend work | Gradle module must exist before source files compile |
| Step 4 (`:api` module) before Step 15 (`ChatController`) | Controller implements `ChatAPI` from `:api` |
| Steps 10–12 (ports) before Steps 13–14 (service + adapter) | Ports first, implementations second (hexagonal rule) |
| Steps 18–22 (tests) written before Steps 13–17 (implementation) | Red-Green-Refactor mandate (Constitution §III) |
| Step 24 (`HomeSearchStateService`) before Steps 26 and 29 | Both components depend on the shared signal service |
| Step 26 (`ChatBoxComponent`) before Step 31 (remove old search bar) | Must have replacement wired before removing existing UI |
