# Feature Specification: AI Assistant Chat Box

**Feature Branch**: `002-ai-chat-box`  
**Created**: 2026-04-02  
**Status**: Draft  
**Input**: User description: "Add an interactive chat box to the home page of the EasyApply web application. When users can ask natural language questions about home listings (only this for now, we will improve in the future), the system will update the result in the home listings page and return a message to users to review the result."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Natural Language Home Search (Priority: P1)

A home buyer visits the EasyApply home page and sees a chat box. They type a natural language question such as "Show me 3-bedroom homes under $500,000 near downtown" and submit it. The system interprets the query, filters the home listings accordingly, and updates the listings displayed on the page. A confirmation message is shown in the chat box instructing the user to review the updated results.

**Why this priority**: This is the core value of the feature — enabling users to find relevant home listings using conversational language instead of complex form filters. Without this, the feature delivers no value.

**Independent Test**: Can be fully tested by typing a natural language query in the chat box and verifying that the home listings panel updates to reflect matching results, and that a response message appears in the chat box.

**Acceptance Scenarios**:

1. **Given** a user is on the home page with listings visible, **When** they type a natural language query and submit, **Then** the home listings panel updates to show results matching the query intent, and the chat box displays a message confirming results have been updated.
2. **Given** a user submits a query, **When** the system is processing, **Then** a loading indicator is visible so the user knows the system is working.
3. **Given** a user submits a query, **When** no matching listings exist, **Then** the chat box informs the user that no results were found and the listings panel reflects an empty state.

---

### User Story 2 - Persistent Chat Interaction (Priority: P2)

After an initial query, the user can see their previous messages in the chat box. They may submit additional queries to further refine results (e.g., "Only show ones with a garage"), and the listings update accordingly each time.

**Why this priority**: Multi-turn interaction makes the chat feel natural and allows progressive refinement. It improves usability but is not required for initial value delivery.

**Independent Test**: Can be tested by submitting a first query, then a follow-up query, and verifying the chat history shows both messages and listings update on each submission.

**Acceptance Scenarios**:

1. **Given** a user has already submitted a query, **When** they submit another query, **Then** both the previous and new messages are visible in the chat history and the listings reflect the latest query.
2. **Given** a chat session with prior messages, **When** the user reloads the page, **Then** the chat history is cleared and listings return to the default state.

---

### User Story 3 - Clear / Reset Chat (Priority: P3)

The user wants to start fresh. They use a "Clear" or "Reset" button in the chat box to clear the conversation history and restore the home listings to the default (unfiltered) state.

**Why this priority**: Provides a user-controlled way to reset without a full page reload. Enhances usability but can be deferred if page reload achieves the same effect initially.

**Independent Test**: Can be tested by submitting a query, clicking "Clear", and verifying the chat is empty and listings reset to default.

**Acceptance Scenarios**:

1. **Given** a user has an active chat session with filtered listings, **When** they click the Clear/Reset button, **Then** the chat history is cleared and listings return to the default unfiltered view.

---

### Edge Cases

