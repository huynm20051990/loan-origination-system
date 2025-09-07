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
