package com.example.demo;

import com.example.demo.dto.BranchPerformanceDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.AnalyticsService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("benchmark") // or benchmark-pg based on runner
@Tag("benchmark")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnalyticsBenchmarkTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private apyFormRepository apyRepo;
    
    @Autowired
    private pmjjbyFormRepository pmjjbyRepo;
    
    @Autowired
    private pmsbyFormRepository pmsbyRepo;
    
    @Autowired
    private kvpFormRepository kvpRepo;
    
    @Autowired
    private pmmyFormRepository pmmyRepo;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private Environment env;

    @BeforeAll
    public void guardInMemoryDb() {
        String dbUrl = env.getProperty("spring.datasource.url", "");
        if (!dbUrl.contains("jdbc:h2:mem:")) {
            throw new IllegalStateException("SAFETY GUARD: Test must run against an in-memory DB! Active URL: " + dbUrl);
        }
    }

    @Test
    public void testSeededParityAndBenchmark() {
        // 1. Parity Check on Seeded Data
        List<BranchPerformanceDTO> legacyRankings = analyticsService.getBranchRankingsLegacy();
        List<BranchPerformanceDTO> optimizedRankings = analyticsService.getBranchRankings();

        // Sort legacy deterministically to compare properly because legacy tie-breaker is non-deterministic
        legacyRankings.sort(Comparator.comparingLong(BranchPerformanceDTO::getTotalScore).reversed()
                .thenComparing(BranchPerformanceDTO::getBranchName));
        
        assertEquals(legacyRankings.size(), optimizedRankings.size(), "Result sizes must match");

        for (int i = 0; i < legacyRankings.size(); i++) {
            BranchPerformanceDTO leg = legacyRankings.get(i);
            BranchPerformanceDTO opt = optimizedRankings.get(i);
            assertEquals(leg.getBranchName(), opt.getBranchName(), "Branch names must match at sorted index " + i);
            assertEquals(leg.getTotalScore(), opt.getTotalScore(), "Total scores must match");
            assertEquals(leg.getApyCount(), opt.getApyCount(), "APY counts must match");
            assertEquals(leg.getPmjjbyCount(), opt.getPmjjbyCount(), "PMJJBY counts must match");
            assertEquals(leg.getPmsbyCount(), opt.getPmsbyCount(), "PMSBY counts must match");
            assertEquals(leg.getKvpCount(), opt.getKvpCount(), "KVP counts must match");
            assertEquals(leg.getPmmyCount(), opt.getPmmyCount(), "PMMY counts must match");
        }

        // 2. Benchmarking
        int warmup = 5;
        int iterations = 30;

        System.out.println("Warmup legacy...");
        for (int i = 0; i < warmup; i++) analyticsService.getBranchRankingsLegacy();
        
        System.out.println("Warmup optimized...");
        for (int i = 0; i < warmup; i++) analyticsService.getBranchRankings();

        long[] legacyTimes = new long[iterations];
        long[] optTimes = new long[iterations];

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        System.out.println("Measuring legacy...");
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            analyticsService.getBranchRankingsLegacy();
            legacyTimes[i] = System.nanoTime() - start;
        }
        Statistics legStats = sessionFactory.getStatistics();
        long legQueries = legStats.getQueryExecutionCount() / iterations;
        long legEntities = legStats.getEntityLoadCount() / iterations;

        System.out.println("Measuring optimized...");
        sessionFactory.getStatistics().clear();
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            analyticsService.getBranchRankings();
            optTimes[i] = System.nanoTime() - start;
        }
        Statistics optStats = sessionFactory.getStatistics();
        long optQueries = optStats.getQueryExecutionCount() / iterations;
        long optEntities = optStats.getEntityLoadCount() / iterations;

        Arrays.sort(legacyTimes);
        Arrays.sort(optTimes);

        System.out.println("\n========== BENCHMARK RESULTS ==========");
        System.out.println("Legacy -> Min: " + (legacyTimes[0]/1_000_000.0) + "ms, Median: " + (legacyTimes[iterations/2]/1_000_000.0) + 
                           "ms, Mean: " + (Arrays.stream(legacyTimes).average().orElse(0)/1_000_000.0) + "ms, p95: " + (legacyTimes[(int)(iterations*0.95)]/1_000_000.0) + "ms");
        System.out.println("Legacy Hibernate -> Queries/iter: " + legQueries + ", Entities loaded/iter: " + legEntities);
        
        System.out.println("Optimized -> Min: " + (optTimes[0]/1_000_000.0) + "ms, Median: " + (optTimes[iterations/2]/1_000_000.0) + 
                           "ms, Mean: " + (Arrays.stream(optTimes).average().orElse(0)/1_000_000.0) + "ms, p95: " + (optTimes[(int)(iterations*0.95)]/1_000_000.0) + "ms");
        System.out.println("Optimized Hibernate -> Queries/iter: " + optQueries + ", Entities loaded/iter: " + optEntities);
        System.out.println("=======================================\n");
    }
}
