# 📌 Mortgage Business Process - DDD Subdomain Analysis

## **1. Core Subdomain**
These are the unique, strategic parts of the mortgage business:

| **Subdomain** | **Reasoning** |
|---------------|---------------|
| Loan Origination | Intake of applications, borrower interaction, and pre-qualification is business-critical. Differentiation can occur in evaluation, personalized offers, or speed. |
| Underwriting | Risk assessment, eligibility evaluation, and decision-making is core intellectual property. |
| Loan Decision Engine | Automating final approval decisions using business-specific rules is strategic. |

---

## **2. Supporting Subdomain**
Specialized but non-core; supports the main business:

| **Subdomain** | **Reasoning** |
|---------------|---------------|
| Borrower Management / KYC | Managing borrower identity and compliance; can use standardized verification services. |
| Income & Asset Verification | Supports underwriting; often integrates with third-party providers. |
| Property Verification | Supports collateral assessment using MLS and appraisal services. |
| Notification Service | Sends emails/alerts; important for UX but not differentiating. |
| Process Orchestration (Process Manager) | Coordinates workflow; can leverage messaging/event-driven frameworks. |

---

## **3. Generic Subdomain**
Commoditized or common services; off-the-shelf solutions often suffice:

| **Subdomain** | **Reasoning** |
|---------------|---------------|
| Credit Bureau Integration | Standard credit reporting from Experian, Equifax, TransUnion; generic integration. |
| Secondary Market / Investor Reporting | Selling loans to Fannie Mae, Freddie Mac, or tracking remittance is common; can use existing platforms. |
| Escrow & Closing Management | Standardized financial/legal process; usually supported by external escrow providers. |
| Document Management & Storage | Generic document upload, storage, and retrieval; not business differentiating. |

---

## **Summary Table**

| **Subdomain Type** | **Subdomains** |
|-------------------|----------------|
| **Core Domain** | Loan Origination, Underwriting, Loan Decision Engine |
| **Supporting Domain** | Borrower/KYC Management, Income & Asset Verification, Property Verification, Notification Service, Process Orchestration |
| **Generic Domain** | Credit Bureau Integration, Secondary Market / Investor Reporting, Escrow & Closing Management, Document Management & Storage |
