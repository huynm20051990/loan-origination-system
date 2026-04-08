# Data Model: AI Chat Box

**Phase**: 1 | **Date**: 2026-04-08 | **Branch**: `003-ai-chat-box`

---

## Persistence Boundary

**chat-service stores nothing except chat memory.**

| Storage | What is stored | Managed by |
|---------|---------------|------------|
| Cassandra (`chat_keyspace`) | Spring AI `Message` objects (user/assistant turns) | Spring AI `CassandraChatMemoryRepository` — no hand-rolled code |
| *(nothing else)* | Home listing data is **never persisted** by chat-service | n/a |

Home results from `home-service` are received, immediately serialized into the `listings` SSE
event, and discarded. chat-service has **no persistence adapter** for home data and no JPA /
database dependency beyond Cassandra.

---

## Domain Entities (chat-service)

### `ChatTurn` — Application-layer value object

Pure POJO (no framework annotations). Carries one user query + AI reply across the application
layer only. Never persisted — Spring AI stores chat history independently using its own
`Message` types in Cassandra.

| Field | Type | Constraint |
|-------|------|-----------|
| `sessionId` | `String` | Non-null UUID string; frontend-generated; scoped to page load |
| `query` | `String` | Non-null, non-blank |
| `reply` | `String` | Non-null after processing; may be partial during streaming |
| `timestamp` | `Instant` | Set at creation time |

---

### `HomeResult` — Output port contract type

Defined in `application/port/output/HomeResult.java` (not in `domain/`). This is a plain Java
record that represents the shape of data returned by `HomeSearchPort.search(query)`. It is
**not a domain entity** — it exists only to give the port a typed return contract.

Lifecycle: created in `HomeSearchAdapter` by deserializing the `home-service` JSON response →
passed to `ChatApplicationService` → serialized into the `listings` SSE event → **discarded**.
Never written to any store.

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Listing identifier |
| `price` | `BigDecimal` | |
| `beds` | `Integer` | |
| `baths` | `Double` | |
| `sqft` | `Integer` | |
| `imageUrl` | `String` | |
| `address` | `Address` (record) | street, city, state, zip |
| `status` | `String` | |
| `description` | `String` | |

**No separate DTO**: `HomeSearchAdapter` deserializes `home-service` JSON directly into
`HomeResult` using `RestClient`'s built-in Jackson mapping. No intermediate `HomeResultDTO`
or MapStruct mapper needed — the record constructor serves as the mapping contract.

---

## Spring AI Chat Memory (Cassandra)

Managed entirely by Spring AI. No hand-rolled entities.

| Cassandra element | Value |
|-------------------|-------|
| Keyspace | `chat_keyspace` |
| Table | `chat_memory` |
| Messages column | `chat_messages` |
| Conversation key | `sessionId` (frontend UUID, passed as `ChatMemory.CONVERSATION_ID`) |
| Retention | Session-scoped — frontend discards `sessionId` on page navigation |
| Max messages window | 20 (matches `assessment-service`) |

---

## SSE Event Schema

The stream from `POST /api/v1/chat/stream` emits `ServerSentEvent<String>` objects with the
following named event types:

### `listings` event

Emitted **once**, immediately after `home-service` responds, before AI tokens begin streaming.

```
event: listings
data: [{"id":"...","price":450000,"beds":3,"baths":2.0,"sqft":1800,...}, ...]
```

Data is a JSON-serialized `List<HomeResult>`. Empty array `[]` is emitted when no listings match.

### `token` event

Emitted **per AI streaming chunk**.

```
event: token
data: I found 3 homes
```

```
event: token
data:  in Austin matching your criteria.
```

### `done` event

Emitted **once** when the AI stream completes normally.

```
event: done
data:
```

### `error` event

Emitted **once** when `home-service` is unavailable or the AI call fails. Stream terminates
after this event.

```
event: error
data: Something went wrong. Please try again.
```

---

## Angular State Model

### `ChatMessage` (frontend model — `app-ui/src/app/core/models/chat.ts`)

```typescript
export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;          // accumulates tokens for assistant messages
  isStreaming: boolean;     // true while tokens are still arriving
}
```

### `HomeSearchStateService` (shared signal service)

```typescript
@Injectable({ providedIn: 'root' })
export class HomeSearchStateService {
  readonly homes = signal<Home[]>([]);
  readonly isLoading = signal<boolean>(false);

  updateHomes(homes: Home[]): void { this.homes.set(homes); }
  setLoading(v: boolean): void { this.isLoading.set(v); }
}
```

`HomeListingsComponent` reads `homes` signal instead of calling `HomeService.getHomes()`
directly after this feature is added (the signal is initialized from `HomeService.getHomes()`
on component init; the chat box overrides it on new search results).

---

## Entity Relationship Summary

```
Frontend (sessionId: UUID)
    │
    │  POST /api/v1/chat/stream  { sessionId, query }
    ▼
ChatController (primary adapter)
    │
    ▼
ChatApplicationService
    ├──► HomeSearchPort ──► HomeSearchAdapter ──► home-service GET /api/v1/homes/search
    │                            returns List<HomeResult>
    │                            emits: listings SSE event
    │
    └──► Spring AI ChatClient (Gemini 2.5 Flash)
              + MessageChatMemoryAdvisor(sessionId)
              └── CassandraChatMemoryRepository (chat_keyspace.chat_memory)
                         streams: token SSE events
                         emits: done / error SSE event
```
