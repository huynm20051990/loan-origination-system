# 🏗 Architectural Style — Microservices

The system follows a **Microservices Architecture**, with services decomposed around business capabilities.

## 🌐 Key Principles

- **Domain-driven decomposition**
    - Loan Product, Loan Application, and Loan Ratings are separate bounded contexts.
- **Independent Deployability**
    - Each service can be updated and deployed independently.
- **API-driven communication**
    - RESTful APIs (with JSON) for client-facing endpoints.
    - Asynchronous messaging (RabbitMQ/Kafka) for inter-service communication (e.g., credit check results, application status updates).
- **Polyglot persistence**
    - Each service manages its own database schema and storage needs (SQL for structured loan data, NoSQL for reviews).

## 🔄 Integration Patterns

- **Synchronous**: REST calls for product details, application submission.
- **Asynchronous**: Messaging for credit check responses, notifications, event propagation.
- **External Service Integration**: Secure API calls to third-party credit scoring service.

## ⚙️ Infrastructure

- **API Gateway** for routing, authentication, and request throttling.
- **Service Registry** for service discovery.
- **Config Server** for centralized configuration.
- **Monitoring & Logging** (Prometheus, ELK/EFK stack).
