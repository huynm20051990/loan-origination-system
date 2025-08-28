# Loan Origination System – Bounded Contexts

This document identifies **Bounded Contexts** for each subdomain of a Loan Origination System (LOS).  
Each bounded context encapsulates its own domain model, ubiquitous language, and service boundary.

---

## 🔑 Core Subdomains → Core Bounded Contexts

1. **Loan Application & Workflow Management**  
   **Bounded Context:** *Loan Application Context*
    - Captures borrower applications, manages workflow states, orchestrates the process.

2. **Credit Decisioning & Risk Assessment**  
   **Bounded Context:** *Credit Decisioning Context*
    - Owns scoring models, risk evaluation, bureau integration, decision outcomes.

3. **Underwriting Rules & Policy Enforcement**  
   **Bounded Context:** *Underwriting Context*
    - Contains underwriting policies, eligibility rules, exception handling.

4. **Loan Offer & Pricing Engine**  
   **Bounded Context:** *Loan Offer Context*
    - Produces loan offers, calculates pricing, terms, and repayment schedules.

---

## 🤝 Supporting Subdomains → Supporting Bounded Contexts

5. **Loan Product Exploration**  
   **Bounded Context:** *Loan Product Catalog Context*
    - Manages available loan products, eligibility info, rate tables, and product browsing.

6. **Document Management**  
   **Bounded Context:** *Document Management Context*
    - Handles borrower uploads, storage, classification, and verification.

7. **KYC / Identity Verification**  
   **Bounded Context:** *KYC Context*
    - Identity verification, AML checks, sanctions list screening.

8. **Compliance & Audit Logging**  
   **Bounded Context:** *Compliance Context*
    - Tracks audit trails, regulatory reports, and decision justifications.

9. **Loan Funding & Disbursement**  
   **Bounded Context:** *Funding Context*
    - Handles disbursement instructions, settlements, and payout confirmations.

10. **Customer Communication & Notifications**  
    **Bounded Context:** *Communication Context*
    - Manages email, SMS, and push notifications for loan application status.

11. **Workflow Orchestration / Process Manager**  
    **Bounded Context:** *Workflow Context*
    - Cross-cutting orchestration between Loan Application, Decisioning, Underwriting, and Funding.

---

## ⚙️ Generic Subdomains → Generic Bounded Contexts

12. **Authentication & Authorization**  
    **Bounded Context:** *Identity & Access Context*
    - Authentication, authorization, access control, tokens.

13. **User Management / Roles & Permissions**  
    **Bounded Context:** *User Management Context*
    - Manages system users, roles, permissions, and profiles.

14. **Logging & Monitoring**  
    **Bounded Context:** *Observability Context*
    - Centralized logging, monitoring, metrics, and alerts.

15. **Reporting & Analytics Infrastructure**  
    **Bounded Context:** *Reporting Context*
    - BI dashboards, performance reports, data exports.

16. **Integration Infrastructure**  
    **Bounded Context:** *Integration Context*
    - APIs, messaging, connectors to external systems.

17. **CRM / Customer Data Management**  
    **Bounded Context:** *CRM Context*
    - Customer records, contact info, interaction history.

18. **Payment Processing Gateway**  
    **Bounded Context:** *Payment Context*
    - Payment initiation, confirmation, reconciliation.

---

## ✅ Key DDD Takeaways

- Each **subdomain maps to a bounded context**.
- **Core bounded contexts** require rich, evolving domain models (strategic investment).
- **Supporting bounded contexts** encapsulate domain-specific logic but are not competitive differentiators.
- **Generic bounded contexts** can often be implemented via off-the-shelf solutions.
- Context boundaries guide **microservice design**, ensuring each service has a clear, consistent language.