- What happens when the user submits an empty or whitespace-only message?
- What happens when the query is ambiguous or unrelated to home listings (e.g., "What's the weather today?")?
- How does the system handle very long queries or messages?
- What happens if the chat service is unavailable or returns an error?
- How does the system behave if there are no listings loaded on the page yet?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The home page MUST display a chat box as a fixed side panel (left or right) shown alongside the home listings panel, so both are simultaneously visible without scrolling on a standard desktop screen.
- **FR-002**: The chat box MUST accept free-text natural language input from the user.
- **FR-003**: Users MUST be able to submit a query by pressing Enter or clicking a Send button.
- **FR-004**: The system MUST interpret the natural language query and apply it as a filter to the home listings displayed on the page.
- **FR-005**: The home listings panel MUST update in place (without a full page reload) to reflect results matching the user's query.
- **FR-006**: The chat box MUST display a response message after each query, telling the user to review the updated listing results.
- **FR-007**: The chat box MUST show a loading/processing indicator between query submission and result delivery. The system response MUST be delivered via SSE (Server-Sent Events) and tokens MUST be appended to the assistant message bubble incrementally as they arrive; buffering the full response before rendering is prohibited.
- **FR-008**: If no listings match the query, the system MUST inform the user via a chat message and display an appropriate empty state in the listings panel.
- **FR-009**: The system MUST reject empty or whitespace-only submissions and prompt the user to enter a valid query.
- **FR-010**: If query processing fails due to a system error, the chat box MUST display an inline user-friendly error message and keep the input open with a visible "Try again" prompt; the input MUST NOT be disabled on failure.
- **FR-011**: The chat box MUST maintain a scrollable history of messages within the current session.
- **FR-012**: Out-of-scope queries (unrelated to home listings) MUST receive a graceful response informing the user of the chat box's current scope.
- **FR-013**: The system MUST apply a soft rate limit on query submissions; when a user submits queries too rapidly, the chat box MUST display a warning message. If the rate limit is persistently exceeded, the input MUST be briefly disabled with a visible cooldown indicator before re-enabling.

### Key Entities

- **Chat Message**: A single user query or system response, with content text, sender (user/system), and timestamp.
- **Chat Session**: The collection of messages exchanged during a single page visit; resets on page reload.
- **Home Listing Query**: The interpreted intent derived from a natural language message, used to filter the listings.
- **Home Listing**: An existing entity on the page representing a property for sale or rent.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can submit a natural language query and see updated home listings within 5 seconds under normal network conditions.
- **SC-002**: 90% of clearly-worded queries about home listing attributes (price, bedrooms, location, features) return relevant results.
- **SC-003**: Users receive a confirmation message in the chat box for every submitted query, with no silent failures.
- **SC-004**: The chat side panel and the listings panel are simultaneously visible on a standard desktop screen (≥1280px wide) without scrolling or toggling.
- **SC-005**: Error and empty-result states are communicated clearly, with at least one user-facing message guiding next steps.
- **SC-006**: The chat feature supports at least 100 concurrent users submitting queries without degradation in response time or loss of results.
- **SC-007**: The first response token MUST appear in the chat box within 3 seconds of query submission under normal network conditions.

## Clarifications

### Session 2026-04-02

- Q: What is the layout position of the chat box relative to the home listings panel? → A: Fixed side panel displayed alongside listings at all times (Option B)
- Q: Should rapid repeated query submissions be rate limited? → A: Soft limit — warn user when querying too rapidly; briefly disable input with cooldown if limit persistently exceeded (Option B)
- Q: How should the AI response text appear in the chat box? → A: Full response appears all at once after processing completes; no streaming (Option A) *(Superseded by constitution §Quality Standards: SSE streaming mandatory, buffering before render prohibited — see plan.md Complexity Tracking)*
- Q: What is the chat box behavior when the AI backend is persistently unavailable? → A: Keep input open; show inline error after each failed attempt with a "Try again" prompt (Option B)
- Q: How many concurrent users should the chat feature support without degradation? → A: 100 concurrent users

## Assumptions

- The feature targets desktop web users in v1; mobile responsiveness is not required for this iteration.
- The home listings panel already exists on the home page and supports programmatic filtering/updating of displayed results.
- NLP and listing search are handled by a new dedicated `chat-service` microservice using Google Gemini and pgvector RAG — not by home-service directly. home-service is used only as a data source for embedding ingestion.
- Authentication is required — all chat API endpoints require a valid Bearer token. No new authentication mechanism is introduced; the existing OAuth2 authorization server is reused.
- Chat history persists only for the duration of the current page session; no server-side persistence of chat history is required.
- The scope of natural language queries is limited to home listing attributes; out-of-scope queries receive a graceful informational response.
- Only one active chat session per page view is supported; concurrent multi-tab behavior is out of scope.
