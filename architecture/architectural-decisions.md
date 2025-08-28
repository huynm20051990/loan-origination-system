# 📝 Architectural Decision Records (ADR)

---

## ADR-001: Microservices Architecture
**Status:** Accepted

**Context:**  
We need an architecture that supports scalability, modularity, and the ability to evolve business capabilities independently. A monolithic design would be simpler initially but limits agility as the system grows.

**Decision:**  
Adopt a **Microservices Architecture**, where services are decomposed by business domains (Loan Product, Loan Application, Ratings & Reviews).

**Consequences:**
- ✅ Supports independent deployment and scalability.
- ✅ Aligns with DDD bounded contexts.
- ❌ Introduces operational complexity (service discovery, monitoring, CI/CD).

**Governance:**
- Service boundaries must follow domain boundaries.
- All new business features must map into an existing service or require a new one.

**Notes:**  
Operational complexity will be managed via DevOps practices and platform tooling (Kubernetes, Service Registry).

---

## ADR-002: API Gateway
**Status:** Accepted

**Context:**  
Clients need a unified entry point. Without a gateway, clients would have to manage multiple service endpoints, increasing complexity and reducing security.

**Decision:**  
Introduce an **API Gateway** to handle routing, authentication, and throttling.

**Consequences:**
- ✅ Simplifies client integration.
- ✅ Centralized security enforcement.
- ❌ Gateway may become a bottleneck if not scaled properly.

**Governance:**
- All external requests must go through the gateway.
- API versioning and deprecation policies are managed at the gateway.

**Notes:**  
Gateway will also log requests for monitoring and auditability.

---

## ADR-003: Event-driven Messaging
**Status:** Accepted

**Context:**  
Inter-service communication must be decoupled for resilience. Synchronous calls create tight coupling and increase risk of cascading failures.

**Decision:**  
Use **RabbitMQ/Kafka** for asynchronous event-driven communication between services.

**Consequences:**
- ✅ Improves resilience and decoupling.
- ✅ Enables reactive workflows (e.g., notifications on application status).
- ❌ Eventual consistency must be handled.
- ❌ Consumers must be idempotent to avoid duplicate processing.

**Governance:**
- All inter-service domain events must be published to a message broker.
- Event schemas must be versioned and backward compatible.

**Notes:**  
Patterns like Saga and Circuit Breaker will be used for distributed transaction handling.

---

## ADR-004: Database per Service
**Status:** Accepted

**Context:**  
Shared databases create tight coupling and hinder independent deployments.

**Decision:**  
Each service owns its **separate database** (SQL or NoSQL depending on domain needs).

**Consequences:**
- ✅ Autonomy and independence for each service.
- ✅ Enables polyglot persistence.
- ❌ Requires handling distributed transactions (no ACID across services).
- ❌ Data duplication may occur.

**Governance:**
- No direct cross-service DB access.
- Data consistency managed through events and sagas.

**Notes:**  
Loan Application DB may use SQL + document storage for uploaded files metadata.

---

## ADR-005: Security Model
**Status:** Accepted

**Context:**  
System handles sensitive financial and personal data. Authentication and authorization must be secure and standardized.

**Decision:**  
Use **OAuth 2.0 / OIDC** for authentication and role-based authorization.

**Consequences:**
- ✅ Secure, industry-standard approach.
- ✅ Easy integration with external Identity Providers.
- ❌ Additional setup/maintenance complexity.

**Governance:**
- All services must externalize security concerns to the gateway and Identity Provider.
- Security audits conducted quarterly.

**Notes:**  
Roles include Customer, Loan Officer, and Admin.

---

## ADR-006: External Credit Service Integration
**Status:** Accepted

**Context:**  
Loan applications require real-time credit checks. De
