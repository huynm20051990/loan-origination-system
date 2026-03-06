# Loan Application Lifecycle – Core Services

To move a loan application from **submission** to **closing**, the architecture should be composed of the following specialized services, each responsible for a clear business capability.

## Core Domain Services

### Document Service
Responsible for uploading, storing, and managing borrower documents such as:
- Pay stubs
- Tax returns
- Government-issued IDs

### Credit Service
Interfaces with external credit bureaus to retrieve credit data, including:
- Experian
- TransUnion
- Equifax

### Underwriting Service
Acts as the rules engine to:
- Assess borrower risk
- Determine loan eligibility
- Apply underwriting policies and decision logic

### Pricing Service
Calculates loan terms based on assessed risk, including:
- Interest rates
- Monthly payment amounts

### Property Valuation Service
Manages property valuation processes such as:
- Appraisals
- Automated Valuation Models (AVMs)

### Verification Service
Handles verification workflows, including:
- Employment verification
- Income verification

### Funding Service
Manages the final stage of the loan lifecycle:
- Loan approval to disbursement
- Release of funds to the borrower or escrow

### Notification Service
Handles all borrower and internal communications, such as:
- Email notifications
- SMS messages
- Loan status updates

---

## Event-Driven Communication Model

The system uses an **event-driven architecture** with a **two-topic messaging model** to coordinate work between services while keeping them loosely coupled.

### The 2-Topic Architecture

#### `loan.commands` — *The “To-Do” List*
- **Source:**
    - `Application-Service` (via its **Outbox** table)
- **Consumers:**
    - All worker services (Document, Credit, Underwriting, Pricing, etc.)
- **Purpose:**
    - Instructs a specific service to start a task
    - Represents commands such as *Request Credit Check*, *Start Underwriting*, or *Upload Documents*

#### `loan.results` — *The “Done” List*
- **Source:**
    - All worker services (via their respective **Outbox** tables)
- **Consumers:**
    - `Application-Service` (acting as the **Orchestrator**)
- **Purpose:**
    - Reports task completion back to the system “brain”
    - Publishes results (e.g., FICO scores, underwriting decisions) or failures

---

## Orchestration Responsibility

The **Application-Service** acts as the central orchestrator:
- Emits commands to `loan.commands`
- Listens to results on `loan.results`
- Tracks loan application state transitions
- Decides next steps in the workflow based on returned outcomes
