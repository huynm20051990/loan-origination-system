# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**EasyApply** — a digital platform for home buying and loan applications. Built as a microservices system with event-driven architecture, AI-powered assessments, and an Angular frontend.

## Build & Run Commands

### Java (Gradle, Java 21)
```bash
./gradlew build                                        # Build all modules
./gradlew :microservices:home-service:build            # Build a specific module
./gradlew :microservices:home-service:test             # Test a specific module
./gradlew test --tests "com.example.SomeTest" --info   # Run a single test class
```

### Frontend (Angular 21, inside `app-ui/`)
```bash
npm start          # Dev server
npm run build      # Production build
npm test           # Run tests with Vitest
```

### Docker (Full System)
```bash
docker-compose up -d                              # Full stack (Kafka, CDC, all services, observability)
docker-compose -f docker-compose-demo.yml up -d   # Simplified demo (single DB, no Kafka/Cassandra)
docker-compose down                               # Stop all services
docker-compose logs -f <service-name>             # Tail logs for a service
```

### Obtaining an OAuth2 Token (for API testing)
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token \
  -d grant_type=client_credentials -d scope="product:read product:write" \
  -s | jq -r .access_token)
```

### Registering Debezium CDC Connectors (after `docker-compose up`)
```bash
bash scripts/register-connectors.sh
```

## Architecture

### Service Map

| Service | Local Port | Tech | Database |
|---|---|---|---|
| API Gateway | 8443 (HTTPS) | Spring Cloud Gateway, WebFlux | — |
| Auth Server | 9999 | Spring Security OAuth2 Authorization Server | — |
| Home Service | 7004 | Spring Boot, Spring AI (Gemini), R2DBC | PostgreSQL:5432 (pgvector) |
| App Service | 7005 | Spring Boot, Spring Cloud Stream | PostgreSQL:5433 (pgvector) |
| Assessment Service | 7006 | Spring Boot, Spring AI (Gemini), Spring Cloud Stream | PostgreSQL:5434 (pgvector) + Cassandra:9042 |
| Notification Service | 7007 | Spring Boot, Spring Cloud Stream | PostgreSQL:5435 |
| Assessment MCP Server | 4004 | Spring Boot, Spring AI MCP Server | — |
| App UI | via gateway | Angular 21, Nginx | — |
| Observability | 9411/9090/3000 | Zipkin, Prometheus, Grafana | — |

Inside Docker, services communicate over an internal network; each microservice runs on port 80.

### Event Flow (Transactional Outbox + CDC)

Services write domain events to an `outbox` table within the same DB transaction as business data. Debezium reads PostgreSQL WAL (logical decoding) and publishes to Kafka. Downstream services consume those events.

```
App Service → outbox table → Debezium CDC → Kafka → Assessment Service
                                                  → Notification Service
```

Key Kafka topics: `application.loan_application`, `application.application_submitted`

### Hexagonal Architecture

All microservices follow Hexagonal (Ports & Adapters) architecture:
- `domain/` — pure business logic, no framework dependencies
- `application/` — use cases / application services (ports)
- `infrastructure/` — adapters: REST controllers, Kafka listeners, DB repositories, Spring AI integrations

### AI Features

- **Home Service:** Semantic search using Google Gemini embeddings + pgvector (768-dim HNSW index)
- **Assessment Service:** Multi-turn AI agent with 8-step workflow (Identity, Credit, Income, Collateral, etc.), Cassandra-backed chat memory, RAG with pgvector, and an MCP client talking to Assessment MCP Server
- **Assessment MCP Server:** Implements Model Context Protocol — exposes assessment domain capabilities as tools for LLMs

### Configuration

Service configs live in `config-repo/` (one YAML per service): `home.yml`, `app.yml`, `assessment.yml`, `notification.yml`, `gateway.yml`, `auth-server.yml`, `assessment-mcp-server.yml`.

Runtime secrets are in `.env` (loaded by Docker Compose): `POSTGRES_USR`, `POSTGRES_PWD`, `GATEWAY_TLS_PWD`, `GEMINI_API_KEY`.

### Shared Modules

- `api/` — shared DTOs and API contracts used across services
- `contracts/` — consumer-driven contract tests (Spring Cloud Contract)
- `util/` — common utilities

### Database Initialization

SQL init scripts are in `database/init-<service>/`. CDC connector definitions are in `connectors/`. Demo mode uses `database/init-demo/` and a single PostgreSQL instance.

## Active Technologies
- Java 21 LTS (backend), TypeScript strict mode (frontend, Angular 21) + Spring Boot 3.x, Spring AI (Google Gemini), Spring Security OAuth2, RxJS, Vitest (002-ai-chat-box)
- PostgreSQL + pgvector (HNSW cosine, 768-dim) for session/messages/embeddings; Apache Cassandra for Spring AI chat memory (002-ai-chat-box)

## Recent Changes
- 002-ai-chat-box: Added Java 21 LTS (backend), TypeScript strict mode (frontend, Angular 21) + Spring Boot 3.x, Spring AI (Google Gemini), Spring Security OAuth2, RxJS, Vitest
