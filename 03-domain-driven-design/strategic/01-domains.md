# 📌 Subdomains

Based on DDD principles, the mortgage system subdomains are classified as **Core**, **Supporting**, and **Generic**.

---

## **Core Domain**
These are the unique, strategic parts of the mortgage business that provide competitive advantage:

| **Subdomain** | **Reasoning** |
|---------------|---------------|
| Loan Origination | Handles borrower intake, application submission, and pre-qualification. |
| Underwriting | Evaluates borrower risk, creditworthiness, and eligibility. |
| Loan Decision Engine | Applies final decision rules, calculates risk scores, and approves or rejects loans. |

---

## **Supporting Domain**
Specialized services that support the core business:

| **Subdomain** | **Reasoning** |
|---------------|---------------|
| Borrower Management / KYC | Manages borrower identity, personal info, and AML/KYC checks. |
| Income & Asset Verification | Verifies borrower income, employment, and asset details. |
| Property Verification | Verifies property details, appraisal, and title. |
| Notification Service | Sends emails, alerts, and notifications to borrowers. |
| Process Orchestration (Process Manager) | Coordinates workflow, event handling, and loan application process. |
| Credit Bureau Integration | Retrieves and standardizes credit reports and scores from bureaus. |
| Secondary Market / Investor Reporting | Handles loan sales, securitization, and investor reporting. |
| Escrow & Closing Management | Manages escrow accounts, fund disbursement, and closing processes. |
| Document Management & Storage | Stores, retrieves, and manages all borrower and loan-related documents. |

---

## **Generic Domain**
Cross-cutting, commoditized services applicable across multiple domains:

| **Subdomain** | **Reasoning / Domain Terms** |
|---------------|-----------------------------|
| Auth Service | Authentication and authorization (SSO, OAuth2, Keycloak). Domain terms: User, Role, Access Token. |
| User Service | User management and role assignments. Domain terms: User Profile, Group, Account Status. |
| Logging Service | Centralized logging and monitoring. Domain terms: Log Entry, Metric, Alert. |
| Reporting Service | Reporting, dashboards, and analytics. Domain terms: Report, Dashboard, KPI, Data Export. |
| Integration Service | Provides API gateway, messaging, and external system connectors. Domain terms: API, Event, Message Queue, Connector. |

---

### **Notes**
- Supporting subdomains now include mortgage-specific services previously classified as generic (Credit, Investor, Escrow, Document).
- Generic subdomains handle cross-cutting concerns and are largely reusable across systems.
- Each subdomain has its **bounded context, language, and domain rules**.
