# Mortgage Calculations Reference

## 1. Formulas / Methods

| # | Calculation | Formula / Method | Inputs | Output / Purpose |
|---|-------------|-----------------|-------|----------------|
| 1 | Monthly Principal & Interest (P&I) | M = P * [r(1+r)^n] / [(1+r)^n - 1] | P = loan principal, r = monthly interest rate, n = total payments | Monthly payment for principal + interest |
| 2 | Property Taxes | Annual property tax ÷ 12 | Annual property tax | Monthly property tax portion |
| 3 | Homeowners Insurance | Annual insurance ÷ 12 | Annual insurance premium | Monthly insurance portion |
| 4 | Mortgage Insurance (PMI/FHA) | Usually a % of loan ÷ 12 | Loan amount, insurance rate | Monthly mortgage insurance |
| 5 | Total Monthly Payment (PITI) | PITI = P&I + Taxes + Insurance (+ PMI if any) | P&I, Taxes, Insurance, PMI | Total monthly mortgage payment |
| 6 | Front-End DTI | Front-End DTI = PITI ÷ Gross Monthly Income × 100% | PITI, Borrower gross monthly income | Housing debt ratio for eligibility |
| 7 | Back-End DTI | Back-End DTI = (PITI + Other Monthly Debt) ÷ Gross Monthly Income × 100% | PITI, Other monthly debt, income | Total debt ratio for risk assessment |
| 8 | Loan-to-Value (LTV) | LTV = Loan Amount ÷ Property Value × 100% | Loan amount, property value | Risk level and mortgage insurance requirement |
| 9 | Simple Interest | I = P × r × t | Principal, annual interest rate, time (years) | Interest for short-term loans |
| 10 | Amortized Interest | Derived from amortization schedule | P, r, n | Interest portion decreases over time |
| 11 | Prepayment Remaining Balance | Calculate using amortization schedule after extra payment | Current principal, extra payment | New loan balance after prepayment |
| 12 | Interest Savings from Prepayment | Interest Saved = Original Interest - Interest Paid with Prepayment | Original amortization schedule, prepayment amount | Potential interest reduction |
| 13 | Closing Costs | Sum of fees | Fees, prepaid items, credits | Total cash required to close |
| 14 | Total Cash to Close | Down Payment + Closing Costs + Prepaids - Credits | Down payment, closing costs, credits | Total cash borrower must provide |
| 15 | Yield Spread Premium (YSP) | Sale price - Par value | Loan sale price, par value | Profit from selling loan above par |
| 16 | Servicing Fee | UPB × Servicing % per month | Unpaid principal balance, servicing rate | Monthly fee for loan servicing |
| 17 | MBS Valuation | Discounted cash flows | Loan payments, discount rate | Market value of securitized loans |

---

## 2. Examples

| # | Example Calculation | Inputs | Output |
|---|------------------|--------|--------|
| 1 | Monthly P&I | P=$350,000, r=6.25%/12, n=360 | M ≈ $2,150.61 |
| 2 | Property Taxes | Annual tax=$4,800 | $400/month |
| 3 | Homeowners Insurance | Annual premium=$1,200 | $100/month |
| 4 | Mortgage Insurance (PMI) | Loan=$350,000, rate=0.5% | $145.83/month |
| 5 | Total Monthly Payment (PITI) | P&I=$2,150.61, Taxes=$400, Insurance=$100, PMI=$145.83 | $2,796.44 |
| 6 | Front-End DTI | PITI=$2,796.44, income=$8,000 | 34.95% |
| 7 | Back-End DTI | PITI=$2,796.44, Other debt=$500, income=$8,000 | 41.2% |
| 8 | LTV | Loan=$350,000, Property=$400,000 | 87.5% |
| 9 | Simple Interest | P=$350,000, r=6.25%, t=1yr | $21,875 |
| 10 | Amortized Interest | Month 1: P=$350,000, r=6.25%, n=360 | $1,822.92 interest |
| 11 | Prepayment Remaining Balance | Current principal=$340,000, extra=$10,000 | $330,000 |
| 12 | Interest Savings from Prepayment | Original interest=$375,000, prepayment interest=$360,000 | $15,000 saved |
| 13 | Closing Costs | Origination=$3,500, Appraisal=$500, Title=$1,200, Recording=$200, Prepaids=$1,500, Credits=$0 | $6,900 |
| 14 | Total Cash to Close | Down=$50,000, Closing=$6,900, Prepaids=$1,500, Credits=$0 | $58,400 |
| 15 | YSP | Sale=$352,000, Par=$350,000 | $2,000 profit |
| 16 | Servicing Fee | UPB=$350,000, rate=0.25% | $875/month |
| 17 | MBS Valuation | Monthly payment=$2,796.44, discount rate=5% | PV ≈ $335,000 |
