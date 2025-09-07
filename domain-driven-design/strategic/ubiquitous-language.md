# 📌 Mortgage Business Process - Ubiquitous Language

In Domain-Driven Design (DDD), a **Ubiquitous Language** is a common vocabulary used by developers, domain experts, and stakeholders to ensure clear communication. The terms are aligned with the **Core, Supporting, and Generic Subdomains**.

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

## **Supporting Subdomain Terms**
| **Term** | **Definition** |
|----------|----------------|
| Borrower Management | Service managing borrower personal information, identity verification, and contact info. |
| KYC (Know Your Customer) | Process for verifying borrower identity and performing AML checks. |
| Income Verification | Process of validating borrower’s income via pay stubs, W-2s, tax returns. |
| Asset Verification | Process of validating borrower’s assets (bank accounts, investments). |
| Property Verification | Process of verifying property ownership, appraisal, and title details. |
| Notification | Sending updates or loan decision communications to the borrower. |
| Process Manager | Component orchestrating loan application workflow and coordinating services. |

---

## **Generic Subdomain Terms**
| **Term** | **Definition** |
|----------|----------------|
| Credit Report | Report containing borrower credit history and credit score from bureaus. |
| Credit Bureau | Organization providing borrower credit data (Experian, Equifax, TransUnion). |
| Secondary Market | Investors purchasing loans from the lender (Fannie Mae, Freddie Mac, Ginnie Mae, private investors). |
| Escrow | Account managed by a third party to hold funds during closing. |
| Document Storage | System or repository for storing borrower and loan-related documents. |
| Loan Boarding | Process of entering an approved loan into a servicing system for management. |

---

### **Notes**
- All developers, analysts, and domain experts should use these terms consistently.
- Each term is mapped to the relevant subdomain to clarify responsibility and ownership.
- The **Core Domain terms** define the unique strategic differentiators of the mortgage process.
