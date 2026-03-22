---
name: ratio-calculator
description: Calculates financial ratios (DTI/LTV) using local Python scripts.
---
# Skill: Financial Ratio Calculator
Ensures 100% mathematical accuracy for lending policy formulas.

**Logic:**
- Payment = Loan Amount * 0.007 (0.7% Rule)
- LTV = (Loan Amount / Property Value) * 100
- Income Multiple = Monthly Income / Payment

**Usage:**
Run `python3 scripts/calculate_ratios.py --loan {amount} --value {prop_value} --income {monthly_income}`