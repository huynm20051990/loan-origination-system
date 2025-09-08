# Mortgage Analysis Process

## Steps

1. **Loan Application Intake**
    - Borrower submits loan application with personal, financial, and property details.
    - Collect supporting documents: income, assets, debts, employment verification.

2. **Credit Analysis**
    - Pull credit report from major bureaus (Equifax, Experian, TransUnion).
    - Evaluate credit score, history, and debt obligations.
    - Identify risk factors (late payments, bankruptcies, foreclosures).

3. **Debt-to-Income (DTI) Calculation**
    - Calculate front-end DTI: PITI ÷ Gross Income.
    - Calculate back-end DTI: (PITI + other debts) ÷ Gross Income.
    - Compare against lender or investor thresholds.

4. **Loan-to-Value (LTV) Assessment**
    - Determine the property’s appraised value.
    - LTV = Loan Amount ÷ Property Value.
    - Verify against maximum allowed by loan type.

5. **Income and Asset Analysis**
    - Verify borrower’s income (pay stubs, W-2s, tax returns, VOE).
    - Review assets for down payment and reserves (bank statements, investments).

6. **Property Analysis**
    - Review property details (type, location, occupancy).
    - Assess property condition and market comparables.

7. **Risk Assessment**
    - Combine credit, DTI, LTV, income, assets, and property analysis.
    - Identify potential underwriting conditions.

---

## Sample Java Code

```java
public class MortgageAnalysis {

    public double calculateFrontEndDTI(double piti, double income) {
        return piti / income;
    }

    public double calculateBackEndDTI(double piti, double otherDebt, double income) {
        return (piti + otherDebt) / income;
    }

    public double calculateLTV(double loanAmount, double propertyValue) {
        return loanAmount / propertyValue;
    }

    public boolean isCreditScoreAcceptable(int creditScore, String loanType) {
        switch (loanType) {
            case "Conventional": return creditScore >= 620;
            case "FHA": return creditScore >= 580;
            case "VA": return creditScore >= 620;
            default: return false;
        }
    }

    public boolean isEligible(double frontDTI, double backDTI, double ltv) {
        return frontDTI <= 0.35 && backDTI <= 0.45 && ltv <= 0.80;
    }
}
