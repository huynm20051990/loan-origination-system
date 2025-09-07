# 📌 Requirement: Loan Origination System

## **Description**
The Loan Origination System allows users to browse loan products (e.g., personal loans, mortgages, auto loans) with details such as interest rates, terms, and eligibility criteria.

Users can then submit a loan application by providing identity and contact information. Supporting financial documents are pulled automatically through third-party services. Credit checks and verifications are performed without manual intervention.

Once all verifications and underwriting are complete, the system makes a decision automatically and notifies the borrower via email. Borrowers can log in at any time to track their application status.

---

## **Automation Process Flow**

### **1. User Action**
- User clicks **Apply** on a loan product.
- Redirected to **Apply for Loan** page.
- Inputs **ID, phone, email** (loan product pre-filled).
- Clicks **Submit**.

---

### **2. Loan Application Service (Entry Point)**
- Handles `SubmitLoanApplicationCommand`.

**Step 1: Borrower Creation**
- Sends `CreateBorrowerCommand` → **Borrower Service**.
- Borrower Service persists borrower details.
- Emits `BorrowerCreated(borrowerId)`.

**Step 2: Loan Application Creation**
- On receiving `BorrowerCreated`, creates a **LoanApplication aggregate** linked to `borrowerId`.
- Emits `LoanApplicationSubmitted`.

---

### **3. LoanApplicationProcessManager (Workflow Orchestrator)**
- Subscribes to `LoanApplicationSubmitted`.
- Triggers verification commands:
    - `VerifyKycCommand` → KYC Service
    - `VerifyIncomeCommand` → Income Service
    - `VerifyAssetsCommand` → Assets Service
    - `PullCreditCommand` → Credit Service
    - `VerifyPropertyCommand` → Property Service

---

### **4. Data Aggregation Services**
Each service validates borrower data and emits results:

- **KYC Service** → `KycVerified` / `KycFailed`
- **Income Service** → `IncomeVerified` / `IncomeFailed`
- **Assets Service** → `AssetsVerified` / `AssetsFailed`
- **Credit Service** → `CreditPulled` / `CreditFailed`
- **Property Service** → `PropertyVerified` / `PropertyFailed`

All events include `borrowerId` and `loanApplicationId`.

---

### **5. LoanApplicationProcessManager (Verification Results)**
- Collects all verification events for the `loanApplicationId`.
- If **all succeed** → sends `RunUnderwritingCommand` → Underwriting Engine.
- If **any fail** → sends `RejectLoanApplicationCommand`.

---

### **6. Underwriting Engine Service**
- Applies rules and calculations:
    - Debt-to-Income (DTI)
    - Loan-to-Value (LTV)
    - Risk scoring
- Emits `UnderwritingCompleted(loanApplicationId, findings)`.

---

### **7. LoanApplicationProcessManager**
- Receives underwriting results.
- Issues `MakeLoanDecisionCommand` → Loan Decision Engine.

---

### **8. Loan Decision Engine Service**
- Finalizes decision (Approve / Deny).
- Emits `LoanDecisionMade(loanApplicationId, status)`.

---

### **9. LoanApplicationProcessManager**
- Updates loan application status.
- Sends `SendNotificationCommand` → Notification Service.

---

### **10. Notification Service**
- Retrieves borrower contact info (from Borrower Service or event payload).
- Sends **Approval / Rejection Email**.
- Emits `NotificationSent`.

---
