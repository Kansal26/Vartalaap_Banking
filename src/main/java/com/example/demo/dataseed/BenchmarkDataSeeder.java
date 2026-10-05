package com.example.demo.dataseed;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Profile({"benchmark", "benchmark-pg"})
public class BenchmarkDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final apyFormRepository apyRepo;
    private final pmjjbyFormRepository pmjjbyRepo;
    private final pmsbyFormRepository pmsbyRepo;
    private final kvpFormRepository kvpRepo;
    private final pmmyFormRepository pmmyRepo;

    public BenchmarkDataSeeder(UserRepository userRepository, apyFormRepository apyRepo,
                               pmjjbyFormRepository pmjjbyRepo, pmsbyFormRepository pmsbyRepo,
                               kvpFormRepository kvpRepo, pmmyFormRepository pmmyRepo) {
        this.userRepository = userRepository;
        this.apyRepo = apyRepo;
        this.pmjjbyRepo = pmjjbyRepo;
        this.pmsbyRepo = pmsbyRepo;
        this.kvpRepo = kvpRepo;
        this.pmmyRepo = pmmyRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting benchmark data seeding...");
        
        long count = apyRepo.count();
        if (count > 0) {
            System.out.println("Data already seeded. Skipping.");
            return;
        }

        Random random = new Random(42); // Reproducible seed
        
        // Create 40 distinct branches and users
        List<User> users = new ArrayList<>();
        List<String> usernames = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            User u = new User();
            u.setUsername("user_" + i);
            u.setBranch("Branch_" + String.format("%02d", i));
            u.setFullName("Benchmark User " + i);
            users.add(u);
            usernames.add(u.getUsername());
        }

        // Edge case: user with null branch
        User nullBranchUser = new User();
        nullBranchUser.setUsername("null_branch_user");
        nullBranchUser.setFullName("Null Branch User");
        users.add(nullBranchUser);
        usernames.add("null_branch_user");

        userRepository.saveAll(users);

        // Generate 10000 forms for each type
        int numForms = 10000;
        
        System.out.println("Seeding APY forms...");
        List<apyForm> apyForms = new ArrayList<>();
        for (int i = 0; i < numForms; i++) {
            apyForm f = new apyForm();
            f.onCreate();
            f.setSubmittedBy(usernames.get(random.nextInt(usernames.size())));
            f.setSubmissionDate(LocalDate.now().minusDays(random.nextInt(365)));
            if (i == 0) f.setSubmittedBy(null); // Edge case
            if (i == 1) f.setSubmittedBy("unmatched_user"); // Edge case
            apyForms.add(f);
        }
        apyRepo.saveAll(apyForms);

        System.out.println("Seeding PMJJBY forms...");
        List<pmjjbyForm> pmjjbyForms = new ArrayList<>();
        for (int i = 0; i < numForms; i++) {
            pmjjbyForm f = new pmjjbyForm();
            f.onCreate();
            f.setSubmittedBy(usernames.get(random.nextInt(usernames.size())));
            f.setSubmissionDate(LocalDate.now().minusDays(random.nextInt(365)));
            if (i == 0) f.setSubmittedBy(null);
            if (i == 1) f.setSubmittedBy("unmatched_user");
            pmjjbyForms.add(f);
        }
        pmjjbyRepo.saveAll(pmjjbyForms);

        System.out.println("Seeding PMSBY forms...");
        List<pmsbyForm> pmsbyForms = new ArrayList<>();
        for (int i = 0; i < numForms; i++) {
            pmsbyForm f = new pmsbyForm();
            f.onCreate();
            f.setSubmittedBy(usernames.get(random.nextInt(usernames.size())));
            f.setSubmissionDate(LocalDate.now().minusDays(random.nextInt(365)));
            if (i == 0) f.setSubmittedBy(null);
            if (i == 1) f.setSubmittedBy("unmatched_user");
            pmsbyForms.add(f);
        }
        pmsbyRepo.saveAll(pmsbyForms);

        System.out.println("Seeding KVP forms...");
        List<kvpForm> kvpForms = new ArrayList<>();
        for (int i = 0; i < numForms; i++) {
            kvpForm f = new kvpForm();
            f.onCreate();
            f.setSubmittedBy(usernames.get(random.nextInt(usernames.size())));
            f.setSubmissionDate(LocalDate.now().minusDays(random.nextInt(365)));
            if (i == 0) f.setSubmittedBy(null);
            if (i == 1) f.setSubmittedBy("unmatched_user");
            kvpForms.add(f);
        }
        kvpRepo.saveAll(kvpForms);

        System.out.println("Seeding PMMY forms...");
        List<pmmyForm> pmmyForms = new ArrayList<>();
        for (int i = 0; i < numForms; i++) {
            pmmyForm f = new pmmyForm();
            f.onCreate();
            f.setSubmittedBy(usernames.get(random.nextInt(usernames.size())));
            f.setSubmissionDate(LocalDate.now().minusDays(random.nextInt(365)));
            if (i == 0) f.setSubmittedBy(null);
            if (i == 1) f.setSubmittedBy("unmatched_user");
            pmmyForms.add(f);
        }
        pmmyRepo.saveAll(pmmyForms);

        System.out.println("Benchmark data seeding completed.");
    }
}
