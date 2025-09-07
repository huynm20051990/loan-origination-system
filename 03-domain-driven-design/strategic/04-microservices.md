# 📌 Microservices Identification

Based on DDD subdomains and bounded contexts, the following microservices are defined:

---

## **Core Domain Microservices**

| **Microservice** | **Purpose / Responsibility** | **Bounded Context** |
|-----------------|-----------------------------|------------------|
| Loan Application Service | Handles borrower loan applications, manages application lifecycle, initial validation | Loan Application Context |
| Underwriting Service | Evaluates borrower risk, creditworthiness, DTI/LTV, and generates underwriting findings | Underwriting Context |
| Loan Decision Engine Service | Applies final loan decision rules, calculates risk score, approves or rejects loan | Loan Decision Context |

---

## **Supporting Domain Microservices**

| **Microservice** | **Purpose / Responsibility** | **Bounded Context** |
|-----------------|-----------------------------|------------------|
| Borrower Service | Manages borrower personal information, identity verification, KYC/AML checks | Borrower Context |
| Income Verification Service | Pulls and verifies borrower income, employment, and tax documents | Financial Verification Context |
| Asset Verification Service | Pulls and verifies borrower bank balances, investments, and other assets | Financial Verification Context |
| Property Service | Verifies property details, appraisal, and title | Property Context |
| Notification Service | Sends emails, alerts, and notifications to borrowers | Notification Context |
| Loan Process Orchestrator | Coordinates workflow between microservices, handles events, and drives application process | Loan Process Context |
| Credit Service | Retrieves and standardizes credit reports and scores from credit bureaus | Credit Context |
| Investor Service | Handles loan sale, MBS securitization, and reporting to investors | Investor Context |
| Escrow / Closing Service | Manages escrow accounts, fund disbursement, and closing process | Escrow Context |
| Document Service | Provides storage, retrieval, and management of all borrower and loan-related documents | Document Context |

---

## **Generic Domain Microservices**

| **Microservice** | **Purpose / Responsibility** | **Domain Terms / Context** |
|-----------------|-----------------------------|---------------------------|
| Auth Service | Authentication and authorization (SSO, OAuth2, Keycloak) | User, Role, Access Token |
| User Service | User management and role assignments | User Profile, Group, Account Status |
| Logging Service | Centralized logging and monitoring | Log Entry, Metric, Alert |
| Reporting Service | Reporting, dashboards, and analytics | Report, Dashboard, KPI, Data Export |
| Integration Service | Provides API gateway, messaging, and external system connectors | API, Event, Message Queue, Connector |

---

### **Notes**
- Supporting microservices now include previously generic mortgage-specific services like Credit, Investor, Escrow, and Document services.
- Generic microservices handle cross-cutting concerns such as authentication, user management, logging, reporting, and integrations.
- All microservices are designed with **bounded contexts**, **own domain models**, and communicate via **APIs or events**.
