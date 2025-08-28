# 🔍 Logical Components

## 🏛 Core Services

- **Loan Product Service**
    - Manages loan product catalog (personal loan, mortgage, auto loan).
    - Stores interest rates, terms, eligibility criteria.
    - Provides search and detail APIs.

- **Loan Application Service**
    - Handles loan application lifecycle (submit, review, approve/reject).
    - Integrates with external credit scoring service.
    - Supports document upload & secure storage.
    - Publishes application status events.

- **Rating & Review Service**
    - Manages customer ratings and reviews of loan products.
    - Provides aggregated product rating for display in product catalog.
    - Enforces rule: only approved applicants can review.

## 🧩 Supporting Components

- **User Service**
    - Manages authentication, authorization, and user profiles.
    - Provides role-based access (customer, loan officer, admin).

- **Notification Service**
    - Sends email/SMS notifications for status updates and approvals.

- **API Gateway**
    - Single entry point for all clients.
    - Enforces security, routing, throttling.

## 🗄 Data Stores

- Loan Product DB (SQL)
- Loan Application DB (SQL/Document DB for uploaded files metadata)
- Ratings DB (NoSQL for reviews, aggregations)
- User DB (SQL with strong security & encryption)

## 🔄 External Systems

- **Credit Scoring Service**: Third-party system for automated credit checks.
- **Email/SMS Provider**: External service for customer notifications.
