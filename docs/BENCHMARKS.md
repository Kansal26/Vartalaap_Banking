# Analytics Query Benchmarks

This document records the performance improvements for the `getBranchRankings` analytics query.

## Environment

*   **Machine/OS:** Apple MacBook Air, macOS, Apple M2
*   **Runtime JDK:** OpenJDK 24.0.1 (project source/target: Java 17)
*   **Database:** H2 In-Memory (`benchmark` profile)
*   **Dataset:** 41 distinct users/branches, 50,000 synthetic form rows (10,000 per scheme), randomly assigned `submitted_by`
*   **Methodology:** 5 warm-up iterations, 30 measured iterations, `System.nanoTime()`, 3 separate runs

## Results (Run 2 — representative middle run)

| Implementation | Min (ms) | Median (ms) | Mean (ms) | p95 (ms) | Hibernate Queries / iter | Entities Loaded / iter |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Legacy (Java Aggregation)** | 47.59 | 54.01 | 59.14 | 73.20 | 6 | 50,050 |
| **Optimized (SQL GROUP BY)** | 0.77 | 0.92 | 1.01 | 1.45 | 1 | 0 |

## Median across all three runs

| Run | Legacy Median (ms) | Optimized Median (ms) | Speedup |
| :--- | :--- | :--- | :--- |
| Run 1 | 54.22 | 0.93 | ~58× |
| Run 2 | 54.01 | 0.92 | ~59× |
| Run 3 | 53.98 | 1.00 | ~54× |

## Analysis

The legacy implementation fetched all users and all 5 form tables entirely into application memory (hydration of 50,050 JPA entities via 6 queries) and performed loop-based aggregation in Java.
The optimized version executes a single SQL query (`UNION ALL` + `JOIN` + `GROUP BY`), projecting directly into a DTO interface.
This reduces heap allocations significantly (0 entities loaded) and achieves a consistent **~54–59× speedup** across all three runs.

Indexes were added to `User(username, branch)` and `submitted_by` on all form entities. However, the vast majority of the speedup is attributable to eliminating entity hydration overhead — the H2 in-memory DB is extremely fast at scanning memory, but materialising 50,050 Java objects on every request is slow regardless of DB speed.
