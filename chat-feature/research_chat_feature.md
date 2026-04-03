# Research: Chat Feature — Spec Driven Design + Ralph Loop

## What is the Ralph Loop in this context?

The **Ralph Loop** is an iterative SDD cycle:
**Spec → Review → Implement → Validate → Refine (repeat)**

Each loop produces a tighter, verified artifact before moving to the next layer. You never write code without a validated spec above it.

---

## Step-by-Step Plan

### Step 1 — Feature Scoping (Problem Spec)
Write a concise feature brief that answers:
- What can the user ask? (home info only? loan eligibility? general Q&A?)
- What data sources does the chat backend query?
- Is it stateless (one-shot Q&A) or stateful (multi-turn conversation with memory)?
- Who can use it — anonymous or authenticated users?
- Real-time streaming response (SSE/WebSocket) or request/response?

This becomes the **Feature Spec** — the north star for every downstream decision.

---

### Step 2 — Architecture Spec (Ralph Loop #1)
Design how `chat-service` fits into the existing system:
- Where does it live in the service map (port, Docker service name)
- How the API Gateway routes to it (`/api/v1/chat/**`)
- What existing services/data it depends on (e.g., Home Service for home data, pgvector for RAG)
- Whether it needs its own database (chat history → Cassandra like `assessment-service`, or PostgreSQL)
- AI integration strategy (Spring AI + Gemini, same pattern as `assessment-service`)
- Communication protocol decision: REST + SSE for streaming, or WebSocket

**Review gate:** Does this architecture fit the hexagonal pattern? Can it be deployed independently?

---

### Step 3 — API Contract Spec (Ralph Loop #2)
Write the **OpenAPI 3.x spec** for `chat-service` before any code:
- `POST /api/v1/chat/sessions` — create a session (if stateful)
- `POST /api/v1/chat/sessions/{id}/messages` — send a message, stream response via SSE
- `GET /api/v1/chat/sessions/{id}/messages` — retrieve history
- Define request/response schemas, error codes, authentication requirements

**Review gate:** Validate the spec against the Feature Spec from Step 1. Run a loop back if anything is missing.

---

### Step 4 — Data Model Spec (Ralph Loop #3)
Define all data models before touching schema:
- `ChatSession` entity (id, userId, createdAt, topic)
- `ChatMessage` entity (id, sessionId, role: USER/ASSISTANT, content, timestamp)
- Vector store schema (if using RAG over home data)
- Cassandra keyspace/table definitions for chat memory (following `assessment-service` pattern)

**Review gate:** Are models consistent with existing domain conventions in `api/` shared module? Add shared DTOs there.

---

### Step 5 — Consumer-Driven Contract Spec
Define contracts between:
- `app-ui` (consumer) ↔ `chat-service` (producer): Add a Spring Cloud Contract for the chat API
- `chat-service` ↔ `home-service` (if chat-service queries home data): define that internal contract

This goes into the `contracts/` module following existing patterns.

---

### Step 6 — AI/Agent Behavior Spec
Write a prompt spec and agent behavior document:
- System prompt definition (what the AI knows, what it refuses)
- RAG strategy: what documents/embeddings are ingested (home listings? FAQ? loan policies?)
- Tool/MCP usage: does the chat agent call Assessment MCP Server for loan-related queries?
- Guardrails: what topics are out of scope

This is the AI equivalent of an API spec — it defines observable behavior.

---

### Step 7 — Implementation (against the specs)
Only now write code, in layers:
1. `chat-service` backend (hexagonal: domain → application → infrastructure)
2. Database migrations + Cassandra schema
3. Spring AI integration (Gemini + pgvector RAG + chat memory)
4. API Gateway route addition in `config-repo/gateway.yml`
5. Docker Compose service entry
6. Angular chat box component in `app-ui`

---

### Step 8 — Validation Loop (Ralph Loop #4)
Validate each layer against its spec:
- Run Spring Cloud Contract tests (consumer contracts pass?)
- API responses match OpenAPI spec (use contract tests or Postman/Newman)
- AI responses match the behavior spec (golden-set prompt tests)
- Frontend component matches UX spec

**Refine:** Any deviation triggers a loop back to the relevant spec — fix the spec first, then the code.

---

### Step 9 — Integration & Observability
- Add Zipkin tracing spans for chat requests
- Add Prometheus metrics (message count, AI response latency)
- Add Grafana dashboard panel for chat service
- Validate end-to-end flow through API Gateway → chat-service → AI → response

---

## Summary

```
Step 1  Feature Scoping Spec
Step 2  Architecture Spec          ← Ralph Loop #1
Step 3  OpenAPI Contract Spec      ← Ralph Loop #2
Step 4  Data Model Spec            ← Ralph Loop #3
Step 5  Consumer-Driven Contracts
Step 6  AI Agent Behavior Spec
Step 7  Implementation (code)
Step 8  Validation & Refinement    ← Ralph Loop #4
Step 9  Integration & Observability
```

The key discipline: **each loop produces a written artifact that is reviewed and agreed upon before the next step begins**. No code is written until Steps 1–6 are stable.
