# Implementation Plan: AI Assistant Chat Box

**Branch**: `002-ai-chat-box` | **Date**: 2026-04-02 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/specs/002-ai-chat-box/spec.md`

---

## Summary

Add a fixed side-panel chat box to the EasyApply home page that allows authenticated users to ask natural language questions about home listings. Queries are processed by a new `chat-service` microservice using Spring AI + Google Gemini with RAG over a pgvector home-listing embeddings store. The AI response streams via SSE; the Angular frontend appends tokens incrementally and simultaneously updates the listings panel to reflect the filtered results. Chat history is scoped to the current page session; Cassandra provides the Spring AI chat memory store per existing `assessment-service` patterns.

---

## Technical Context

**Language/Version**: Java 21 LTS (backend), TypeScript strict mode (frontend, Angular 21)  
**Primary Dependencies**: Spring Boot 3.x, Spring AI (Google Gemini), Spring Security OAuth2, RxJS, Vitest  
**Storage**: PostgreSQL + pgvector (HNSW cosine, 768-dim) for session/messages/embeddings; Apache Cassandra for Spring AI chat memory  
**Testing**: JUnit 5 + Mockito (unit), Testcontainers (integration), Vitest + Angular TestBed (frontend)  
**Target Platform**: Linux container (JVM), Angular SPA served via Nginx through API Gateway  
**Project Type**: Web application — new microservice (`chat-service`) + Angular feature module  
**Performance Goals**: SSE first token < 3 s; full listing update visible within 5 s (SC-001); 100 concurrent chat users (SC-006)  
**Constraints**: Virtual threads enabled (`spring.threads.virtual.enabled: true`); HikariCP max-pool-size 10; desktop only (≥1280 px, v1)  
**Scale/Scope**: 100 concurrent users; session-scoped chat (no cross-session state); home-listings queries only (v1)

**New runtime dependencies introduced**:
- `spring-boot-starter-data-cassandra` (chat memory — already used by assessment-service)
- `spring-ai-cassandra-store-spring-boot-starter`
- `spring-ai-starter-vector-store-pgvector` (already used by home-service)
- `spring-ai-starter-model-google-genai` (already used by home-service)
- No new infrastructure services required beyond existing Cassandra and PostgreSQL

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-checked after Phase 1 design.*

| Principle | Gate | Status |
|---|---|---|
| I. Hexagonal Purity | `domain/` must be POJO-only; no Spring/JPA imports in `domain/` or `application/`; adapters must implement port interfaces | **PASS** — package structure follows chat-feature plan.md hexagonal layout |
| I. Naming conventions | `ChatApplicationService`, `ChatWebAdapterController`, `ChatSessionRepositoryPort`, `ChatAIPort`, `ChatPersistenceAdapter`, `ChatWebMapper` | **PASS** — all names comply |
| I. No `assert` in production | Explicit exceptions used in all validation paths | **PASS** |
| II. SDD — spec gates | spec.md has independently testable user story, API contract defined, data model declared, NFRs stated | **PASS** |
| II. Branch naming | `002-ai-chat-box` matches `<number>-<kebab-name>` | **PASS** |
| III. Test-First | Unit tests (domain + application), integration tests (persistence adapters via Testcontainers), contract tests (Spring Cloud Contract), Angular component `.spec.ts` files | **PASS** — required before implementation |
| III. Mocked DB prohibited | Integration tests must use real PostgreSQL (Testcontainers + pgvector extension) | **PASS** |
| Streaming REQUIRED | SSE streaming mandatory; buffering before render prohibited | **PASS** — see Complexity Tracking for clarification override |
| Security — Bearer token | All chat endpoints require `Authorization: Bearer`; JWT principal extracted in controller | **PASS** |
| Security — auth interceptor | `auth-interceptor.ts` must cover `/api/v1/chat/**`; no skip-list addition needed (already covered by wildcard) | **PASS** |

**Post-Phase-1 re-check**: No new violations introduced. Data model is consistent with existing domain conventions. Shared DTOs placed in `api/` module.

---

## Project Structure

### Documentation (this feature)

```text
specs/002-ai-chat-box/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── openapi.yaml
└── tasks.md             # Phase 2 output (/speckit.tasks — NOT created here)
```

### Source Code (repository root)

```text
# New microservice
microservices/chat-service/
├── build.gradle.kts
├── Dockerfile
└── src/
    └── main/java/com/loan/origination/system/microservices/chat/
        ├── ChatApplication.java
        ├── domain/
        │   ├── model/
        │   │   ├── ChatSession.java
        │   │   └── ChatMessage.java
        │   └── vo/
        │       └── MessageRole.java          (Enum: USER, ASSISTANT)
        ├── application/
        │   ├── port/
        │   │   ├── input/
        │   │   │   └── ChatUseCase.java
        │   │   └── output/
        │   │       ├── ChatSessionRepositoryPort.java
        │   │       ├── ChatMessageRepositoryPort.java
        │   │       └── ChatAIPort.java
        │   └── service/
        │       └── ChatApplicationService.java
        └── infrastructure/
            ├── config/
            │   └── BeanConfiguration.java
            ├── input/rest/
            │   ├── ChatWebAdapterController.java
            │   └── mapper/ChatWebMapper.java
            └── output/
                ├── persistence/
                │   ├── ChatSessionPersistenceAdapter.java
                │   ├── ChatMessagePersistenceAdapter.java
                │   ├── entity/
                │   │   ├── ChatSessionEntity.java
                │   │   └── ChatMessageEntity.java
                │   ├── mapper/ChatPersistenceMapper.java
                │   └── repository/
                │       ├── ChatSessionRepository.java
                │       └── ChatMessageRepository.java
                └── ai/
                    ├── ChatAIAdapter.java
                    └── tools/HomeSearchChatTools.java

# Shared API module additions
api/src/main/java/com/loan/origination/system/api/core/chat/v1/
├── ChatAPI.java
└── dto/
    ├── ChatSessionResponseDTO.java
    ├── ChatSessionSummaryDTO.java
    ├── ChatMessageRequestDTO.java
    ├── ChatMessageResponseDTO.java
    └── ChatStreamChunkDTO.java

# Database init scripts
database/init-chat/
└── 01.schema.sql

database/init-chat-cassandra/
└── init.cql

# Config
config-repo/
└── chat.yml

# AI prompt
agentic-ai/prompts/
└── chat-agent.st

# Frontend
app-ui/src/app/features/chat/
├── chat-box/
│   ├── chat-box.component.ts
│   ├── chat-box.component.html
│   ├── chat-box.component.scss
│   └── chat-box.component.spec.ts
└── models/
    ├── chat-session.model.ts
    └── chat-message.model.ts

app-ui/src/app/core/services/
└── chat.ts                      (ChatService + chat.spec.ts)
```

**Structure Decision**: Option 2 (Web application) — new Spring Boot microservice for backend and a new Angular feature module for frontend, following the exact same hexagonal pattern as `home-service` and `assessment-service`.

---

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|---|---|---|
| Q3 Clarification overridden: "full response at once" → **SSE streaming** | Constitution §Quality Standards mandates streaming REQUIRED; buffering before render is PROHIBITED. The clarification was made without awareness of this constitution rule. | Non-streaming would violate a hard constitution gate and degrade perceived performance. Short confirmation messages also benefit from the same streaming path, simplifying the controller contract. |

---

## Review Gates (Ralph Loop Checkpoints)

| Gate | Question | Pass Criteria |
|---|---|---|
| G1 | Hexagonal layer purity | No Spring/JPA imports in `domain/` or `application/` |
| G2 | API endpoints match contracts/openapi.yaml exactly | Method, path, request/response types identical |
| G3 | Session ownership enforced at query level | All DB queries filter by `userId`; 403 returned for mismatched ownership |
| G4 | AI guardrails prevent prompt injection | System prompt includes scope guardrails; out-of-scope queries redirected gracefully |
| G5 | Shared DTOs in `api/` module only | No DTO classes in `chat-service` infrastructure package |
| G6 | SSE streaming on controller | `produces = TEXT_EVENT_STREAM_VALUE`; frontend appends tokens incrementally |
| G7 | Rate limiting enforced | Soft limit warning shown; input disabled with cooldown on persistent excess (FR-013) |
| G8 | Side-panel layout | Chat panel and listings panel co-visible at ≥1280 px without scroll (SC-004) |
