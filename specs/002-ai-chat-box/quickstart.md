# Quickstart: AI Assistant Chat Box (002-ai-chat-box)

This guide explains how to set up the development environment, run the new `chat-service`, and verify the chat box feature end-to-end.

---

## Prerequisites

- Docker + Docker Compose installed and running
- Java 21 JDK (`java -version` should show 21)
- Node.js 20+ (`node -v`)
- `GEMINI_API_KEY` set in your `.env` file
- Existing services already run successfully via `docker-compose up -d`

---

## 1. Environment Setup

Ensure `.env` contains:
```bash
POSTGRES_USR=your_db_user
POSTGRES_PWD=your_db_password
GATEWAY_TLS_PWD=your_tls_password
GEMINI_API_KEY=your_gemini_api_key
```

No new secrets are required for `chat-service` beyond `GEMINI_API_KEY` (already used by `home-service`).

---

## 2. Start the Full Stack

```bash
# Start all services including chat-service
docker-compose up -d

# Verify chat-service is healthy
docker-compose logs -f chat-service
# Expected: "Started ChatApplication" and actuator health: UP
```

`chat-service` depends on:
- `chat-pg` (PostgreSQL with pgvector, port 5436) — auto-initialized via `database/init-chat/`
- `cassandra` (shared, port 9042) — `chat_memory` keyspace auto-created
- `auth-server` — for token validation
- `home-service` — for RAG embedding sync

---

## 3. Obtain an OAuth2 Token

```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq -r .access_token)

echo $ACCESS_TOKEN   # Verify non-null
```

---

## 4. Smoke Test the Chat API

### Create a session
```bash
curl -k -s -X POST https://localhost:8443/api/v1/chat/sessions \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" | jq .

# Expected:
# { "sessionId": "<uuid>", "userId": "...", "createdAt": "..." }

SESSION_ID=<uuid from above>
```

### Send a message (streaming)
```bash
curl -k -N -X POST https://localhost:8443/api/v1/chat/sessions/$SESSION_ID/messages \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -H "Accept: text/event-stream" \
  -d '{"content": "Show me 3-bedroom homes under $500,000"}'

# Expected: stream of SSE events
# data: {"token":"Based","done":false}
# data: {"token":" on","done":false}
# ...
# data: {"token":"","done":true,"messageId":"<uuid>"}
```

### Get message history
```bash
curl -k -s https://localhost:8443/api/v1/chat/sessions/$SESSION_ID/messages \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq .
```

---

## 5. Run Backend Tests

```bash
# All tests for chat-service
./gradlew :microservices:chat-service:test

# Integration tests only (requires Docker for Testcontainers)
./gradlew :microservices:chat-service:test --tests "*IT"

# Contract tests
./gradlew :contracts:test
```

---

## 6. Run Frontend

```bash
cd app-ui
npm start
# Navigate to http://localhost:4200
# The chat side panel should appear on the home page alongside listings
```

### Run Angular tests
```bash
cd app-ui
npm test
# ChatBoxComponent and ChatService specs must pass
```

---

## 7. Verify RAG Sync

The `chat-service` syncs home listing embeddings from `home-service` every 15 minutes.  
To trigger a manual sync (via actuator):
```bash
curl -k -s -X POST https://localhost:8443/actuator/sync-embeddings \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```
Or wait for the scheduled job to run. Check logs:
```bash
docker-compose logs -f chat-service | grep "syncHomeEmbeddings"
```

---

## 8. Observability

| Tool | URL | What to check |
|---|---|---|
| Zipkin | http://localhost:9411 | Trace `POST /api/v1/chat/sessions/*/messages` spans |
| Prometheus | http://localhost:9090 | `chat_message_total`, `chat_ai_latency_seconds` |
| Grafana | http://localhost:3000 | Chat service dashboard panel |

---

## 9. Troubleshooting

| Symptom | Likely Cause | Fix |
|---|---|---|
| `chat-service` fails to start | `chat-pg` not ready | `docker-compose restart chat-service` after pg is healthy |
| Empty RAG results | Embeddings not synced yet | Wait 15 min or trigger manual sync |
| 403 on session endpoints | JWT mismatch | Verify `ACCESS_TOKEN` is fresh (tokens expire) |
| SSE stream hangs | AI backend timeout | Check `GEMINI_API_KEY` validity; chat-service logs will show `503` |
