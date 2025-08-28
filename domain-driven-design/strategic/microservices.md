# Loan Origination System – Recommended Microservice Split

This document suggests a microservice split aligned with **DDD subdomains and bounded contexts** for a Loan Origination System (LOS).

---

## 🔑 Core Microservices

- **loan-application-service**
  Handles borrower loan applications, workflow, and state transitions.
  *Domain terms:* Loan Application, Applicant, Status, Workflow Step.

- **credit-decisioning-service**
  Integrates with credit bureaus, applies scoring models, calculates risk.
  *Domain terms:* Credit Score, Risk Assessment, Decision Outcome.

- **underwriting-service**
  Applies lending policies and eligibility rules for approval/decline.
  *Domain terms:* Underwriter, Eligibility Criteria, Policy Rule.

- **loan-offer-service**
  Generates personalized loan offers with terms, interest rates, and repayment schedules.
  *Domain terms:* Loan Offer, Interest Rate, APR, Repayment Schedule.

---

## 🤝 Supporting Microservices

- **loan-product-service**
  Manages product catalog and loan product exploration.
  *Domain terms:* Loan Product, Rate Table, Eligibility Information.

- **document-service**
  Handles document upload, storage, and verification.
  *Domain terms:* Document, Document Type, Verification, Fraud Check.

- **kyc-service**
  Manages AML/KYC checks and identity verification.
  *Domain terms:* KYC, Identity Document, AML Check, Verification Result.

- **compliance-service**
  Provides audit trail, decision justification, and regulatory reporting.
  *Domain terms:* Audit Trail, Retention Policy, Decision Justification.

- **notification-service**
  Sends email/SMS/push notifications to applicants.
  *Domain terms:* Notification, Status Update, Reminder.

- **funding-service**
  Manages loan disbursement and payout confirmation.
  *Domain terms:* Disbursement, Funding Instruction, Settlement Account.

---

## ⚙️ Generic/Shared Infrastructure

- **auth-service**
  Authentication and authorization (SSO, OAuth2, Keycloak).
  *Domain terms:* User, Role, Access Token.

- **user-service**
  User management and role assignments.
  *Domain terms:* User Profile, Group, Account Status.

- **logging-service**
  Centralized logging and monitoring.
  *Domain terms:* Log Entry, Metric, Alert.

- **reporting-service**
  Reporting, dashboards, and analytics.
  *Domain terms:* Report, Dashboard, KPI, Data Export.

- **integration-service**
  Provides API gateway, messaging, and external system connectors.
  *Domain terms:* API, Event, Message Queue, Connector.

---

## ✅ Notes

- Core microservices require **rich domain models** with aggregates, entities, and events.
- Supporting microservices may use **simpler models** or external libraries.
- Generic microservices should rely on **off-the-shelf solutions** when possible.
- Cross-cutting concerns (auth, logging, reporting) should not duplicate business logic.