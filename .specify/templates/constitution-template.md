# Project Constitution: [Project Name]

**Version:** 1.0.0  
**Effective Date:** [DATE]  
**Approved By:** [Names/Roles]

---

## Project Identity

**Name:** [Project Name]  
**Purpose:** [One-sentence mission statement]  
**Primary Users:** [Who uses this software]

---

## Core Principles

### I. Architectural Purity (Hexagonal/Ports & Adapters)
Mandatory separation of concerns. The **domain** layer MUST be a "POJO-only" zone with zero dependencies on frameworks (Spring, JPA, Kafka, Jackson). Infrastructure MUST NEVER leak into business logic.

### II. Spec-Driven Development (SDD)
The specification is the single source of truth. No implementation code is written until a corresponding `.spec` file is validated via the Spec-Kit (`specify-cli`).

### III. Test-First Engineering (Red-Green-Refactor)
Every feature requires a failing test before implementation. Integration tests MUST use real containers (Testcontainers); mocking persistence (PostgreSQL/pgvector) or messaging middleware (Kafka) is strictly prohibited.

---

## Technology Stack

### Frontend
- [x] Framework: [e.g., Angular 21]
- [x] Language: [e.g., TypeScript Strict Mode]
- [x] State Management: [e.g., Signals / RxJS]

### Backend (Java)
- [x] Runtime: [e.g., Java 21 LTS (Virtual Threads enabled)]
- [x] Framework: [e.g., Spring Boot 3.x / Spring AI]
- [x] Persistence: [e.g., PostgreSQL 15+ / PgVector / HNSW Index]
- [x] Messaging: [e.g., Apache Kafka / Debezium Outbox Pattern]

### Infrastructure
- [x] Hosting: [e.g., Cloud Droplet / Kubernetes]
- [x] Gateway: [e.g., Spring Cloud Gateway / Nginx SSL]
- [x] CI/CD: [e.g., GitHub Actions / Gradle Wrapper]

---

## Quality Standards

### Testing Requirements
- [x] Unit test coverage: [X]%
- [x] Integration tests: [Required/Optional]
- [x] E2E tests: [Required for critical paths]

### Code Quality
- [x] **Naming:** `{Domain}Service`, `{Domain}RepositoryPort`, `{Domain}Controller`, `{Domain}Adapter`.
- [x] **Formatting:** [e.g., Google Java Format].
- [x] **Logging:** MUST use `static final Logger LOG` (uppercase).

---

## Security Posture

### Authentication & Authorization
- [x] Auth pattern: [e.g., Stateless JWT / OAuth2]
- [x] Access Control: Required for all protected API paths; principal extracted in Controllers.

### Data Protection
- [x] Input validation: Enforced at Port/DTO level via Spec-Kit validation.
- [x] Query safety: Parameterized JPA/SQL only; no raw string concatenation.
- [x] Secrets: Zero hardcoded credentials; all secrets stored in `.env` or Vault.

---

## Performance Baselines
- [x] **API p95 (Non-AI):** < [X] ms
- [x] **AI First-Token Latency:** < [X] seconds (Streaming REQUIRED)
- [x] **Database connection acquisition:** < [X] ms
- [x] **Concurrency:** Optimized for Virtual Threads (`spring.threads.virtual.enabled: true`).

---

## Exceptions Process

When a constitutional principle cannot be followed:
1. Document the specific principle being violated.
2. Explain why violation is necessary (technical debt or delivery constraint).
3. Describe mitigation measures (e.g., follow-up refactoring task).
4. Get approval from: [Lead Role].
5. Set review date (max 90 days).

Exceptions tracked in: `.specify/memory/exceptions.md`