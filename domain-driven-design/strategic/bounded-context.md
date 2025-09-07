# 📌 Mortgage System - Bounded Contexts by Subdomain

In Domain-Driven Design (DDD), a **Bounded Context** defines the boundaries within which a particular domain model is consistent and has its own language, rules, and logic.

---

## **Core Domain Bounded Contexts**

| **Subdomain** | **Bounded Context** | **Description** |
|---------------|------------------|----------------|
| Loan Origination | Loan Application Context | Handles borrower intake, loan product selection, application submission, and initial validation. |
| Underwriting | Underwriting Context | Evaluates borrower risk, creditworthiness, collateral, DTI/LTV ratios, and generates underwriting findings. |
| Loan Decision Engine | Loan Decision Context | Applies final decision rules, calculates risk scores, and approves or rejects the loan. |

---

## **Supporting Subdomain Bounded Contexts**

| **Subdomain** | **Bounded Context** | **Description** |
|---------------|------------------|----------------|
| Borrower Management / KYC | Borrower Context | Manages borrower identity, contact info, KYC/AML verification, and compliance data. |
| Income & Asset Verification | Financial Verification Context | Verifies borrower income, employment, and assets using third-party integrations. |
| Property Verification | Property Context | Manages property details, appraisal, title search, and verification of ownership. |
| Notification Service | Notification Context | Sends emails, alerts, and notifications to borrowers regarding application status. |
| Process Orchestration (Process Manager) | Loan Process Context | Coordinates workflow of all services, handles events, and drives the application through various stages. |

---

## **Generic Subdomain Bounded Contexts**

| **Subdomain** | **Bounded Context** | **Description** |
|---------------|------------------|----------------|
| Credit Bureau Integration | Credit Context | Retrieves and standardizes credit reports and scores from bureaus (Experian, Equifax, TransUnion). |
| Secondary Market / Investor Reporting | Investor Context | Handles loan sale, MBS securitization, and reporting to investors. |
| Escrow & Closing Management | Escrow Context | Manages escrow accounts, fund disbursement, and closing procedures. |
| Document Management & Storage | Document Context | Provides storage, retrieval, and management of all borrower and loan-related documents. |

---

### **Notes**
- Each Bounded Context has its own **model, rules, and language**.
- Contexts communicate via **well-defined interfaces or messaging events**.
- Core Domain contexts contain the **most business-critical logic**, while supporting and generic contexts handle auxiliary functionality.  
