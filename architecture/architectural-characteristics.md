# 📐 Architectural Characteristics

The system is designed with the following architectural characteristics in mind:

## ✅ Quality Attributes

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

- **Modifiability**
    - Support evolving business rules (e.g., new eligibility criteria, new loan products).
    - Independent deployments of microservices without impacting the whole system.

- **Auditability**
    - Track all loan application events for compliance and regulatory needs.
    - Maintain review and rating history.

- **Usability**
    - User-friendly interface for exploring products and tracking application status.
    - Clear status notifications (submitted, under review, approved, rejected).

## 📊 Trade-offs

- Favor **consistency within a service**, but eventual consistency across services (e.g., loan application status vs. user dashboard view).
- Emphasis on **security & reliability** over absolute performance.
