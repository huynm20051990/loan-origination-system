<!--
SYNC IMPACT REPORT
==================
Version change: (template, unpopulated) → 1.0.0
Modified principles: (none, initial population)
Added sections:
  - Project Identity (derived from README + requirements)
  - Core Principles I–V (fully populated)
  - Technology Stack (Section 2, fully populated)
  - Quality Standards & Performance (Section 3, fully populated)
  - Governance (fully populated)
Templates reviewed:
  - .specify/templates/plan-template.md ✅ aligned (Constitution Check gate references principles by concept)
  - .specify/templates/spec-template.md ✅ aligned (FR/SC structure matches security & test principles)
  - .specify/templates/tasks-template.md ✅ aligned (phase structure supports TDD mandate)
  - .specify/templates/commands/ ⚠ no command files found under commands/
Deferred TODOs:
  - None. All placeholders resolved from repo context.
-->

# EasyApply Constitution

**Version**: 1.0.0
**Effective Date**: 2026-04-07
**Approved By**: Engineering Lead (huynm20051990)

---

## Project Identity

**Name**: EasyApply
**Purpose**: Streamline the home buying and loan origination process through
AI-assisted property discovery, automated financial verification, and
real-time application tracking.
**Primary Users**: Borrowers/Home Buyers, Home Sellers, Loan Officers, Admins

---

## Core Principles

### I. Architectural Purity (Hexagonal/Ports & Adapters)

Mandatory separation of concerns. The **domain** layer MUST be a "POJO-only"
zone with zero dependencies on frameworks (Spring, JPA, Kafka, Jackson).
Infrastructure MUST NEVER leak into business logic.

Package layout is non-negotiable:
- `domain/` — entities, value objects, domain exceptions only.
- `application/port/input/` — primary port interfaces (use-case contracts).
- `application/port/output/` — secondary port interfaces (repository/messaging contracts).
- `application/service/` — use-case implementations, depends only on ports.
- `infrastructure/input/` — REST controllers, Kafka consumers (primary adapters).
- `infrastructure/output/` — JPA repositories, Kafka producers, email adapters (secondary adapters).

**Rationale**: Prevents framework lock-in and ensures business logic is
independently testable without spinning up Spring context or a database.

### II. Spec-Driven Development (SDD)

The `.specify` specification is the **single source of truth**. No
implementation code MUST be written until a corresponding spec file has been
reviewed and all acceptance scenarios are agreed upon.

Rules:
- Every feature MUST have a `spec.md` with prioritized user stories and
  measurable success criteria before implementation begins.
- Changes to requirements MUST update the spec before updating code.
- The `plan.md` Constitution Check gate MUST be evaluated against this
  constitution before Phase 0 research proceeds.

**Rationale**: Prevents scope creep and ensures the team builds what was
agreed, not what was assumed.

### III. Test-First Engineering (Red-Green-Refactor)

Every feature MUST have a failing test written and approved before
implementation starts. The Red-Green-Refactor cycle is strictly enforced.

Rules:
- Integration tests MUST use real containers (Testcontainers); mocking
  PostgreSQL, pgvector, or Kafka is strictly prohibited.
- Unit tests MUST cover domain logic independently of infrastructure.
- Contract tests MUST verify inter-service API compatibility on every change
  that touches a shared interface.
- Tests MUST fail before implementation begins — a passing test written after
  the fact does not satisfy this principle.

**Rationale**: Integration-layer mocks caused production divergence in prior
incident. Real containers eliminate this class of failure.

### IV. Security & Data Protection

Sensitive personal and financial data MUST be protected at every layer.

Rules:
- Authentication MUST use stateless JWT tokens issued via OAuth 2.0/OIDC.
- All API paths for borrower, loan, and document data MUST enforce RBAC
  (roles: `BORROWER`, `LOAN_OFFICER`, `ADMIN`).
- No raw string SQL concatenation is permitted; all queries MUST use
  parameterized JPA/JPQL or named parameters.
- Zero hardcoded credentials; all secrets MUST reside in `.env` (local) or
  a Vault-equivalent (production).
- Data in transit MUST be encrypted (TLS 1.2+); data at rest MUST be encrypted
  for PII and financial fields.
- Input validation MUST be enforced at the Port/DTO boundary via `jakarta.validation`.

**Rationale**: Loan origination data is regulated; a breach or injection
vulnerability carries legal and reputational consequences.

### V. Observability & Reliability

The system MUST be observable and resilient by default, not as an afterthought.

