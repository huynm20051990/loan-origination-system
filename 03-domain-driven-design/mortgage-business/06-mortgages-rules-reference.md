# Loan Eligibility Rules Reference

| # | Criteria | Rule / Threshold | Example / Notes |
|---|---------|----------------|----------------|
| 1 | Credit Score | Minimum required per loan type | Conventional ≥ 620, FHA ≥ 580, VA ≥ 620 |
| 2 | Front-End DTI | ≤ 35% | PITI ÷ Gross Monthly Income ≤ 35% |
| 3 | Back-End DTI | ≤ 45% | (PITI + Other Debt) ÷ Gross Monthly Income ≤ 45% |
| 4 | LTV (Loan-to-Value) | Maximum allowed per loan type | Conventional ≤ 80%, FHA ≤ 96.5%, VA ≤ 100% |
| 5 | Income Verification | Stable, verifiable income | 2 years employment, pay stubs, W-2s, VOE |
| 6 | Asset / Reserves | Sufficient for down payment & reserves | Down payment ≥ 20% for conventional w/o PMI, 2 months mortgage reserves |
| 7 | Property Requirements | Meets appraisal & eligibility | Appraised value ≥ loan amount, allowed property type (single-family, condo, townhouse) |
| 8 | Employment History | Stable employment | ≥ 2 years current employer, no significant gaps |
| 9 | Existing Debt | Limits on debt burden | Revolving debt ≤ 30% of monthly income, no bankruptcy < 2 yrs, no foreclosure < 5 yrs |
| 10 | Compliance & Legal | Must pass legal checks | AML checks, no legal judgments affecting borrower |
| 11 | AUS Automated Rules | Pre-approved if combined criteria met | Fannie Mae DU / Freddie Mac LP approval based on credit, DTI, LTV, assets |

---

## Example Scenarios Satisfying Loan Eligibility

| # | Borrower Profile | Loan Type | Eligibility Outcome | Notes |
|---|----------------|----------|------------------|-------|
| 1 | Credit Score: 700, Income: $8,000/mo, PITI: $2,500, Other Debt: $300, LTV: 75%, Assets: $50,000, Employment: 3 yrs current | Conventional | Approved | All DTI, LTV, credit, and asset requirements satisfied |
| 2 | Credit Score: 620, Income: $5,500/mo, PITI: $1,800, Other Debt: $400, LTV: 96%, Assets: $5,000, Employment: 2 yrs current | FHA | Approved | Meets FHA minimums despite low reserves |
| 3 | Credit Score: 680, Income: $10,000/mo, PITI: $3,200, Other Debt: $1,000, LTV: 80%, Assets: $100,000, Employment: 5 yrs | Conventional | Approved | Back-end DTI = 42% < 45%, all rules satisfied |
| 4 | Credit Score: 640, Income: $7,000/mo, PITI: $2,000, Other Debt: $1,500, LTV: 100%, Assets: $20,000, Employment: 4 yrs | VA | Approved | VA allows 100% LTV; other ratios acceptable |
| 5 | Credit Score: 590, Income: $6,000/mo, PITI: $1,500, Other Debt: $200, LTV: 95%, Assets: $10,000, Employment: 3 yrs | FHA | Approved | Meets FHA credit threshold and LTV limits |

## Loan Eligibility Rule Checker Example (Java)

```java
// Rule checker
public class LoanEligibilityChecker {

    public boolean isEligible(LoanApplication application) {
        return checkCreditScore(application) &&
               checkDTI(application) &&
               checkLTV(application) &&
               checkAssets(application) &&
               checkEmployment(application);
    }

    private boolean checkCreditScore(LoanApplication app) {
        int score = app.getBorrower().getCreditScore();
        switch (app.getLoanType()) {
            case "Conventional": return score >= 620;
            case "FHA": return score >= 580;
            case "VA": return score >= 620;
            default: return false;
        }
    }

    private boolean checkDTI(LoanApplication app) {
        double frontEndDTI = app.getMonthlyPITI() / app.getBorrower().getMonthlyIncome();
        double backEndDTI = (app.getMonthlyPITI() + app.getBorrower().getMonthlyDebt()) 
                            / app.getBorrower().getMonthlyIncome();
        return frontEndDTI <= 0.35 && backEndDTI <= 0.45;
    }

    private boolean checkLTV(LoanApplication app) {
        double ltv = app.getLoanAmount() / app.getPropertyValue();
        switch (app.getLoanType()) {
            case "Conventional": return ltv <= 0.80;
            case "FHA": return ltv <= 0.965;
            case "VA": return ltv <= 1.0;
            default: return false;
        }
    }

    private boolean checkAssets(LoanApplication app) {
        // Example: minimum reserves of 2 months PITI for conventional loans
        if ("Conventional".equals(app.getLoanType())) {
            return app.getBorrower().getAssets() >= 2 * app.getMonthlyPITI();
        }
        return true;
    }

    private boolean checkEmployment(LoanApplication app) {
        return app.getBorrower().getEmploymentYears() >= 2;
    }
}
