# Vartalaap Banking: Application Pipeline Analytics

Analytics layer for the Vartalaap bank-scheme application portal (APY, PMJJBY, PMSBY, KVP, PMMY).
Cleans messy application data, measures branch quality and approval turnaround, flags anomalies,
and exports tables for a Tableau dashboard.

> **All data is synthetic.** It was generated to mirror the real schema, with data-quality problems and
> business patterns deliberately planted (`docs/planted_patterns.md`). The analysis recovering them validates
> the *pipeline*; it is not evidence about real branches.

## Run it
```bash
pip install -r requirements.txt
python generate_data.py            # writes data/raw/*, data/ground_truth/*, docs/*
python build_notebook.py           # (optional) rebuilds the notebook from source
jupyter nbconvert --to notebook --execute --inplace notebooks/01_cleaning_and_analysis.ipynb
```

## What is inside
| path | purpose |
|---|---|
| `generate_data.py` | seeded synthetic data generator (reproducible, seed 42) |
| `notebooks/01_cleaning_and_analysis.ipynb` | profiling, cleaning with a decision log, audit, analysis, SQL, findings |
| `sql/analysis_queries.sql` | window-function queries (scorecard, MoM, aging, turnaround, reason share) |
| `data/clean/` | cleaned fact table and summary tables for Tableau |
| `data/clean/cleaning_log.csv` | every cleaning decision with rows affected |
| `docs/data_dictionary.md` | columns, known issues, proposed schema change |
| `charts/` | PNG charts used in the findings |

## Method highlights
* **Clean without silent deletion:** unambiguous issues fixed (exact duplicates, formats), ambiguous ones flagged (suspect amounts, resubmissions, ineligible ages).
* **Audit:** cleaning is checked against the answer key; amount-typo detection reports recall (88%) and precision (100%).
* **Anomaly detection:** branch rejection rate vs network rate using a z-score, flagged at z > 3.
* **SQL cross-check:** the SQL scorecard is asserted equal to the pandas scorecard.

## Limitations
* Synthetic data; planted patterns.
* `decided_date` and `approver_id` are not in the current Vartalaap schema (proposed addition).
* Rejection-reason accuracy is inflated by the small template set used for synthetic remarks.
