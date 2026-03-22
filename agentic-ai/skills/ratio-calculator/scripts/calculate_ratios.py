import sys
import json
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('--loan', type=float)
parser.add_argument('--value', type=float)
parser.add_argument('--income', type=float)
args = parser.parse_args()

payment = args.loan * 0.007
ltv = (args.loan / args.value) * 100
multiple = args.income / payment

result = {
    "monthly_payment": round(payment, 2),
    "ltv_percent": round(ltv, 2),
    "income_multiple": round(multiple, 2),
    "meets_ltv": ltv <= 90,
    "meets_dti": multiple >= 3
}

print(json.dumps(result))