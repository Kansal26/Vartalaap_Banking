#!/usr/bin/env python3
"""
Synthetic data generator for the Vartalaap Banking analytics project.

Mirrors the real Vartalaap schema (users + five scheme-application tables, unified into one
long table) and deliberately injects (a) data-quality problems to clean and (b) known business
patterns to discover. Everything is SYNTHETIC: names are random combinations, Aadhaar is reduced
to 4 random digits, and there are no phone numbers or account numbers.

Run:  python generate_data.py            (writes data/raw/*.csv, data/ground_truth/*, docs/*)
"""
import json
import re
from datetime import date, timedelta
from pathlib import Path

import numpy as np
import pandas as pd

SEED = 42
N_APPS = 12_000
START, END = date(2025, 10, 1), date(2026, 9, 30)
SNAPSHOT = date(2026, 10, 1)          # "extract date" of the dataset
ROOT = Path(__file__).parent
rng = np.random.default_rng(SEED)

# --------------------------------------------------------------------------- reference data
# name, region, volume weight, P(at least one required document missing)
BRANCHES = [
    ("Meerut City", "Meerut Region", 1.6, 0.05), ("Meerut Cantt", "Meerut Region", 1.0, 0.06),
    ("Manzol", "Meerut Region", 0.9, 0.30),        ("Modipuram", "Meerut Region", 0.8, 0.06),
    ("Mawana", "Meerut Region", 0.6, 0.08),        ("Hapur", "Meerut Region", 1.0, 0.06),
    ("Jaipur MI Road", "Jaipur Region", 2.3, 0.17), ("Jaipur Vaishali Nagar", "Jaipur Region", 1.2, 0.05),
    ("Jaipur Mansarovar", "Jaipur Region", 1.1, 0.06), ("Ajmer Station Road", "Jaipur Region", 0.9, 0.07),
    ("Kota Station Area", "Jaipur Region", 0.8, 0.06), ("Alwar Scheme 1", "Jaipur Region", 0.7, 0.05),
    ("Delhi Karol Bagh", "Delhi Region", 1.4, 0.06), ("Delhi Lajpat Nagar", "Delhi Region", 1.2, 0.05),
    ("Delhi Rohini", "Delhi Region", 1.0, 0.06),   ("Gurugram Sector 14", "Delhi Region", 0.5, 0.02),
    ("Faridabad NIT", "Delhi Region", 0.8, 0.07),  ("Ghaziabad Nehru Nagar", "NCR East Region", 1.1, 0.06),
    ("Noida Sector 18", "NCR East Region", 1.3, 0.05), ("Greater Noida Alpha", "NCR East Region", 0.7, 0.06),
]
REGION_APPROVER = {"Meerut Region": "2001", "Jaipur Region": "2002", "Delhi Region": "2003", "NCR East Region": "2004"}
APPROVER_SPEED = {"2001": 1.0, "2002": 1.2, "2003": 2.6, "2004": 0.8}      # multiplies median decision time
APPROVER_STALE_P = {"2001": 0.01, "2002": 0.01, "2003": 0.09, "2004": 0.01}  # P(application never decided)

SCHEMES = ["APY", "PMJJBY", "PMSBY", "KVP", "PMMY"]
SCHEME_MIX = [0.30, 0.22, 0.22, 0.12, 0.14]
AGE_PARAMS = {"APY": (29, 6, 18, 40), "PMJJBY": (36, 9, 18, 50), "PMSBY": (40, 12, 18, 70),
              "KVP": (45, 14, 18, 80), "PMMY": (38, 9, 21, 65)}   # mean, sd, min, max-eligible
INELIGIBLE_P = {"APY": 0.012, "PMJJBY": 0.010, "PMSBY": 0.008, "KVP": 0.0, "PMMY": 0.006}

