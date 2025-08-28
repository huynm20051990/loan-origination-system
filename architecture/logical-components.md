# 🔍 Logical Components

## Core Domain Services

### Loan Product Service
- Maintains the **loan product catalog** (personal, mortgage, auto).
- Stores product attributes: **interest rates, terms, eligibility criteria**.
- Exposes APIs for product **search** and **detailed view**.

### Loan Application Service
- Orchestrates the **loan application lifecycle**: submission → review → approval/rejection.
- Integrates with the **external credit scoring service**.
- Supports **document upload** with secure storage.
- Publishes **application status events** for other services.

### Rating & Review Service
- Allows customers to **submit ratings and reviews** for loan products.
- Provides **aggregated ratings** and recent feedback for product details.
- Enforces business rule: **only approved applicants can review**.

---

## Supporting Services

### User Service
- Manages **user authentication and authorization**.
- Maintains **user profiles**.
- Provides **role-based access** (customer, loan officer, admin).

### Notification Service
- Sends **email and SMS notifications** for application status and approvals.
- Subscribes to **application events** to trigger customer updates.
