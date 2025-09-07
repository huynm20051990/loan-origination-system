# 📌 Mortgage System - Bounded Contexts by Subdomain (Updated)

In Domain-Driven Design (DDD), a **Bounded Context** defines the boundaries within which a particular domain model is consistent and has its own language, rules, and logic.

---

## **Core Domain Bounded Contexts**

| **Subdomain** | **Bounded Context** | **Description** |
|---------------|------------------|----------------|
| Loan Origination | Loan Application Context | Handles borrower intake, loan product selection, application submission, and initial validation. |
| Underwriting | Underwriting Context | Evaluates borrower risk, creditworthiness, DTI/LTV ratios, and generates underwriting findings. |
| Loan Decision Engine | Loan Decision Context | Applies final decision rules, calculates risk score, approves or rejects the loan. |

---

## **Supporting Domain Bounded Contexts**

| **Subdomain** | **Bounded Context** | **Description** |
|---------------|------------------|----------------|
| Borrower Management / KYC | Borrower Context | Manages borrower identity, contact info, KYC/AML verification, and compliance data. |
| Income & Asset Verification | Financial Verification Context | Verifies borrower income, employment, and assets using third-party integrations. |
| Property Verification | Property Context | Manages property details, appraisal, and title verification. |
| Notification Service | Notification Context | Sends emails, alerts, and notifications to borrowers. |
| Process Orchestration (Process Manager) | Loan Process Context | Coordinates workflow of all services, handles events, and drives the application through various stages. |
| Credit Bureau Integration | Credit Context | Retrieves and standardizes credit reports and scores from bureaus (Experian, Equifax, TransUnion). |
| Secondary Market / Investor Reporting | Investor Context | Handles loan sale, MBS securitization, and reporting to investors. |
| Escrow & Closing Management | Escrow Context | Manages escrow accounts, fund disbursement, and closing procedures. |
| Document Management & Storage | Document Context | Provides storage, retrieval, and management of all borrower and loan-related documents. |

---

## **Generic Domain Bounded Contexts**

| **Subdomain** | **Bounded Context** | **Description** |
|---------------|------------------|----------------|
| Auth Service | Authentication Context | Handles authentication, authorization, SSO, OAuth2, and Keycloak integration. |
| User Service | User Management Context | Manages user profiles, roles, groups, and account status. |
| Logging Service | Logging & Monitoring Context | Centralized logging, metrics collection, and alerting. |
| Reporting Service | Reporting Context | Dashboards, analytics, KPIs, and data export. |
| Integration Service | Integration Context | Provides API gateway, messaging infrastructure, and external system connectors. |

---

### **Notes**
- Supporting Bounded Contexts now include previously generic mortgage-specific services like Credit, Investor, Escrow, and Document services.
- Generic Bounded Contexts handle cross-cutting concerns such as authentication, user management, logging, reporting, and integrations.
- Each context owns its **domain model, rules, and logic**, and communicates with others via **events or APIs**.
