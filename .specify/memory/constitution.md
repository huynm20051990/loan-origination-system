<!--
SYNC IMPACT REPORT
==================
Version change: [TEMPLATE] → 1.0.0
Basis: Initial creation — regenerated from updated constitution-template.md (2026-04-01).
       Structure now follows the revised template: Project Identity, Core Principles,
       Technology Stack, Quality Standards, Security Posture, Performance Baselines,
       Exceptions Process.

Previous generation (same day) used a different structure derived from constitution-research.md
and did not follow the updated template — replaced entirely.

Principles defined (3, matching template):
  I.   Architectural Purity (Hexagonal/Ports & Adapters)
  II.  Spec-Driven Development (SDD)
  III. Test-First Engineering (Red-Green-Refactor)

Templates reviewed:
  - .specify/templates/plan-template.md ✅ — "Constitution Check" gate aligns with all
    three principles; "Performance Goals" aligns with Performance Baselines section
  - .specify/templates/spec-template.md ✅ — "User Scenarios & Testing" aligns with P3
    (Test-First); "Success Criteria" aligns with Performance Baselines and Security Posture
  - .specify/templates/tasks-template.md ✅ — Tests-before-implementation task ordering
    aligns with P3 (Red-Green-Refactor); Phase N "Polish" aligns with P1 and Quality Standards

Deferred items:
  - TODO(APPROVED_BY): No formal approval roles defined yet — placeholder left
  - TODO(UNIT_COVERAGE_TARGET): Minimum unit test coverage % not yet decided
  - TODO(SCHEMA_MIGRATION_TOOLING): Flyway/Liquibase not yet adopted; production
    migration strategy is a known gap — flag in any feature that modifies DB schema
-->

# Project Constitution: EasyApply

**Version:** 1.0.0
**Effective Date:** 2026-04-01
**Approved By:** TODO(APPROVED_BY): Lead architect / repository maintainer

---

## Project Identity

**Name:** EasyApply
**Purpose:** A digital platform for home buying and loan origination that guides applicants
through the full application lifecycle with AI-powered assessments and real-time streaming.
**Primary Users:** Home buyers (loan applicants) and loan officers reviewing applications

---

## Core Principles

### I. Architectural Purity (Hexagonal/Ports & Adapters)

Mandatory separation of concerns across all microservices. The **`domain/`** layer MUST be
a "POJO-only" zone with zero dependencies on frameworks (Spring, JPA, Kafka, Jackson).
Infrastructure MUST NEVER leak into business logic:

- **`domain/`** — pure business logic. Domain models MUST encode business rules as methods
  (e.g., `submit()`, `updatePrice()`, `markAsSold()`). No Spring annotations, no imports
  from `org.springframework`, `javax.persistence`, or Apache Kafka.
- **`application/`** — use cases and port interfaces only. MUST NOT import infrastructure
  classes. `@Transactional` boundaries belong here or in infrastructure adapters only.
- **`infrastructure/`** — all adapters: REST controllers, Kafka consumers/producers,
  persistence adapters, Spring AI integrations, Spring configuration. Adapters MUST
  implement port interfaces; adapters MUST NOT call each other directly.

**Hard gates — any violation blocks merge:**
- No framework annotations (`@Repository`, `@Service`, `@RestController`) in `domain/`
- No direct DB access in `application/` — use output port interfaces only
- No `assert` statements in production code — throw an explicit exception instead
- No hardcoded user IDs, emails, or environment-specific strings in `domain/` or `application/`

**Naming conventions (enforced at code review):**
- Services: `{Domain}Service`, `{Domain}ApplicationService`
- Controllers: `{Domain}Controller`
- Adapters: `{Domain}PersistenceAdapter`, `{Domain}Adapter`
- Mappers: `{Domain}WebMapper`, `{Domain}PersistenceMapper`
- Ports: `{Domain}RepositoryPort`, `{Domain}UseCase`, `{Domain}SenderPort`
- Loggers: `static final Logger LOG` (uppercase) — not `log`

