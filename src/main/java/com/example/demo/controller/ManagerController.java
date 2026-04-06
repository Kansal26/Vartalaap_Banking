package com.example.demo.controller;

import com.example.demo.dto.BranchPerformanceDTO;
import com.example.demo.model.TransferRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AnalyticsService;
import com.example.demo.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class ManagerController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/manager_dashboard")
    public String managerDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User manager = userRepo.findByUsername(username);

        // 1. Fetch Branch Rankings
        List<BranchPerformanceDTO> rankings = analyticsService.getBranchRankings();
        model.addAttribute("rankings", rankings);

        // 2. Fetch My Transfer Requests
        List<TransferRequest> myRequests = transferService.getMyRequests(manager);
        model.addAttribute("myRequests", myRequests);

        // 3. Prepare Data for Charts (Aggregation for the whole bank or manager's
        // branch?)
        // For "Power BI looking dashboard", let's aggregate totals across all branches
        // for the Pie Chart
        long totalApy = rankings.stream().mapToLong(BranchPerformanceDTO::getApyCount).sum();
        long totalPmjjby = rankings.stream().mapToLong(BranchPerformanceDTO::getPmjjbyCount).sum();
        long totalPmsby = rankings.stream().mapToLong(BranchPerformanceDTO::getPmsbyCount).sum();
        long totalKvp = rankings.stream().mapToLong(BranchPerformanceDTO::getKvpCount).sum();
        long totalPmmy = rankings.stream().mapToLong(BranchPerformanceDTO::getPmmyCount).sum();

        model.addAttribute("totalApy", totalApy);
        model.addAttribute("totalPmjjby", totalPmjjby);
        model.addAttribute("totalPmsby", totalPmsby);
        model.addAttribute("totalKvp", totalKvp);
        model.addAttribute("totalPmmy", totalPmmy);

        model.addAttribute("managerName", manager.getFullName());

        return "manager_dashboard";
    }

    @PostMapping("/manager/transfer-request")
    public String createTransferRequest(@RequestParam String targetBranch,
            @RequestParam String targetRegion,
            @RequestParam String replacementName,
            Principal principal) {
        String username = principal.getName();
        User manager = userRepo.findByUsername(username);

        transferService.createTransferRequest(manager, targetBranch, targetRegion, replacementName);

        return "redirect:/manager_dashboard?success=true";
    }
}
