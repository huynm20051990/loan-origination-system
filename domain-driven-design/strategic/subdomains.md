# Loan Origination System – Domain-Driven Design (DDD)

This document classifies the subdomains of a Loan Origination System (LOS) into **Core**, **Supporting**, and **Generic** subdomains following DDD principles.  
It also provides a **Context Map** and suggested **Microservice Split** for practical implementation.

---

## 📌 Subdomain Classification

| Subdomain Type   | Subdomains |
|------------------|------------|
| **Core**         | - Loan Application & Workflow Management <br> - Credit Decisioning & Risk Assessment <br> - Underwriting Rules & Policy Enforcement <br> - Loan Offer & Pricing Engine |
| **Supporting**   | - Loan Product Exploration <br> - Document Management <br> - Customer Communication & Notifications <br> - KYC / Identity Verification <br> - Compliance & Audit Logging <br> - Loan Funding & Disbursement <br> - Workflow Orchestration / Process Manager |
| **Generic**      | - Authentication & Authorization <br> - User Management / Roles & Permissions <br> - Logging & Monitoring <br> - Reporting & Analytics Infrastructure <br> - Integration Infrastructure (APIs, Kafka, ESB, etc.) <br> - CRM / Customer Data Management <br> - Payment Processing Gateway |