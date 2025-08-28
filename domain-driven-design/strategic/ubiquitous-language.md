# Loan Origination System – Ubiquitous Language (DDD)

This document lists the **Ubiquitous Language** for each subdomain of the Loan Origination System (LOS).  
The goal is to align business and technical teams with shared terminology.

---

## 🔑 Core Subdomains

### 1. Loan Application & Workflow Management
- **Loan Application** – a customer’s request for credit.
- **Applicant / Borrower** – the individual or entity applying for a loan.
- **Application Status** – submitted, in-review, approved, declined, funded.
- **Application ID** – unique identifier for tracking applications.
- **Workflow Step** – stage in the application process.
- **Loan Product Selection** – the loan type chosen by the applicant.

### 2. Credit Decisioning & Risk Assessment
- **Credit Score** – numeric value representing creditworthiness.
- **Risk Assessment** – evaluation of borrower’s repayment ability.
- **Credit Bureau** – external data source for borrower history.
- **Debt-to-Income Ratio (DTI)** – borrower’s monthly debt vs. income.
- **Decision Outcome** – approve, decline, or review.

### 3. Underwriting Rules & Policy Enforcement
- **Underwriter** – person/system applying lending policies.
- **Eligibility Criteria** – rules that define loan qualification.
- **Policy Rule** – condition to validate (e.g., max LTV, employment status).
- **Exception Handling** – when a case requires manual review.
- **Approval Limit** – max loan amount under specific rules.

### 4. Loan Offer & Pricing Engine
- **Loan Offer** – a proposed loan with terms and conditions.
- **Interest Rate** – cost of borrowing (fixed or variable).
- **Loan Term** – duration of repayment (e.g., 36 months).
- **Repayment Schedule** – periodic payment plan.
- **APR (Annual Percentage Rate)** – standardized cost measure.
- **Offer Acceptance** – borrower agrees to terms.

---

## 🤝 Supporting Subdomains

### 5. Loan Product Exploration
- **Loan Product** – category of loan (personal, mortgage, auto).
- **Product Catalog** – collection of available loan products.
- **Eligibility Information** – criteria for product selection.
- **Rate Table** – product-specific rates and terms.
- **Product Comparison** – comparing multiple products.

### 6. Document Management
- **Document** – file provided by borrower (ID, payslip, statement).
- **Document Type** – category (identity, income, collateral).
- **Upload** – borrower provides a document.
- **Verification** – validating document authenticity.
- **Fraud Check** – detect manipulated or fake documents.

### 7. KYC / Identity Verification
- **KYC (Know Your Customer)** – compliance process to verify identity.
- **Identity Document** – passport, ID card, driver’s license.
- **AML Check** – anti-money laundering screening.
- **Sanctions List** – restricted individuals/entities.
- **Verification Result** – pass, fail, or manual review.

### 8. Compliance & Audit Logging
- **Audit Trail** – historical log of decisions and actions.
- **Regulatory Report** – compliance-mandated export.
- **Decision Justification** – reason for approval/decline.
- **Retention Policy** – how long records are kept.

### 9. Loan Funding & Disbursement
- **Disbursement** – releasing funds to borrower.
- **Funding Instruction** – payment order details.
- **Settlement Account** – source of funds.
- **Payout Confirmation** – acknowledgement from banking system.

### 10. Customer Communication & Notifications
- **Notification** – message to applicant (email/SMS/push).
- **Status Update** – inform borrower about progress.
- **Reminder** – request for missing documents/actions.
- **Message Template** – pre-defined notification format.

---

## ⚙️ Generic Subdomains

### 11. Authentication & Authorization
- **User** – system participant.
- **Role** – set of permissions.
- **Access Token** – credential for authentication.
- **Permission** – allowed action within system.

### 12. User Management / Roles & Permissions
- **User Profile** – information about a system user.
- **Group / Role Assignment** – mapping of permissions.
- **Account Status** – active, suspended, locked.

### 13. Logging & Monitoring
- **Log Entry** – record of system event.
- **Metric** – measured system behavior (latency, throughput).
- **Alert** – automated notification on threshold breach.

### 14. Reporting & Analytics
- **Report** – structured data view (loan performance, pipeline).
- **Dashboard** – visualization of key metrics.
- **KPI** – key performance indicator.
- **Data Export** – CSV, PDF, or BI tool integration.

### 15. Integration Infrastructure
- **API** – interface for communication.
- **Event** – message exchanged between services.
- **Message Queue** – brokered communication (Kafka, RabbitMQ).
- **Connector** – link to external system.

### 16. CRM / Customer Data Management
- **Customer Record** – centralized borrower information.
- **Contact Information** – phone, email, address.
- **Interaction History** – record of customer communication.

### 17. Payment Processing Gateway
- **Payment Request** – initiate transaction.
- **Payment Confirmation** – response from PSP.
- **Transaction ID** – unique identifier for payment.
- **Reconciliation** – verifying financial records.

---

## ✅ Key DDD Takeaway
Each **bounded context** should use its **Ubiquitous Language consistently** in code, documentation, and conversations.  
This avoids ambiguity and ensures **business experts and engineers are aligned**.
