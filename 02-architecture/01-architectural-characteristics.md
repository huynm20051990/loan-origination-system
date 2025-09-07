# 📌 Architectural Characteristics

The system is designed with the following architectural characteristics in mind:

| Attribute       | Description                                                                 |
|-----------------|-----------------------------------------------------------------------------|
| Scalability     | Services scale independently to handle growing load (e.g., credit check).   |
| Availability    | 24/7 uptime, resiliency patterns for external dependencies.                 |
| Performance     | Fast product search, near real-time status updates.                         |
| Security        | Encryption, OAuth2/OIDC, RBAC for sensitive data protection.                |
| Modifiability   | Business rules and services evolve independently via microservice updates. |
| Auditability    | Full event tracking for compliance and regulatory requirements.             |
| Usability       | Clear, user-friendly UI with transparent loan application tracking.         |

- **Scalability**
    - Must handle an increasing number of users exploring loan products and submitting applications.
    - Microservices can be scaled independently (e.g., credit-check vs. rating).

- **Availability & Reliability**
    - System must be available 24/7 for customers to explore and apply for loans.
    - Resiliency patterns (circuit breaker, retries) required for external credit service integration.

- **Performance**
    - Quick product search and detail retrieval.
    - Near real-time loan status updates.

- **Security**
    - Sensitive data (personal, financial) must be encrypted in transit and at rest.
    - Secure authentication & authorization (OAuth 2.0 / OIDC).
    - Role-based access control (customers, loan officers, admins).

- **Mod**
