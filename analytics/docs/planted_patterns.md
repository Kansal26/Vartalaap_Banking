# Planted patterns (answer key)

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