Rules:
- All services MUST emit structured logs using `static final Logger LOG`
  (SLF4J, uppercase) with correlation IDs propagated via Istio/W3C
  trace context.
- Distributed tracing MUST be instrumented (Jaeger/OpenTelemetry) for every
  cross-service call.
- Metrics MUST be exposed in Prometheus format and captured in Grafana
  dashboards.
- External integrations (credit bureau, third-party document services) MUST be
  wrapped in circuit breakers (Resilience4j).
- Guaranteed message delivery MUST be achieved via the Transactional Outbox
  pattern with Debezium CDC; direct Kafka produce-on-commit is prohibited
  for business-critical events.

**Rationale**: A 24/7 loan platform with real-money consequences requires
immediate visibility into failures and guaranteed event delivery.

---

## Technology Stack

### Frontend

- **Framework**: Angular (latest stable LTS)
- **Language**: TypeScript Strict Mode
- **State Management**: Angular Signals / RxJS
- **Testing**: Jest + Angular Testing Library

### Backend (Java)

- **Runtime**: Java 21 LTS (Virtual Threads enabled —
  `spring.threads.virtual.enabled: true`)
- **Framework**: Spring Boot 3.x / Spring AI
- **Persistence**: PostgreSQL 15+ with pgvector (HNSW index for AI search)
- **Messaging**: Apache Kafka (event-driven, Outbox Pattern via Debezium)
- **Logging**: SLF4J + Logback (JSON format for EFK stack ingestion)

### Infrastructure

- **Orchestration**: Kubernetes
- **Service Mesh**: Istio (mTLS, traffic management, observability)
- **Gateway**: Spring Cloud Gateway / Nginx SSL termination
- **CI/CD**: GitHub Actions + Gradle Wrapper (`./gradlew`)
- **Monitoring**: Kiali, Jaeger, EFK stack, Prometheus, Grafana

---

## Quality Standards & Performance

### Code Quality

- **Naming**: `{Domain}Service`, `{Domain}RepositoryPort`, `{Domain}Controller`,
  `{Domain}Adapter` — all class names MUST follow this convention.
- **Logging**: `private static final Logger LOG = LoggerFactory.getLogger(…)`
  (uppercase `LOG`).
- **Formatting**: Google Java Format enforced via Gradle plugin.
- **No framework annotations in domain**: `@Entity`, `@Column`, `@JsonProperty`
  are forbidden in `domain/` packages.

### Testing Requirements

- Unit test coverage: ≥ 80% on `domain/` and `application/service/` packages.
- Integration tests: Required for all persistence adapters, Kafka
  producers/consumers, and REST controllers.
- Contract tests: Required for every inter-service API boundary.
- E2E tests: Required for the critical borrower path
  (search → apply → track → decision).

### Performance Baselines

- **API p95 (non-AI endpoints)**: < 200 ms
- **AI semantic search first-token latency**: < 3 seconds (streaming REQUIRED
  via Server-Sent Events or WebSocket)
- **Database connection acquisition**: < 50 ms
- **Kafka event end-to-end (produce → consume)**: < 500 ms p99

---

## Governance

This constitution supersedes all other development practices and informal
agreements. It is the binding document for all engineering decisions on
EasyApply.

### Amendment Procedure

1. Open a PR modifying this file with proposed changes.
2. Describe the principle or section being changed, the reason, and any
   migration impact on existing code.
3. Obtain approval from the Engineering Lead.
4. Increment the version per semantic versioning rules:
   - **MAJOR**: Backward-incompatible governance changes, principle removals
     or fundamental redefinitions.
   - **MINOR**: New principles, new mandatory sections, materially expanded
     guidance.
   - **PATCH**: Clarifications, wording improvements, typo fixes.
5. Update `LAST_AMENDED_DATE` to the merge date.
6. Propagate changes to affected templates (plan, spec, tasks) in the same PR.

### Compliance

- All PRs MUST include a Constitution Check confirming no principle violations.
- Violations require an exception entry in `.specify/memory/exceptions.md` with
  justification, mitigation plan, approver, and review date (max 90 days).
- Complexity beyond what the constitution mandates MUST be justified explicitly
  in the `plan.md` Complexity Tracking table.

### Runtime Guidance

For day-to-day development workflows, refer to `COMMAND.md` in the repository
root and the `.specify/` directory for spec, plan, and task tooling.

**Version**: 1.0.0 | **Ratified**: 2026-04-07 | **Last Amended**: 2026-04-07
