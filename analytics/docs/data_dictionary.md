# Data dictionary

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
