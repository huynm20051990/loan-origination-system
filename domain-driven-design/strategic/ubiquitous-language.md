# 📌 Mortgage System - Ubiquitous Language (Updated)

In Domain-Driven Design (DDD), a **Ubiquitous Language** is a shared vocabulary used consistently by developers, domain experts, and stakeholders. The terms are aligned with **Core, Supporting, and Generic Subdomains**.

---

## **Core Domain Terms**
| **Term** | **Definition** |
|----------|----------------|
| Borrower | Individual applying for a mortgage or loan. |
| Loan Application | Request submitted by a borrower for a specific loan product. |
| Loan Product | Type of loan offered (e.g., personal, mortgage, auto) with specific terms and interest rate. |
| Underwriting | Process of assessing borrower risk, collateral, and eligibility for loan approval. |
| Loan Decision | Final determination of approval, conditional approval, or rejection. |
| Debt-to-Income (DTI) | Ratio of borrower’s monthly debt to monthly income, used in underwriting. |
| Loan-to-Value (LTV) | Ratio of loan amount to appraised property value, used in risk assessment. |
| Credit Risk | Evaluation of borrower’s ability to repay the loan. |

---

## **Supporting Domain Terms**
| **Term** | **Definition** |
|----------|----------------|
| Borrower Management | Service managing borrower personal information, identity verification, KYC/AML verification. |
| Income Verification | Process of validating borrower’s income via pay stubs, W-2s, tax returns, or VOE. |
| Asset Verification | Process of validating borrower’s assets (bank accounts, investments). |
| Property Verification | Process of verifying property ownership, appraisal, and title details. |
| Notification | Sending updates or loan decision communications to the borrower. |
| Process Manager | Component orchestrating loan application workflow and coordinating services. |
| Credit Report | Standardized credit data pulled from credit bureaus. |
| Investor Reporting | Handles loan sale, MBS securitization, and reporting to investors. |
| Escrow | Account managed by a third party to hold funds during closing. |
| Loan Boarding | Process of entering an approved loan into a servicing system. |
| Document Management | Storage and retrieval of all borrower and loan-related documents. |

---

## **Generic Domain Terms**
| **Term** | **Definition / Domain Context** |
|----------|--------------------------------|
| User | An individual accessing the system. |
| Role | Permissions or access level assigned to a user. |
| Access Token | Credential used to authorize a user session. |
| User Profile | Metadata and account information of a user. |
| Group | Collection of users for role/permission assignment. |
| Account Status | State of the user account (active, suspended, etc.). |
| Log Entry | Record of an event or system action. |
| Metric | Measurable value tracked by monitoring. |
| Alert | Notification triggered by monitoring thresholds. |
| Report | Analytical or operational data output. |
| Dashboard | Visual presentation of KPIs and metrics. |
| KPI | Key Performance Indicator used for measurement. |
| Data Export | Extraction of system data in usable formats. |
| API | Interface for external system communication. |
| Event | Notification or message representing a domain change. |
| Message Queue | Infrastructure for asynchronous message delivery. |
| Connector | Component linking external systems to the platform. |

---

### **Notes**
- Terms are mapped to their **subdomains and bounded contexts** to maintain clarity and consistency.
- Core domain terms define the unique, strategic differentiators of the mortgage system.
- Supporting terms provide auxiliary functions that support core workflows.
- Generic terms cover cross-cutting concerns and infrastructure services.  
