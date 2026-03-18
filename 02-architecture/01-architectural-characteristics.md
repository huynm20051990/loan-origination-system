# 📌 Architectural Characteristics

The system is designed with the following architectural characteristics in mind:

| Attribute      | Description                                                               |
|----------------|---------------------------------------------------------------------------|
| Usability      | Clear, user-friendly UI with transparent loan application tracking.       |
| Scalability    | Services scale independently to handle growing load (e.g., credit check). |
| Availability   | 24/7 uptime, resiliency patterns for external dependencies.               |
| Reliability    | Ensure no application is lost during service or network failures..        |
| Performance    | Fast product search, near real-time status updates.                       |
| Security       | Encryption, OAuth2/OIDC, RBAC for sensitive data protection.              |
| Cost-Effective | Optimize LLM consumption to reduce the cost.                              |

- **Usability**
    - User-friendly interface for exploring properties and tracking application status.
    - Clear status notifications.
- **Scalability**
    - Must handle an increasing number of users exploring properties and submitting applications.
    - Microservices can be scaled independently.

- **Availability & Reliability**
    - System must be available 24/7 for customers to explore and apply for loans.
  
- **Availability & Reliability**
    - Resiliency patterns (circuit breaker, retries) required for external credit service integration.
    - Guaranteed message delivery and data consistency via the Transactional Outbox pattern and Kafka CDC.

- **Performance**
    - Quick product search and detail retrieval.
    - Near real-time loan status updates.

- **Security**
    - Sensitive data (personal, financial) must be encrypted in transit and at rest.
    - Secure authentication & authorization (OAuth 2.0 / OIDC).
    - Role-based access control (customers, loan officers, admins).

- **Cost-Effective**
    - Strategic token management through local vector retrieval.
    - Dynamic tool search with Tool Search Tool or Pre-Select Tool.

## 📊 Trade-offs

- Favor **consistency within a service**, but eventual consistency across services (e.g., loan application status vs. user dashboard view).
- Emphasis on **security & reliability** over absolute performance.
