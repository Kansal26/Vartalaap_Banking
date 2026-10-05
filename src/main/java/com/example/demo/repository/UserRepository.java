package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsByUsername(String username);

    User findByEmail(String email);

    boolean existsByEmail(String email);
    
    List<User> findByBranch(String branch);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT u.branch) FROM User u WHERE u.branch IS NOT NULL")
    long countDistinctBranches();

    // Assumption: legacy relies on findAll() returning in order of id ASC. Thus MIN(id) replicates picking the first entity.
    @Query(value = """
        SELECT 
          inner_q."branchName",
          inner_q."apyCount",
          inner_q."pmjjbyCount",
          inner_q."pmsbyCount",
          inner_q."kvpCount",
          inner_q."pmmyCount"
        FROM (
          SELECT 
            u.branch AS "branchName",
            SUM(CASE WHEN f.scheme = 'apy' THEN 1 ELSE 0 END) AS "apyCount",
            SUM(CASE WHEN f.scheme = 'pmjjby' THEN 1 ELSE 0 END) AS "pmjjbyCount",
            SUM(CASE WHEN f.scheme = 'pmsby' THEN 1 ELSE 0 END) AS "pmsbyCount",
            SUM(CASE WHEN f.scheme = 'kvp' THEN 1 ELSE 0 END) AS "kvpCount",
            SUM(CASE WHEN f.scheme = 'pmmy' THEN 1 ELSE 0 END) AS "pmmyCount",
            SUM(CASE WHEN f.scheme = 'apy' THEN 1 ELSE 0 END) +
            SUM(CASE WHEN f.scheme = 'pmjjby' THEN 1 ELSE 0 END) +
            SUM(CASE WHEN f.scheme = 'pmsby' THEN 1 ELSE 0 END) +
            SUM(CASE WHEN f.scheme = 'kvp' THEN 1 ELSE 0 END) +
            SUM(CASE WHEN f.scheme = 'pmmy' THEN 1 ELSE 0 END) AS "totalCount"
          FROM (
            SELECT submitted_by, 'apy' as scheme FROM apy_form WHERE submitted_by IS NOT NULL
            UNION ALL
            SELECT submitted_by, 'pmjjby' as scheme FROM pmjjby_form WHERE submitted_by IS NOT NULL
            UNION ALL
            SELECT submitted_by, 'pmsby' as scheme FROM pmsby_form WHERE submitted_by IS NOT NULL
            UNION ALL
            SELECT submitted_by, 'kvp' as scheme FROM kvp_form WHERE submitted_by IS NOT NULL
            UNION ALL
            SELECT submitted_by, 'pmmy' as scheme FROM pmmy_form WHERE submitted_by IS NOT NULL
          ) f
          INNER JOIN (
            SELECT username, branch 
            FROM users 
            WHERE id IN (
              SELECT MIN(id) FROM users WHERE branch IS NOT NULL AND username IS NOT NULL GROUP BY username
            )
          ) u ON u.username = f.submitted_by
          GROUP BY u.branch
        ) inner_q
        ORDER BY inner_q."totalCount" DESC, inner_q."branchName" ASC
    """, nativeQuery = true)
    List<BranchPerformanceProjection> getBranchRankings();
}