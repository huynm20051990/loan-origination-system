# Research: AI Assistant Chat Box (002-ai-chat-box)

**Sources**: `chat-feature/research_chat_feature.md`, `chat-feature/plan.md`, `chat-feature/spec.md`, project CLAUDE.md, `.specify/memory/constitution.md`

---

## Decision 1: Service Architecture

**Decision**: Implement as a new standalone Spring Boot microservice (`chat-service`, port 7008), following the exact same hexagonal pattern as `home-service` and `assessment-service`.

**Rationale**: The existing system is a microservices architecture where each bounded context owns its own database and compute. A dedicated `chat-service` gives independent deployability, isolated failure modes, and a clean separation of the AI/chat domain from the home-listing domain. Adding chat logic directly to `home-service` would violate service isolation and the hexagonal boundary.

**Alternatives considered**:
- Extend `home-service` directly — rejected: violates single-responsibility and makes `home-service` harder to scale independently.
- Serverless function — rejected: doesn't fit the existing Docker Compose / Helm deployment model.

---

## Decision 2: AI Stack

**Decision**: Spring AI with Google Gemini (`gemini-2.5-flash` for chat, `gemini-embedding-001` for embeddings at 768 dimensions). Same stack as `home-service` and `assessment-service`.

**Rationale**: Already adopted in the project; reusing it avoids introducing new AI SDK dependencies. Gemini embeddings align with the existing pgvector HNSW index dimensions (768) used by home-service.

**Alternatives considered**:
- OpenAI GPT-4o — rejected: would require a new API key and SDK dependency not in the project.
- Local LLM — rejected: latency and infrastructure requirements incompatible with current setup.

---

## Decision 3: Chat Memory Backend

**Decision**: Apache Cassandra (`chat_memory` keyspace), using `spring-ai-cassandra-store-spring-boot-starter`. Follows the exact pattern established by `assessment-service`.

**Rationale**: Cassandra is already running in the Docker Compose stack. Spring AI's `CassandraChatMemory` provides a ready-made session-scoped memory store. PostgreSQL could work but Cassandra is already the established pattern for AI chat memory in this codebase.

**Alternatives considered**:
- In-memory (Java Map) — rejected: doesn't survive service restarts; breaks multi-turn context on pod restart.
- PostgreSQL chat_messages table only — rejected: duplicates data and adds query complexity; Cassandra is optimized for time-series append workloads.

---

## Decision 4: Streaming Protocol

**Decision**: SSE (Server-Sent Events) over HTTP, using `produces = MediaType.TEXT_EVENT_STREAM_VALUE` on the controller. Tokens stream as `ChatStreamChunkDTO` JSON events. Final event carries `done: true` and the persisted `messageId`.

**Rationale**: Constitution mandates streaming REQUIRED for all AI calls. SSE is simpler than WebSocket for unidirectional server-to-client token streaming and is well-supported by Angular's `HttpClient`. Existing `assessment-service` uses SSE patterns. REST + SSE aligns with G6 gate.

**Alternatives considered**:
- WebSocket — rejected: bidirectional protocol adds complexity for a unidirectional stream; no existing WebSocket infrastructure in the project.
- Long-polling — rejected: higher latency and server resource waste.
- Full response at once (non-streaming) — **rejected by constitution**: buffering before render is explicitly PROHIBITED. Overrides Q3 clarification from `/speckit.clarify` session.

---

## Decision 5: RAG Strategy

**Decision**: `chat-service` maintains its own `chat_home_embeddings` pgvector table (768-dim HNSW cosine index), populated by a scheduled sync job (every 15 minutes) that calls `home-service` REST API (`GET /api/v1/homes`). The `HomeSearchChatTools` Spring AI tool performs similarity search with `FilterExpressionTextParser` for structured filters.

**Rationale**: `chat-service` must not share databases with `home-service` (microservice isolation). A sync job is the simplest pattern to keep embeddings fresh without event-driven CDC complexity for this v1 scope. 15-minute freshness is acceptable for home listing data.

**Alternatives considered**:
- Kafka event-driven embedding sync — deferred to future iteration; adds Kafka consumer complexity not justified for v1.
- Direct cross-service DB query — rejected: violates hexagonal isolation; databases are not shared across services.

---

## Decision 6: Frontend Layout

**Decision**: Fixed side panel (right side) displayed alongside the home listings panel. Both panels co-visible at ≥1280px without scrolling. Chat panel contains: message history (scrollable), text input, Send button, Clear/Reset button, and a rate-limit warning area.

**Rationale**: Confirmed in `/speckit.clarify` Q1 (Option B). Keeps listings visible while chatting, directly satisfying SC-004. Overrides the pre-existing `chat-feature/spec.md` suggestion of a bottom-right floating panel (which would obscure listings content).

**Alternatives considered**:
- Floating overlay modal — rejected: obscures listings, contradicts SC-004.
- Bottom bar — rejected: limited vertical space for message history.
- Top full-width bar — rejected: pushes listings out of viewport.

---

## Decision 7: Session Lifecycle

**Decision**: A new `ChatSession` is created (POST `/api/v1/chat/sessions`) when the user first opens the chat panel. The `sessionId` is held in Angular component state. Closing and reopening within the same page load resumes the same session. Page reload creates a new session (no server-side history retrieval on fresh load in v1).

**Rationale**: Matches spec assumption (session-scoped, no cross-session persistence in v1). Simple to implement and test.

**Alternatives considered**:
- Persist and restore session on reload — deferred: not in v1 scope per spec Assumptions.

---

## Decision 8: Rate Limiting

**Decision**: Soft client-side rate limit in the Angular `ChatService`. After N rapid submissions (suggested threshold: 5 messages in 10 seconds), display a warning in the chat UI. If the threshold is persistently exceeded, disable the Send button for a configurable cooldown (suggested: 15 seconds) with a visible countdown.

**Rationale**: Confirmed in `/speckit.clarify` Q2 (Option B). Protects the AI backend from accidental hammering. Client-side enforcement is sufficient for v1; server-side rate limiting (e.g., via gateway) can be added in a future iteration.

**Alternatives considered**:
- API Gateway rate limiting — deferred: requires gateway configuration changes and cross-cutting concerns; out of scope for v1.
- No limit — rejected: leaves AI backend exposed to runaway queries.

---

## Decision 9: API Gateway Routing

**Decision**: Add route in `config-repo/gateway.yml` for `chat-service`:
```yaml
- id: chat-service
  uri: lb://chat
  predicates:
    - Path=/api/v1/chat/**
  filters:
    - TokenRelay=
```
Spring application name: `chat` (matches `lb://chat`). `TokenRelay=` forwards the Bearer token from the gateway to `chat-service`.

**Rationale**: Consistent with all other microservice routes in `gateway.yml`. `TokenRelay` ensures JWT is forwarded for authorization checks.

---

## Resolved NEEDS CLARIFICATION Items

| Item | Resolution |
|---|---|
| Chat box layout position | Fixed side panel alongside listings (Q1 clarification) |
| Rate limiting behavior | Soft limit — warning then cooldown (Q2 clarification) |
| Response delivery style | SSE streaming (constitution override of Q3) |
| Backend failure UX | Inline error with "Try again" prompt; input stays open (Q4 clarification) |
| Concurrent user scale target | 100 concurrent users (Q5 clarification) |