# APY required monthly contribution by (age bracket, pension tier), copied from the repo's ApyService table
_APY = {18: [42, 84, 126, 168, 210], 20: [50, 100, 150, 198, 248], 25: [76, 151, 226, 301, 376],
        30: [116, 231, 347, 462, 577], 35: [181, 362, 543, 722, 902], 40: [291, 582, 873, 1154, 1454]}
TIERS = [1000, 2000, 3000, 4000, 5000]
APY_REQ = {(a, t): v for a, vals in _APY.items() for t, v in zip(TIERS, vals)}
def apy_required(age, tier):
    bracket = max(a for a in _APY if a <= max(age, 18))
    return APY_REQ[(bracket, tier)]

FIRST = ["Aarav", "Vivaan", "Aditya", "Rohan", "Karan", "Neha", "Priya", "Anjali", "Sneha", "Pooja", "Rahul", "Amit",
         "Suresh", "Ramesh", "Sunita", "Geeta", "Kavita", "Manoj", "Deepak", "Vikas", "Meena", "Ritu", "Sanjay",
         "Anil", "Pankaj", "Nisha", "Arjun", "Ishaan", "Divya", "Mohit"]
LAST = ["Sharma", "Verma", "Gupta", "Singh", "Kumar", "Yadav", "Jain", "Agarwal", "Mishra", "Tiwari", "Saini", "Chauhan",
        "Rathore", "Meena", "Joshi", "Bansal", "Goel", "Kansal", "Malik", "Khan", "Ansari", "Pandey", "Bhardwaj", "Soni"]

REMARKS = {
    "missing_documents": ["Aadhaar copy missing", "PAN not attached", "Cancelled cheque missing", "Documents incomplete",
                          "docs missing pls resubmit", "KYC docs not uploaded"],
    "apy_contribution_mismatch": ["Contribution amount does not match pension tier", "Incorrect contribution for age",
                                  "contri amount mismatch"],
    "age_not_eligible": ["Age not eligible for scheme", "Age criteria not met", "Applicant above max age"],
    "other": ["Signature mismatch", "Mobile number invalid", "Bank account inactive"],
    "duplicate_application": ["Duplicate application"],
}
APPROVE_REMARKS = ["OK", "Verified", "Approved", "All documents verified"]

# --------------------------------------------------------------------------- users.csv
def build_users():
    rows, uid = [], 1
    rows.append(dict(user_id=uid, username="0000", role="ADMIN", full_name="System Administrator", year_of_joining=2015,
                     branch="Corporate Center", region="Head Office")); uid += 1
    branch_user = {}
    for i, (b, r, _, _) in enumerate(BRANCHES):
        uname = str(1001 + i)
        rows.append(dict(user_id=uid, username=uname, role="USER", full_name=f"{b} Branch",
                         year_of_joining=int(rng.integers(2016, 2024)), branch=b, region=r)); uid += 1
        branch_user[b] = uname
    for a in REGION_APPROVER.values():
        rows.append(dict(user_id=uid, username=a, role="APPROVER", full_name=f"Approver {a}",
                         year_of_joining=int(rng.integers(2014, 2021)), branch="Head Office", region="Head Office")); uid += 1
    rows.append(dict(user_id=uid, username="3001", role="MANAGER", full_name="Regional Manager",
                     year_of_joining=2016, branch=None, region="Mumbai Region")); uid += 1
    # edge case mirrored from the real code path: same username twice, later row has NULL branch
    dup_user = branch_user["Hapur"]
    rows.append(dict(user_id=uid, username=dup_user, role="USER", full_name="Hapur Branch (re-registered)",
                     year_of_joining=2024, branch=None, region=None))
    return pd.DataFrame(rows), branch_user

# --------------------------------------------------------------------------- applications (ground truth)
def dow_weight(d):
    return {5: 0.5, 6: 0.03}.get(d.weekday(), 1.0)

def month_factor(d, scheme):
    f = 1.5 if d.month == 3 else 1.0                              # fiscal-year-end rush
    if scheme in ("PMJJBY", "PMSBY") and d.month == 5: f *= 1.8   # annual renewal / enrolment window
    return f

