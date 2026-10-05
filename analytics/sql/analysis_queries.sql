-- q1_branch_scorecard
WITH scorecard AS (
  SELECT branch,
         COUNT(*) AS total,
         SUM(CASE WHEN status = 'Approved' THEN 1 ELSE 0 END) AS approved,
         SUM(CASE WHEN status = 'Rejected' THEN 1 ELSE 0 END) AS rejected,
         SUM(CASE WHEN status = 'Pending'  THEN 1 ELSE 0 END) AS pending
  FROM applications
  WHERE branch <> 'Unknown'
  GROUP BY branch)
SELECT branch, total, approved, rejected, pending,
       ROUND(1.0 * approved / NULLIF(approved + rejected, 0), 3) AS approval_rate,
       RANK() OVER (ORDER BY total DESC) AS volume_rank,
       RANK() OVER (ORDER BY 1.0 * approved / NULLIF(approved + rejected, 0) DESC) AS quality_rank
FROM scorecard
ORDER BY volume_rank;

-- q2_monthly_volume_mom
WITH m AS (
  SELECT substr(submission_date, 1, 7) AS month, COUNT(*) AS apps
  FROM applications GROUP BY substr(submission_date, 1, 7))
SELECT month, apps,
       LAG(apps) OVER (ORDER BY month) AS prev_month,
       ROUND(100.0 * (apps - LAG(apps) OVER (ORDER BY month)) / LAG(apps) OVER (ORDER BY month), 1) AS mom_pct
FROM m ORDER BY month;

-- q3_pending_aging
SELECT approver_id,
       SUM(CASE WHEN pending_age_days <= 7 THEN 1 ELSE 0 END)  AS d0_7,
       SUM(CASE WHEN pending_age_days BETWEEN 8 AND 30 THEN 1 ELSE 0 END) AS d8_30,
       SUM(CASE WHEN pending_age_days BETWEEN 31 AND 90 THEN 1 ELSE 0 END) AS d31_90,
       SUM(CASE WHEN pending_age_days > 90 THEN 1 ELSE 0 END)  AS d90_plus,
       COUNT(*) AS pending_total
FROM applications WHERE status = 'Pending'
GROUP BY approver_id ORDER BY pending_total DESC;

-- q4_approver_turnaround
WITH d AS (
  SELECT approver_id, turnaround_days,
         ROW_NUMBER() OVER (PARTITION BY approver_id ORDER BY turnaround_days) AS rn,
         COUNT(*) OVER (PARTITION BY approver_id) AS cnt
  FROM applications WHERE turnaround_days IS NOT NULL)
SELECT approver_id, COUNT(*) AS n,
       ROUND(AVG(turnaround_days), 2) AS avg_days,
       MAX(CASE WHEN rn = (cnt + 1) / 2 THEN turnaround_days END) AS median_days   -- lower median
FROM d GROUP BY approver_id ORDER BY avg_days DESC;

-- q5_rejection_reason_share
SELECT branch, rejection_reason, COUNT(*) AS n,
       ROUND(100.0 * COUNT(*) / SUM(COUNT(*)) OVER (PARTITION BY branch), 1) AS pct_of_branch_rejections
FROM applications
WHERE status = 'Rejected' AND branch <> 'Unknown'
GROUP BY branch, rejection_reason
ORDER BY branch, n DESC;
