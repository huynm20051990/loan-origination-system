# 📌 Mortgage Loan Origination System - Tactical Design Document (DDD + Layered Architecture)

## **1. Overview**
This document defines the **tactical design** of the Mortgage Loan Origination System, applying **Domain-Driven Design (DDD)** principles. It focuses on **aggregates, entities, value objects, repositories, services, and domain events** within each bounded context.

---

## **2. Bounded Contexts & Aggregates**

### **2.1 Core Domain**

| Bounded Context | Aggregate | Description |
|-----------------|-----------|-------------|
| Loan Application Context | LoanApplication | Represents a borrower's application for a loan. Contains references to Borrower, Loan Product, Status, and Events. |
| Underwriting Context | UnderwritingCase | Encapsulates all underwriting logic, verification results, risk scores, and conditional approvals. |
| Loan Decision Context | LoanDecision | Contains the final decision, DTI/LTV calculations, and approval/rejection status. |

---

### **2.2 Supporting Domain**

| Bounded Context | Aggregate | Description |
|-----------------|-----------|-------------|
| Borrower Context | Borrower | Stores borrower identity, contact info, KYC/AML verification results. |
| Financial Verification Context | IncomeVerification, AssetVerification | Each verifies and stores income, employment, and asset data. |
| Property Context | Property | Stores property details, appraisal reports, and title verification. |
| Notification Context | Notification | Tracks notifications sent to borrowers (email, SMS). |
| Process Manager | LoanProcess | Coordinates workflow, tracks events for loan application, and triggers services. |
| Credit Context | CreditReport | Stores credit report details pulled from credit bureaus. |
| Investor Context | InvestorLoan | Stores secondary market sale details and reporting to investors. |
| Escrow Context | EscrowAccount | Tracks fund disbursement, payments, and closing details. |
| Document Context | LoanDocument | Stores loan-related documents and versioning info. |

---

### **2.3 Generic Domain**

| Bounded Context | Aggregate | Description |
|-----------------|-----------|-------------|
| Authentication Context | User, Role, AccessToken | Manages authentication, authorization, and roles. |
| User Management Context | UserProfile, Group | Stores user account information and role assignments. |
| Logging & Monitoring | LogEntry, Metric, Alert | Centralized logging and metrics. |
| Reporting Context | Report, Dashboard | Provides dashboards, KPIs, and analytics. |
| Integration Context | API, Event, Connector | Handles external integrations and messaging infrastructure. |

---

## **3. Entities and Value Objects**

### **3.1 Borrower Context**
- **Entity:** Borrower (borrowerId)
- **Value Objects:** Name, Address, Email, PhoneNumber, SSN
- **Invariants:** SSN must be unique, Email must be valid format.

### **3.2 Loan Application Context**
- **Entity:** LoanApplication (applicationId)
- **Value Objects:** LoanProduct, ApplicationStatus, ApplicationDate
- **Invariants:** A LoanApplication must be associated with exactly one Borrower.

### **3.3 Underwriting Context**
- **Entity:** UnderwritingCase (caseId)
- **Value Objects:** DTI, LTV, RiskScore, VerificationResults
- **Invariants:** Must have completed verification results before final decision.

### **3.4 Loan Decision Context**
- **Entity:** LoanDecision (decisionId)
- **Value Objects:** DecisionStatus (Approved/Rejected/Conditional), DecisionDate
- **Invariants:** Decision must reference a LoanApplication and UnderwritingCase.

---

## **4. Domain Services**

| Service | Responsibility |
|---------|----------------|
| LoanApplicationService | Create LoanApplication, link to Borrower, emit `LoanApplicationSubmitted`. |
| BorrowerService | Manage borrower creation and updates, emit `BorrowerCreated`. |
| KYCService | Verify identity and AML checks. |
| IncomeService | Verify income and employment details. |
| AssetsService | Verify borrower assets. |
| CreditService | Pull and standardize credit reports. |
| PropertyService | Validate property appraisal and title. |
| UnderwritingService | Apply rules, calculate risk scores, generate conditional approval. |
| LoanDecisionService | Apply final approval/rejection logic. |
| NotificationService | Send emails/SMS notifications. |
| ProcessManagerService | Orchestrate workflow and track event-based state. |

---

## **5. Domain Events**

| Event | Emitted By | Description |
|-------|------------|-------------|
| BorrowerCreated | BorrowerService | Signals new borrower creation. |
| LoanApplicationSubmitted | LoanApplicationService | Loan application submitted, triggers verification. |
| KycVerified / KycFailed | KYCService | Identity verification result. |
| IncomeVerified / IncomeFailed | IncomeService | Income verification result. |
| AssetsVerified / AssetsFailed | AssetsService | Asset verification result. |
| CreditPulled / CreditFailed | CreditService | Credit report pulled. |
| PropertyVerified / PropertyFailed | PropertyService | Property verification result. |
| UnderwritingCompleted | UnderwritingService | Underwriting finished, includes risk findings. |
| LoanDecisionMade | LoanDecisionService | Final approval or rejection decision. |
| NotificationSent | NotificationService | Notification delivered to borrower. |

