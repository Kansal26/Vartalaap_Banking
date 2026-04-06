package com.example.demo.service;

import com.example.demo.dto.BranchPerformanceDTO;
import com.example.demo.model.User;
import com.example.demo.model.apyForm;
import com.example.demo.model.pmjjbyForm;
import com.example.demo.model.pmsbyForm;
import com.example.demo.model.kvpForm;
import com.example.demo.model.pmmyForm;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.apyFormRepository;
import com.example.demo.repository.pmjjbyFormRepository;
import com.example.demo.repository.pmsbyFormRepository;
import com.example.demo.repository.kvpFormRepository;
import com.example.demo.repository.pmmyFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private UserRepository userRepo;

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

    public List<BranchPerformanceDTO> getBranchRankings() {
        // Fetch all users to map employees to branches
        List<User> users = userRepo.findAll();
        Map<String, String> employeeBranchMap = users.stream()
                .filter(u -> u.getBranch() != null && u.getUsername() != null)
                .collect(Collectors.toMap(User::getUsername, User::getBranch, (existing, replacement) -> existing));

        // Aggregate counts per branch
        Map<String, Long> apyCounts = new HashMap<>();
        for (apyForm form : apyRepo.findAll()) {
            String branch = employeeBranchMap.get(form.getSubmittedBy());
            if (branch != null) apyCounts.put(branch, apyCounts.getOrDefault(branch, 0L) + 1);
        }

        Map<String, Long> pmjjbyCounts = new HashMap<>();
        for (pmjjbyForm form : pmjjbyRepo.findAll()) {
            String branch = employeeBranchMap.get(form.getSubmittedBy());
            if (branch != null) pmjjbyCounts.put(branch, pmjjbyCounts.getOrDefault(branch, 0L) + 1);
        }

        Map<String, Long> pmsbyCounts = new HashMap<>();
        for (pmsbyForm form : pmsbyRepo.findAll()) {
            String branch = employeeBranchMap.get(form.getSubmittedBy());
            if (branch != null) pmsbyCounts.put(branch, pmsbyCounts.getOrDefault(branch, 0L) + 1);
        }

        Map<String, Long> kvpCounts = new HashMap<>();
        for (kvpForm form : kvpRepo.findAll()) {
            String branch = employeeBranchMap.get(form.getSubmittedBy());
            if (branch != null) kvpCounts.put(branch, kvpCounts.getOrDefault(branch, 0L) + 1);
        }

        Map<String, Long> pmmyCounts = new HashMap<>();
        for (pmmyForm form : pmmyRepo.findAll()) {
            String branch = employeeBranchMap.get(form.getSubmittedBy());
            if (branch != null) pmmyCounts.put(branch, pmmyCounts.getOrDefault(branch, 0L) + 1);
        }

        // Combine into DTOs
        Set<String> allBranches = new HashSet<>();
        allBranches.addAll(apyCounts.keySet());
        allBranches.addAll(pmjjbyCounts.keySet());
        allBranches.addAll(pmsbyCounts.keySet());
        allBranches.addAll(kvpCounts.keySet());
        allBranches.addAll(pmmyCounts.keySet());

        List<BranchPerformanceDTO> rankings = new ArrayList<>();
        for (String branch : allBranches) {
            long apy = apyCounts.getOrDefault(branch, 0L);
            long pmjjby = pmjjbyCounts.getOrDefault(branch, 0L);
            long pmsby = pmsbyCounts.getOrDefault(branch, 0L);
            long kvp = kvpCounts.getOrDefault(branch, 0L);
            long pmmy = pmmyCounts.getOrDefault(branch, 0L);
            rankings.add(new BranchPerformanceDTO(branch, apy, pmjjby, pmsby, kvp, pmmy));
        }

        // Sort by Total Score Descending
        rankings.sort(Comparator.comparingLong(BranchPerformanceDTO::getTotalScore).reversed());

        // Assign ranks
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }
}