**Known debts to remediate opportunistically:**
- `ApplicationService.startAssessment()` — hardcoded `userId: "huynguyen"`
- `ApplicationSubmittedConsumer` (notification-service) — hardcoded `"customer@example.com"`
- `HomePersistenceAdapter.search()` — `assert` instead of explicit null check
- Logger naming — standardize to `LOG` (currently mixed with `log` in some classes)
- JWT principal not extracted in REST controllers — email passed as query param

### II. Spec-Driven Development (SDD)

The specification is the single source of truth. No implementation task begins until
a feature's `spec.md` is approved. A spec MUST satisfy all of the following before
the first implementation task starts:

1. At least one independently testable user story with Given/When/Then acceptance scenarios
2. API contract defined for any new or modified endpoint
3. Data model declared for any new or modified persistence entity
4. Non-functional requirements stated: latency budget, isolation boundary, scalability
   approach, and AI behavior (if applicable)
5. Security review item included if the feature handles user-scoped data

Branch naming MUST follow: `<sequential-number>-<kebab-feature-name>`
(e.g., `001-chat-feature`). PRs targeting `main` MUST pass all CI gates before merge.

### III. Test-First Engineering (Red-Green-Refactor)

Every feature requires a failing test before implementation. The cycle is mandatory:

1. Write the test
2. Confirm it fails (Red)
3. Write the minimum implementation to make it pass (Green)
4. Refactor without re-introducing failures

**Test requirements by layer:**
- **Unit tests** — REQUIRED for all `domain/` business logic and `application/` service
  orchestration. Use Mockito for port interfaces. Suffix: `{ClassUnderTest}Test`
- **Integration tests** — REQUIRED for persistence adapters and Kafka consumers.
  MUST use Testcontainers (real PostgreSQL with pgvector extension, real Kafka).
  **Mocked databases are PROHIBITED.** Suffix: `{ClassUnderTest}IT`
- **Contract tests** — REQUIRED for any new or modified API consumed by another service.
  Use Spring Cloud Contract. New event types MUST have a contract in `contracts/`.
  Suffix: `{Consumer}ContractTest`
- **Frontend tests** — REQUIRED for new Angular services and components. Use Vitest +
  Angular TestBed. Services: `HttpClientTestingModule`. Components: assert rendered
  output and state, not just construction. Every component MUST have a co-located `.spec.ts`

**Decision guidance:** When delivery pressure forces a trade-off between shipping without
tests and delaying, the default is to delay. Exceptions MUST be recorded in the feature's
`plan.md` Complexity Tracking table with a follow-up task created before the PR merges.

---

## Technology Stack

### Frontend
- [x] Framework: Angular 21
- [x] Language: TypeScript (strict mode; `any` is prohibited)
- [x] State Management: RxJS observables; typed return types on all service methods
- [x] Testing: Vitest + Angular TestBed
- [x] HTTP: All requests MUST go through `app-ui/src/app/core/services/`; components
      MUST NOT call `HttpClient` directly

### Backend (Java)
- [x] Runtime: Java 21 LTS (virtual threads enabled: `spring.threads.virtual.enabled: true`)
- [x] Framework: Spring Boot 3.x; Spring AI (Google Gemini); Spring WebFlux for gateway only
- [x] Persistence: PostgreSQL + pgvector (HNSW cosine index, 768-dim); one schema per service;
      HikariCP `maximum-pool-size: 10` baseline
- [x] Messaging: Apache Kafka via Spring Cloud Stream; Debezium CDC for transactional outbox relay
- [x] Agent memory: Apache Cassandra (follow `assessment-service` keyspace pattern)
- [x] Build: Gradle multi-module; `./gradlew` wrapper committed to repo

### Infrastructure
- [x] Hosting: Docker Compose (local dev); Helm charts in `kubernetes/helm/` (Kubernetes)
- [x] Gateway: Spring Cloud Gateway on HTTPS :8443
- [x] Auth: Spring Security OAuth2 Authorization Server on :9999
- [x] Observability: Zipkin (tracing) + Prometheus + Grafana (metrics/dashboards)
- [x] CI/CD: `./gradlew build` (all modules) + `npm test` in `app-ui/` MUST pass before merge
- [x] Schema migration: `spring.jpa.hibernate.ddl-auto: update` (dev only).
      TODO(SCHEMA_MIGRATION_TOOLING): Flyway/Liquibase not yet adopted — must be resolved
      before any production deployment