---

## **6. Repositories**

| Aggregate | Repository Interface |
|-----------|--------------------|
| Borrower | `BorrowerRepository` - save, findById, findBySSN |
| LoanApplication | `LoanApplicationRepository` - save, findById, findByBorrowerId |
| UnderwritingCase | `UnderwritingRepository` - save, findByLoanApplicationId |
| LoanDecision | `LoanDecisionRepository` - save, findByLoanApplicationId |
| Property | `PropertyRepository` - save, findByLoanApplicationId |
| CreditReport | `CreditRepository` - save, findByBorrowerId |
| LoanDocument | `DocumentRepository` - save, findByLoanApplicationId |
| EscrowAccount | `EscrowRepository` - save, findByLoanApplicationId |

---

## **7. Aggregates & Invariants Summary**

- **Borrower**: SSN unique, email valid.
- **LoanApplication**: Linked to one Borrower. Status transitions are controlled.
- **UnderwritingCase**: All verifications must pass or fail explicitly.
- **LoanDecision**: Must reference a LoanApplication and UnderwritingCase.
- **Notification**: Only sent once per event type.

---

## **8. Tactical Design Notes**

- Each **service owns its aggregate and repository**.
- Communication between services is **event-driven** for decoupling and asynchronous processing.
- Synchronous APIs used only for immediate lookup or queries.
- **Process Manager** orchestrates complex workflows without violating bounded context boundaries.
- The system supports **event sourcing** and **auditability**, ensuring full traceability of loan applications.

---

## **9. Loan Product & Related Services (Layered Architecture + Active Record)**

### **9.1 Overview**
The Loan Product services allow users to **browse, review, and rate loan products**. They use a **layered architecture** (Presentation → Service → Repository/Model) and **Active Record** pattern, where each model object handles persistence directly.

---

### **9.2 Bounded Contexts & Aggregates**

| Bounded Context | Aggregate | Description |
|-----------------|-----------|-------------|
| Loan Product Context | LoanProduct | Represents a loan product (e.g., mortgage, personal loan) with terms, interest rates, eligibility. |
| Loan Product Composite Context | LoanProductComposite | Aggregates multiple LoanProduct entities, including ratings, reviews, and metadata for front-end consumption. |
| Product Rating Context | ProductRating | Captures borrower ratings (stars, feedback) for a LoanProduct. |
| Product Review Context | ProductReview | Stores textual reviews and feedback for a LoanProduct by borrowers. |

---

### **9.3 Layered Architecture**

1. **Presentation Layer**
    - REST API controllers or GraphQL resolvers to handle incoming requests.

2. **Service Layer**
    - Encapsulates business logic for managing products, ratings, and reviews.

3. **Model / Active Record Layer**
    - Each entity directly handles persistence (CRUD) via Active Record.

---

### **9.4 Entities / Active Records**

| Entity | Fields | Persistence Methods (Active Record) |
|--------|--------|------------------------------------|
| LoanProduct | productId, name, interestRate, term, eligibilityCriteria | save(), update(), findById(), findAll() |
| ProductRating | ratingId, productId, borrowerId, stars, createdAt | save(), update(), findByProductId() |
| ProductReview | reviewId, productId, borrowerId, content, createdAt | save(), update(), findByProductId() |
| LoanProductComposite | compositeId, product:LoanProduct, ratings:[ProductRating], reviews:[ProductReview] | No direct persistence; composed at runtime |

---

### **9.5 Domain Services**

| Service | Responsibility |
|---------|----------------|
| LoanProductService | CRUD operations on LoanProduct entities; apply validation and business rules. |
| ProductRatingService | Manage ratings; calculate average rating per product. |
| ProductReviewService | Manage textual reviews and feedback for each product. |
| LoanProductCompositeService | Aggregate LoanProduct, Ratings, Reviews for UI/API consumption. |

---

### **9.6 Repositories / Persistence**

- Using **Active Record**, each entity handles its own persistence; separate repositories are optional.

---

### **9.7 Events (Optional for Integration with Core Microservices)**

| Event | Emitted By | Description |
|-------|------------|-------------|
| LoanProductCreated / Updated | LoanProductService | Signals a new or updated loan product. |
| ProductRated | ProductRatingService | Signals a new rating has been submitted. |
| ProductReviewed | ProductReviewService | Signals a new textual review has been submitted. |

---

### **9.8 Notes on Tactical Design**

- Layered design keeps **presentation, business logic, and persistence separate**.
- Active Record simplifies CRUD for product-related entities.
- `LoanProductComposite` aggregates multiple models **without direct persistence**, optimizing UI/API calls.
- Optional **event publishing** allows core Loan Application services to react (e.g., recommend high-rated products to borrowers).
- These services can later be **decoupled as microservices** if scaling requires.  