def sample_dates(scheme, n):
    days = [START + timedelta(days=i) for i in range((END - START).days + 1)]
    w = np.array([dow_weight(d) * month_factor(d, scheme) for d in days]); w /= w.sum()
    return [days[i] for i in rng.choice(len(days), size=n, p=w)]

def sample_age(scheme):
    mu, sd, lo, hi = AGE_PARAMS[scheme]
    if rng.random() < INELIGIBLE_P[scheme]:
        return int(hi + rng.integers(1, 9))                       # genuinely ineligible applicant
    return int(np.clip(round(rng.normal(mu, sd)), lo, hi))

def build_truth(branch_user):
    bw = np.array([b[2] for b in BRANCHES]); bw = bw / bw.sum()
    counts = [int(round(N_APPS * m)) for m in SCHEME_MIX]
    rows, seq = [], {s: 0 for s in SCHEMES}
    for scheme, n in zip(SCHEMES, counts):
        for sub in sample_dates(scheme, n):
            seq[scheme] += 1
            bi = int(rng.choice(len(BRANCHES), p=bw))
            bname, region, _, dm = BRANCHES[bi]
            approver = REGION_APPROVER[region]
            age = sample_age(scheme)
            q = 1 - (1 - dm) ** (1 / 3)                            # per-document miss prob
            docs = {k: (rng.random() >= q) for k in ("aadhaar_doc", "pan_doc", "cheque_doc")}
            any_missing = not all(docs.values())
            tier = contri = btype = None
            if scheme == "APY":
                tier = int(rng.choice(TIERS, p=[.35, .25, .2, .12, .08]))
                req = apy_required(age, tier)
                noncompliant = rng.random() < 0.07
                amount = int(round(req * (1 - rng.uniform(.05, .4)))) if noncompliant else req
            elif scheme == "PMJJBY": amount, noncompliant = 436, False
            elif scheme == "PMSBY": amount, noncompliant = 20, False
            elif scheme == "KVP":
                amount, noncompliant = int(np.clip(round(rng.lognormal(10.2, 1.0), -3), 1000, 500000)), False
            else:
                btype = str(rng.choice(["Shishu", "Kishore", "Tarun"], p=[.55, .35, .10]))
                lo_hi = {"Shishu": (10000, 50000), "Kishore": (55000, 500000), "Tarun": (505000, 1000000)}[btype]
                amount, noncompliant = int(round(rng.uniform(*lo_hi), -3)), False
            # ---- decision process
            delay = int(min(60, round(rng.lognormal(np.log(2.0 * APPROVER_SPEED[approver]), 0.7))))
            decided = sub + timedelta(days=delay)
            stale = rng.random() < APPROVER_STALE_P[approver] and sub <= SNAPSHOT - timedelta(days=35)
            status, cause, remark = "Pending", None, None
            if not stale and decided <= SNAPSHOT:
                inelig = age > AGE_PARAMS[scheme][3]
                if inelig and rng.random() < 0.95: status, cause = "Rejected", "age_not_eligible"
                elif noncompliant and rng.random() < 0.88: status, cause = "Rejected", "apy_contribution_mismatch"
                elif any_missing and rng.random() < 0.82: status, cause = "Rejected", "missing_documents"
                elif rng.random() < 0.03: status, cause = "Rejected", "other"
                else: status = "Approved"
                remark = (str(rng.choice(REMARKS[cause])) if status == "Rejected"
                          else (str(rng.choice(APPROVE_REMARKS)) if rng.random() < 0.35 else None))
            else:
                decided = None
            orphan = rng.random() < 0.004
            rows.append(dict(
                application_id=f"{scheme}-{seq[scheme]:06d}", scheme=scheme,
                full_name=f"{rng.choice(FIRST)} {rng.choice(LAST)}", age=age,
                aadhaar_last4=f"{int(rng.integers(0, 10000)):04d}",
                submitted_by="9999" if orphan else branch_user[bname],
                submission_date=sub, status=status, approver_remark=remark, decided_date=decided,
                approver_id=approver, amount=amount, pension_tier=tier, business_type=btype,
                aadhaar_doc=docs["aadhaar_doc"], pan_doc=docs["pan_doc"], cheque_doc=docs["cheque_doc"],
                true_branch=bname, true_cause=cause, any_doc_missing=any_missing, apy_noncompliant=noncompliant,
                is_resubmission=False))
    df = pd.DataFrame(rows)
    # resubmissions: same applicant re-files within a week, rejected as duplicate
    pick = df.sample(frac=0.012, random_state=SEED)
    extra = []
    for _, r in pick.iterrows():
        sub = r.submission_date + timedelta(days=int(rng.integers(1, 7)))
        if sub > END: continue
        r = r.copy(); seq[r.scheme] += 1
        r["application_id"] = f"{r.scheme}-{seq[r.scheme]:06d}"; r["submission_date"] = sub
        r["status"], r["true_cause"], r["is_resubmission"] = "Rejected", "duplicate_application", True
        r["approver_remark"] = "Duplicate application"
        r["decided_date"] = sub + timedelta(days=int(rng.integers(1, 4)))
        extra.append(r)
    df = pd.concat([df, pd.DataFrame(extra)], ignore_index=True)
    return df

