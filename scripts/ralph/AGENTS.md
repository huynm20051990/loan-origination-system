# Project Patterns and Conventions

## Overview
EasyApply is a platform designed to streamline the home buying and loan origination process. It utilizes AI-assisted property discovery, automated financial verification, and real-time application tracking to serve borrowers, home sellers, and loan officers.

## Code Style

### General
- Java Style: Google Java Format (enforced via Gradle plugin).
- TypeScript: TypeScript Strict Mode is mandatory for frontend development.
- Domain Purity: The domain/ layer must be a "POJO-only" zone. No dependencies on Spring, JPA, Kafka, or Jackson are allowed here.
- Null Safety: Use Optional instead of returning null.
- Concurrency: Java 21 Virtual Threads are enabled; leverage them for I/O-bound tasks.

### Naming Conventions
- Files: kebab-case (Frontend/General); Standard Java naming for backend.
- Functions/Methods: camelCase.
- Classes: PascalCase.
    - Suffixes: {Domain}Service, {Domain}RepositoryPort, {Domain}Controller, {Domain}Adapter.
- Constants: SCREAMING_SNAKE_CASE.
- Loggers: private static final Logger LOG = LoggerFactory.getLogger(...) (Uppercase LOG).

### Comments
- Public APIs: Add Javadoc (Java) or JSDoc (TS) for all public classes and methods.
- Explain WHY: Focus comments on the rationale behind complex logic rather than stating what the code does.
- Test Purposes: Document the importance and specific scenario of each test to assist future iterations.

## Architecture

### Directory Structure (Hexagonal / Ports & Adapters)

```
src/main/java/com/loan/origination/system/microservices/[service]
  domain/             # POJOs, Value Objects, Domain Exceptions
  application/
    port/
      input/          # Primary Port interfaces (Use-case contracts)
      output/         # Secondary Port interfaces (Repo/Messaging contracts)
    service/          # Use-case implementations
  infrastructure/
    input/            # REST Controllers, Kafka Consumers (Primary Adapters)
    output/           # JPA Repos, Kafka Producers, Email Adapters (Secondary Adapters)
```

### Patterns
- Transactional Outbox: Use with Debezium CDC for guaranteed message delivery; direct produce-on-commit is prohibited.
- Spec-Driven Development (SDD): No implementation code is written until a .specify spec file is reviewed.
- Circuit Breakers: External integrations must be wrapped using Resilience4j.

## Testing

### Framework
- Backend: JUnit 5, Mockito, and Testcontainers (for PostgreSQL/Kafka).
- Frontend: Jest + Angular Testing Library.
- Contract Testing: Required for inter-service API compatibility.

### Conventions
- Red-Green-Refactor: Write a failing test before any implementation code.
- No Mocking Infrastructure: Mocking PostgreSQL, pgvector, or Kafka in integration tests is strictly prohibited; use real containers.
- Coverage:
    - Unit tests: ≥ 80% on domain/ and application/service/ packages.

### Running Tests
- Maven: mvn test
- Gradle: ./gradlew test
- Frontend: npm test (All tests) or npm test -- --coverage (With coverage report)

## Build Commands

# Java/Spring
```
./gradlew compileJava # Compile
./gradlew check       # Static analysis and formatting check
```
# Frontend
```
npm run typecheck     # Type checking
npm run lint          # Linting
npm run build         # Production build
```

## Git Conventions
- Conventional Commits: Use prefixes like feat:, fix:, docs:, chore:, refactor:.
- Atomic Commits: Keep changes focused and single-purpose.
- Branching: All work must be performed on feature branches; never commit directly to main or master.

## Performance Baselines
- API p95: < 200 ms (non-AI).
- AI Search First-Token: < 3 seconds (Streaming via SSE/Websocket required).
- Kafka E2E Latency: < 500 ms p99.

## Discovered Learnings
(Ralph will add learnings here)

---
Last updated: 2026-04-07