New runtime dependencies MUST be declared in the feature's `plan.md` Technical Context
section. Fixed technology choices require a formal constitution amendment with architectural
justification in the PR.

---

## Quality Standards

### Testing Requirements
- [x] Unit test coverage: TODO(UNIT_COVERAGE_TARGET): Minimum % not yet set. Baseline:
      all new `domain/` and `application/` code MUST have unit tests
- [x] Integration tests: Required for all persistence adapters and Kafka consumers
- [x] E2E tests: Required for critical loan-application user journeys

### Code Quality
- [x] **Naming:** `{Domain}Service`, `{Domain}RepositoryPort`, `{Domain}Controller`,
      `{Domain}PersistenceAdapter` (see Principle I for full conventions)
- [x] **Formatting:** Standard Java formatting enforced at code review; no trailing whitespace
- [x] **Logging:** MUST use `static final Logger LOG` (uppercase)
- [x] **Streaming responses (SSE):** Components MUST show a typing/loading indicator while
      the stream is in progress and append tokens incrementally — buffering before render
      is PROHIBITED
- [x] **Error states:** Every user-facing operation MUST have an explicit error state.
      Never expose raw HTTP status codes, Java exception class names, or stack traces in UI.
      Surface `HttpErrorInfo` human-readable messages only

---

## Security Posture

### Authentication & Authorization
- [x] Auth pattern: OAuth2 (Spring Security Authorization Server); stateless Bearer tokens
- [x] Access Control: Required for all protected API paths via Spring Cloud Gateway.
      JWT principal MUST be extracted in REST controllers — email MUST NOT be passed as a
      query parameter without cross-checking the authenticated principal

### Data Protection
- [x] Input validation: Enforced at Port/DTO boundary level
- [x] Query safety: Parameterized R2DBC/JPA queries only; no raw string concatenation
- [x] Secrets: Zero hardcoded credentials anywhere in `domain/` or `application/` code.
      All credentials and API keys stored in `.env` (loaded by Docker Compose) and
      documented in `.env.example`. NEVER committed to the repository
- [x] Auth interceptor: All Angular HTTP requests (except `/oauth2/token` and `/assets/`)
      MUST be intercepted by `auth-interceptor.ts` to inject the Bearer token. New public
      paths MUST be explicitly added to the interceptor's skip list with an explanatory comment

---

## Performance Baselines
- [x] **API p95 (Non-AI):** < 200 ms
- [x] **AI First-Token Latency:** < 3 seconds (streaming REQUIRED; non-streaming calls
      MUST have a 30-second timeout with graceful error degradation)
- [x] **Database connection acquisition:** < 500 ms
- [x] **Kafka consumer lag:** < 1,000 messages in steady state; consumers MUST use explicit
      group IDs for independent offset tracking and horizontal scaling
- [x] **Concurrency:** Optimized for Virtual Threads (`spring.threads.virtual.enabled: true`).
      `@Async` thread pool executors and blocking platform-thread patterns are PROHIBITED
      where virtual threads suffice
- [x] **Vector store:** pgvector similarity searches MUST use the HNSW cosine index.
      Full-table scans on `*_embeddings` tables are PROHIBITED in production query paths.
      New embedding tables MUST declare the HNSW index in the SQL init script before first use

**Decision guidance:** Verify a threshold is breached (via Prometheus/Grafana) before adding
a cache, index, or async worker. Premature optimization that adds complexity without a
measured breach is rejected. Document both the measurement and chosen mitigation in `plan.md`.

---

## Exceptions Process

When a constitutional principle cannot be followed:
1. Document the specific principle being violated.
2. Explain why violation is necessary (technical debt or delivery constraint).
3. Describe mitigation measures (e.g., follow-up refactoring task).
4. Get approval from: lead architect / repository maintainer.
5. Set review date (max 90 days).

Exceptions tracked in: `.specify/memory/exceptions.md`

---

**Version**: 1.0.0 | **Ratified**: 2026-04-01 | **Last Amended**: 2026-04-01