# --------------------------------------------------------------------------- inject mess
def fmt_date(d, p_iso=0.85, p_dmy=0.10):
    if d is None or pd.isna(d): return None
    u = rng.random()
    if u < p_iso: return d.strftime("%Y-%m-%d")
    if u < p_iso + p_dmy: return d.strftime("%d/%m/%Y")
    return d.strftime("%d-%b-%Y")

def fmt_amount(v):
    u = rng.random()
    if u < .55: return f"{v}"
    if u < .70: return f"\u20b9{v:,}"
    if u < .82: return f"Rs. {v}"
    if u < .94: return f"{v}.00"
    return f"{v:,}"

def typo(text):
    return (text.replace("Aadhaar", str(rng.choice(["Adhaar", "Aadhar"]))).replace("Contribution", "Contibution"))

def make_raw(t):
    log = {}
    raw = pd.DataFrame(index=t.index)
    raw["application_id"] = t.application_id; raw["scheme"] = t.scheme
    # names
    def noisy_name(n):
        u = rng.random()
        if u < .05: return n.upper()
        if u < .10: return n.lower()
        if u < .15: return f"  {n.replace(' ', '  ')} "
        return n
    raw["full_name"] = t.full_name.map(noisy_name)
    log["name_format_noise_rows"] = int((raw.full_name != t.full_name).sum())
    # age
    age = t.age.astype(str).astype(object)
    idx = rng.permutation(len(t))
    n_null, n_bad = int(.015 * len(t)), int(.008 * len(t))
    age.iloc[idx[:n_null]] = None
    bad = ["0", "-3", "150", "5", "999"]
    for i in idx[n_null:n_null + n_bad]: age.iloc[i] = str(rng.choice(bad))
    for i in idx[n_null + n_bad:n_null + n_bad + 25]: age.iloc[i] = f"{t.age.iloc[i]} yrs"
    raw["age"] = age
    log["age_missing"] = n_null; log["age_garbage_values"] = n_bad; log["age_text_suffix"] = 25
    raw["aadhaar_last4"] = t.aadhaar_last4; raw["submitted_by"] = t.submitted_by
    log["orphan_submitted_by"] = int((t.submitted_by == "9999").sum())
    # dates
    sub = t.submission_date.map(fmt_date)
    n_unp = int(.003 * len(t)); n_fut = int(.002 * len(t))
    ii = rng.permutation(len(t))
    for i in ii[:n_unp]: sub.iloc[i] = str(rng.choice(["N/A", "unknown", "00/00/0000"]))
    for i in ii[n_unp:n_unp + n_fut]: sub.iloc[i] = (SNAPSHOT + timedelta(days=int(rng.integers(30, 400)))).strftime("%Y-%m-%d")
    raw["submission_date"] = sub
    log["submission_date_unparseable"] = n_unp; log["submission_date_in_future"] = n_fut
    # status
    def noisy_status(s):
        u = rng.random()
        if u < .04: return s.lower()
        if u < .07: return s.upper()
        if u < .10: return f" {s} "
        if u < .101: return {"Approved": "Aproved", "Rejected": "Rejeted", "Pending": "Pendng"}[s]
        return s
    raw["status"] = t.status.map(noisy_status)
    log["status_variants"] = int((raw.status != t.status).sum())
    # remarks
    def noisy_remark(r):
        if r is None or pd.isna(r): return None
        u = rng.random()
        if u < .03: return typo(r)
        if u < .10: return r.lower()
        if u < .16: return r + "."
        if u < .20: return f" {r} "
        return r
    raw["approver_remark"] = t.approver_remark.map(noisy_remark)
    # decided date (+ logic errors)
    dec = t.decided_date.map(lambda d: fmt_date(d, .9, .07))
    done = [i for i in range(len(t)) if t.decided_date.iloc[i] is not None and not pd.isna(t.decided_date.iloc[i])]
    n_logic = int(.005 * len(done))
    for i in rng.choice(done, size=n_logic, replace=False):
        dec.iloc[i] = (t.submission_date.iloc[i] - timedelta(days=int(rng.integers(1, 6)))).strftime("%Y-%m-%d")
    raw["decided_date"] = dec; raw["approver_id"] = t.approver_id
    log["decided_before_submitted"] = n_logic
    # amounts (+ x10 typos)
    amt = t.amount.copy().astype(float)
    n_out = int(.004 * len(t)); oi = rng.choice(len(t), size=n_out, replace=False)
    amt.iloc[oi] = amt.iloc[oi] * 10
    raw["amount_raw"] = amt.astype(int).map(fmt_amount)
    log["amount_x10_typos"] = n_out
    tv = t.pension_tier.map(lambda v: None if pd.isna(v) else
                            str(rng.choice([f"{int(v)}", f"\u20b9{int(v):,}", f"Rs. {int(v)}", f"{int(v)} per month"])))
    raw["pension_tier"] = tv; raw["business_type"] = t.business_type
    # docs
    def doc(v):
        if rng.random() < .01: return None
        yes = ["Yes", "Y", "yes", "1"] if v else ["No", "N", "no", "0"]
        return yes[0] if rng.random() < .85 else str(rng.choice(yes[1:]))
    for c in ("aadhaar_doc", "pan_doc", "cheque_doc"): raw[c] = t[c].map(doc)
    # duplicate rows (double submits)
    dups = raw.sample(frac=0.008, random_state=SEED + 1)
    raw = pd.concat([raw, dups], ignore_index=True).sample(frac=1, random_state=SEED + 2).reset_index(drop=True)
    log["exact_duplicate_rows_added"] = len(dups)
    log["resubmission_rows (near-duplicates, legitimately in data)"] = int(t.is_resubmission.sum())
    return raw, log

