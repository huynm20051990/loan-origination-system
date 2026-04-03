# AI Behavior Requirements Checklist: AI Assistant Chat Box

**Purpose**: Lightweight pre-PR gate — surface showstopper gaps in AI behavior requirements before merge
**Created**: 2026-04-02
**Depth**: Lightweight (showstoppers only)
**Focus**: AI behavior — scope restriction, streaming, multi-turn conversation, session persistence
**Feature**: [spec.md](../spec.md) · [plan.md](../plan.md)

---

## Scope Restriction Requirements

- [ ] CHK001 — Is the boundary between "in-scope" and "out-of-scope" queries defined with examples in the spec, or is it left entirely to the AI system prompt? [Clarity, Spec §FR-012, Gap]
- [ ] CHK002 — Is the required content or format of an "out-of-scope" graceful response specified (e.g., a fixed template, a tone guideline, minimum information to include), or is "graceful" undefined and unmeasurable? [Ambiguity, Spec §FR-012]
- [ ] CHK003 — Does the spec address boundary queries that are partially in-scope (e.g., "What's the best time of year to buy a home in Austin?") — are these classified and handled as a scenario? [Coverage, Spec §FR-012, Gap]

---

## Multi-Turn Conversation Requirements

- [ ] CHK004 — Are requirements defined for how the AI must use conversation history when processing a follow-up query (e.g., "Only show ones with a garage" after an earlier filtered result)? Without this, the multi-turn scenario (US-2) has no verifiable acceptance criterion. [Completeness, Spec §US-2, Gap]
- [ ] CHK005 — Does the spec define what the AI should do when a follow-up query is ambiguous without context — silently apply context, ask for clarification, or treat it as a standalone query? [Clarity, Spec §US-2, Gap]
- [ ] CHK006 — Are the acceptance scenarios in US-2 sufficient to distinguish between a system that genuinely uses conversation context versus one that simply displays message history? [Measurability, Spec §US-2]

---

## Session Persistence Contradiction

- [ ] CHK007 — The spec §Assumptions states "chat history persists only for the duration of the current page session," yet plan.md provisions Cassandra-backed `chat_sessions` and `chat_messages` tables with JPA persistence adapters. Is this contradiction explicitly resolved in the spec — either by updating the assumption or documenting the discrepancy as intentional? [Conflict, Spec §Assumptions vs plan.md §Infrastructure]
- [ ] CHK008 — If server-side persistence is now in scope, does the spec define data retention policy (how long are sessions kept?), or is that intentionally deferred? [Completeness, Gap]

---

## Streaming Failure & Degradation Requirements

- [ ] CHK009 — Does the spec define what happens when the SSE stream drops mid-response (e.g., network interruption after 30% of tokens delivered)? Is partial content preserved, discarded, or retried? [Edge Case, Spec §FR-007, Gap]
- [ ] CHK010 — Is a timeout threshold defined for when a streaming response is considered failed (i.e., no tokens after N seconds)? SC-007 defines a 3 s first-token target but does not define a failure timeout. [Completeness, Spec §SC-007, Gap]
- [ ] CHK011 — The clarification session answer for response delivery (Q3 in §Clarifications) still shows the superseded "full response at once" answer, with only an inline note. Is the spec's clarification section updated to reflect the authoritative SSE requirement, or does this create a risk of the superseded decision being applied? [Consistency, Spec §Clarifications vs §FR-007]

---

## RAG Search Quality Requirements

- [ ] CHK012 — SC-002 requires 90% of "clearly-worded" queries to return relevant results, but neither "clearly-worded" nor "relevant results" is defined with measurable criteria. Are these terms quantified anywhere in the spec? [Ambiguity, Spec §SC-002]
- [ ] CHK013 — Does the spec define how the AI should degrade when the RAG store returns no results versus low-confidence results? FR-008 covers the empty-state UX but does not address whether the AI should distinguish "no results" from "results exist but confidence is low." [Completeness, Spec §FR-008, Gap]

---

## AI Response Content Consistency

- [ ] CHK014 — FR-006 requires the chat box to display a response "telling the user to review the updated listing results" after every query, but FR-008 requires a different message when no listings match. Are these two requirements consistent, and is there a clear decision tree for which message the AI produces? [Consistency, Spec §FR-006 vs §FR-008]
- [ ] CHK015 — Does the spec address what message appears in the chat box when the AI provider (Google Gemini) itself returns an error (rate limit, service unavailable) — distinct from FR-010 which covers general system errors? [Coverage, Spec §FR-010, Gap]
