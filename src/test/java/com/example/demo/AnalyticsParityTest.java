package com.example.demo;

import com.example.demo.dto.BranchPerformanceDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.AnalyticsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AnalyticsParityTest {

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
    private Environment env;

    @BeforeAll
    public void guardInMemoryDb() {
        String dbUrl = env.getProperty("spring.datasource.url", "");
        if (!dbUrl.contains("jdbc:h2:mem:")) {
            throw new IllegalStateException("SAFETY GUARD: Test must run against an in-memory DB! Active URL: " + dbUrl);
        }
    }

    @BeforeEach
    public void setup() {
        apyRepo.deleteAllInBatch();
        pmjjbyRepo.deleteAllInBatch();
        pmsbyRepo.deleteAllInBatch();
        kvpRepo.deleteAllInBatch();
        pmmyRepo.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    // Assumption: Legacy code relies on userRepo.findAll() returning entities in ID ASC order. 
    // Thus, MIN(id) in SQL perfectly replicates the behavior of picking the first entity.
    @Test
    public void testDuplicateUsernameLowerIdWins() {
        User u1 = new User(); u1.setUsername("dup_user"); u1.setBranch("Branch_A");
        User u2 = new User(); u2.setUsername("dup_user"); u2.setBranch("Branch_B");
        userRepository.save(u1); // lower ID
        userRepository.save(u2); // higher ID

        apyForm a1 = new apyForm(); a1.setSubmittedBy("dup_user"); apyRepo.save(a1);

        // Expected count: Branch_A: 1 APY (u1 has lower ID). Branch_B: 0.
        List<BranchPerformanceDTO> legacy = analyticsService.getBranchRankingsLegacy();
        List<BranchPerformanceDTO> optimized = analyticsService.getBranchRankings();

        assertEquals(1, optimized.size());
        assertEquals("Branch_A", optimized.get(0).getBranchName());
        assertEquals(1, optimized.get(0).getApyCount());
        
        assertParity(legacy, optimized);
    }

    @Test
    public void testDuplicateUsernameNullBranchWins() {
        User u3 = new User(); u3.setUsername("dup_null"); u3.setBranch(null);
        User u4 = new User(); u4.setUsername("dup_null"); u4.setBranch("Branch_C");
        userRepository.save(u3); // lower ID, null branch
        userRepository.save(u4); // higher ID, non-null branch

        apyForm a2 = new apyForm(); a2.setSubmittedBy("dup_null"); apyRepo.save(a2);

        // Legacy behavior filters out null branches FIRST (via stream filter) before doing toMap deduplication.
        // Thus, the first user with a NON-NULL branch wins.
        // Expected count: Branch_C: 1 APY.
        List<BranchPerformanceDTO> legacy = analyticsService.getBranchRankingsLegacy();
        List<BranchPerformanceDTO> optimized = analyticsService.getBranchRankings();

        assertEquals(1, optimized.size());
        assertEquals("Branch_C", optimized.get(0).getBranchName());
        assertEquals(1, optimized.get(0).getApyCount());

        assertParity(legacy, optimized);
    }

    @Test
    public void testNullOrUnmatchedSubmittedByIgnored() {
        User u1 = new User(); u1.setUsername("valid_user"); u1.setBranch("Branch_A");
        userRepository.save(u1);

        apyForm a1 = new apyForm(); a1.setSubmittedBy(null); apyRepo.save(a1);
        apyForm a2 = new apyForm(); a2.setSubmittedBy("unmatched_ghost"); apyRepo.save(a2);
        apyForm a3 = new apyForm(); a3.setSubmittedBy("valid_user"); apyRepo.save(a3);

        // Expected count: null and unmatched are ignored. Branch_A gets 1 APY.
        List<BranchPerformanceDTO> legacy = analyticsService.getBranchRankingsLegacy();
        List<BranchPerformanceDTO> optimized = analyticsService.getBranchRankings();

        assertEquals(1, optimized.size());
        assertEquals("Branch_A", optimized.get(0).getBranchName());
        assertEquals(1, optimized.get(0).getApyCount());

        assertParity(legacy, optimized);
    }

    @Test
    public void testEqualTotalsOrderedByBranchNameAsc() {
        User u5 = new User(); u5.setUsername("tie_user_1"); u5.setBranch("Tie_Z");
        User u6 = new User(); u6.setUsername("tie_user_2"); u6.setBranch("Tie_A");
        userRepository.save(u5);
        userRepository.save(u6);

        pmjjbyForm p1 = new pmjjbyForm(); p1.setSubmittedBy("tie_user_1"); pmjjbyRepo.save(p1);
        pmjjbyForm p2 = new pmjjbyForm(); p2.setSubmittedBy("tie_user_2"); pmjjbyRepo.save(p2);

        // Expected count: Tie_A: 1, Tie_Z: 1.
        // Tie_A should appear before Tie_Z in the optimized list (alphabetical ASC).
        List<BranchPerformanceDTO> legacy = analyticsService.getBranchRankingsLegacy();
        List<BranchPerformanceDTO> optimized = analyticsService.getBranchRankings();

        assertEquals(2, optimized.size());
        assertEquals("Tie_A", optimized.get(0).getBranchName());
        assertEquals("Tie_Z", optimized.get(1).getBranchName());

        // We do not assert exact list equality with legacy here because legacy ordering for ties is non-deterministic (HashSet iteration).
        // Instead we ensure the counts match independently of order.
        assertEquals(1, optimized.get(0).getPmjjbyCount());
        assertEquals(1, optimized.get(1).getPmjjbyCount());
        
        BranchPerformanceDTO legZ = legacy.stream().filter(l -> l.getBranchName().equals("Tie_Z")).findFirst().get();
        assertEquals(1, legZ.getPmjjbyCount());
    }

    private void assertParity(List<BranchPerformanceDTO> legacy, List<BranchPerformanceDTO> optimized) {
        assertEquals(legacy.size(), optimized.size(), "Result sizes must match");
        for (BranchPerformanceDTO optDto : optimized) {
            BranchPerformanceDTO legDto = legacy.stream().filter(l -> l.getBranchName().equals(optDto.getBranchName())).findFirst().get();
            assertEquals(legDto.getApyCount(), optDto.getApyCount());
            assertEquals(legDto.getPmjjbyCount(), optDto.getPmjjbyCount());
            assertEquals(legDto.getPmsbyCount(), optDto.getPmsbyCount());
            assertEquals(legDto.getKvpCount(), optDto.getKvpCount());
            assertEquals(legDto.getPmmyCount(), optDto.getPmmyCount());
            assertEquals(legDto.getTotalScore(), optDto.getTotalScore());
        }
    }
}