# --------------------------------------------------------------------------- docs
DICTIONARY = """# Data dictionary

All data is **synthetic**. Names are random first/last combinations; Aadhaar is reduced to 4 random digits;
no phone numbers or account numbers exist.

## data/raw/users.csv  (mirrors the `users` table)
| column | type | notes |
|---|---|---|
| user_id | int | primary key, ascending |
| username | text | employee id. **Not unique**: one username appears twice (second row has NULL branch) |
| role | text | USER (branch maker), APPROVER, MANAGER, ADMIN |
| full_name, year_of_joining | | |
| branch, region | text | NULL for some rows |

## data/raw/applications_raw.csv  (the five scheme tables stacked into one)
| column | meaning | known issues to expect |
|---|---|---|
| application_id | `SCHEME-000123` | duplicated in exact-duplicate rows |
| scheme | APY, PMJJBY, PMSBY, KVP, PMMY | |
| full_name | applicant | casing and spacing noise |
| age | years at submission | nulls, text suffixes, impossible values, genuinely ineligible ages |
| aadhaar_last4 | last 4 digits only | |
| submitted_by | `users.username` of the maker | some values match no user |
| submission_date | date filed | three formats, unparseable and future dates |
| status | Pending / Approved / Rejected | case, spacing, typos |
| approver_remark | free text | casing, punctuation, typos, null |
| decided_date | date of decision | **proposed column, not in the current Vartalaap schema**; some precede submission |
| approver_id | approver queue | **proposed column, not in the current schema** |
| amount_raw | scheme-specific amount as text: APY monthly contribution, PMJJBY/PMSBY premium, KVP deposit, PMMY loan | currency symbols, commas, decimals, x10 typos |
| pension_tier | APY only | formats vary |
| business_type | PMMY only: Shishu / Kishore / Tarun | |
| aadhaar_doc, pan_doc, cheque_doc | document received? | Yes/Y/yes/1 and No/N/no/0 variants, some nulls |

## Extract date
Aging and pending logic use **2026-10-01** as the data extract date.

## Proposed schema change for the real app
Add `decided_at` (timestamp) and `approver_id` to each form table so turnaround and approver workload can be
measured on real data, not just synthetic.
"""

PLANTED = """# Planted patterns (answer key)

These were deliberately built into the synthetic data so the analysis pipeline can be validated.
**Do not present them as real-world findings.** The value of the project is the method: cleaning, SQL,
anomaly detection and communication. Say so in your README and in interviews.

1. **Manzol** has ~30% of applications with at least one missing document, so its rejection rate is far above the network.
2. **Jaipur MI Road** has the highest volume (weight 2.3) but a high missing-document rate (17%), so it ranks near the top
   on volume and in the middle or lower on approval rate.
3. **Gurugram Sector 14** has low volume and an excellent approval rate (2% missing documents).
4. **Approver 2003 (Delhi Region)** decides about 2.6x slower than baseline and leaves ~9% of old applications undecided,
   creating a stale-pending backlog.
5. **Seasonality:** March volume is ~1.5x (fiscal-year end); PMJJBY and PMSBY volume is ~1.8x in May.
6. **APY non-compliance:** ~7% of APY applications have a contribution below the required amount and are rejected ~88% of the time.
7. **Missing documents** lift rejection from roughly 6% to roughly 83%.
8. **Data-quality issues** (counts in `data/ground_truth/messiness_log.json`): exact duplicates, mixed date formats,
   status/remark noise, unparseable and future dates, decided-before-submitted dates, impossible ages, x10 amount typos,
   orphan `submitted_by`, and a duplicate username with a NULL branch.
"""

def main():
    users, branch_user = build_users()
    truth = build_truth(branch_user)
    raw, log = make_raw(truth)
    users.to_csv(ROOT / "data/raw/users.csv", index=False, encoding="utf-8")
    raw.to_csv(ROOT / "data/raw/applications_raw.csv", index=False, encoding="utf-8")
    truth.to_csv(ROOT / "data/ground_truth/applications_truth.csv", index=False, encoding="utf-8")
    (ROOT / "data/ground_truth/messiness_log.json").write_text(json.dumps(log, indent=2))
    (ROOT / "docs/data_dictionary.md").write_text(DICTIONARY, encoding="utf-8")
    (ROOT / "docs/planted_patterns.md").write_text(PLANTED, encoding="utf-8")
    print(f"users: {len(users)} rows | applications_raw: {len(raw)} rows | truth: {len(truth)} rows")
    print(json.dumps(log, indent=2))

if __name__ == "__main__":
    main()
