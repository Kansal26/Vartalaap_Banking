package com.example.demo.controller;

import com.example.demo.dto.ApyDTO;
import com.example.demo.dto.PmjjbyDTO;
import com.example.demo.dto.PmsbyDTO;
import com.example.demo.model.apyForm;
import com.example.demo.model.pmjjbyForm;
import com.example.demo.model.pmsbyForm;
import com.example.demo.repository.apyFormRepository;
import com.example.demo.repository.pmjjbyFormRepository;
import com.example.demo.repository.pmsbyFormRepository;
import com.example.demo.service.ApyService;
import com.example.demo.service.PmjjbyService;
import com.example.demo.service.PmsbyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@SessionAttributes("form")
public class AuthController {

    @Autowired
    private apyFormRepository apyFormRepository;

    @Autowired
    private pmjjbyFormRepository pmjjbyFormRepository;

    @Autowired
    private pmsbyFormRepository pmsbyFormRepository;

    @Autowired
    private ApyService apyService;

    @Autowired
    private PmjjbyService pmjjbyService;

    @Autowired
    private PmsbyService pmsbyService;

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/apy")
    public String apy(Model model) {
        model.addAttribute("form", new apyForm());
        return "apy";
    }

    @PostMapping("/submitApy")
    public String handleApySubmit(@ModelAttribute("form") apyForm form, RedirectAttributes redirectAttributes,Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        apyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/pmjjby")
    public String pmjjby(Model model) {
        model.addAttribute("form", new pmjjbyForm());
        return "pmjjby";
    }

    @PostMapping("/submitPmjjby")
    public String handlePmjjbySubmit(
            @ModelAttribute("form") pmjjbyForm form,
            RedirectAttributes redirectAttributes,Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        pmjjbyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/pmsby")
    public String pmsby(Model model) {
        model.addAttribute("form", new pmsbyForm());
        return "pmsby";
    }

    @PostMapping("/submitPmsby")
    public String handlePmsbySubmit(@ModelAttribute("form") pmsbyForm form, RedirectAttributes redirectAttributes, Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        pmsbyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "All") String status,
                            Model model,
                            Authentication authentication,
                            @ModelAttribute("success") Object success) {
        List<ApyDTO> apyTransactions = apyService.getAllApyTransactions();
        List<PmjjbyDTO> pmjjbyTransactions = pmjjbyService.getAllPmjjbyTransactions();
        List<PmsbyDTO> pmsbyTransactions = pmsbyService.getAllPmsbyTransactions();
        String currentUser = authentication.getName();                           
        apyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));
        pmjjbyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));
        pmsbyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));

        if (!status.equalsIgnoreCase("All")) {
            apyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            pmjjbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            pmsbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
        }

        model.addAttribute("apyTransactions", apyTransactions);
        model.addAttribute("pmjjbyTransactions", pmjjbyTransactions);
        model.addAttribute("pmsbyTransactions", pmsbyTransactions);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("success", success);
        model.addAttribute("branchName", authentication.getName());
        return "dashboard";
    }

    @GetMapping("/approver_dashboard")
    public String showApproverDashboard(@RequestParam(defaultValue = "All") String status, Model model) {
        try {
            List<ApyDTO> apyTransactions = apyService.getAllApyTransactions();
            List<PmjjbyDTO> pmjjbyTransactions = pmjjbyService.getAllPmjjbyTransactions();
            List<PmsbyDTO> pmsbyTransactions = pmsbyService.getAllPmsbyTransactions();

            if (!status.equalsIgnoreCase("All")) {
                apyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
                pmjjbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
                pmsbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            }

            model.addAttribute("apyTransactions", apyTransactions);
            model.addAttribute("pmjjbyTransactions", pmjjbyTransactions);
            model.addAttribute("pmsbyTransactions", pmsbyTransactions);
            model.addAttribute("selectedStatus", status);
            return "approver_dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/decision")
    public String handleDecision(@RequestParam("scheme") String scheme,
                                 @RequestParam("requestId") Long id,
                                 @RequestParam("action") String action,
                                 @RequestParam("remark") String remark,
                                 @RequestParam(defaultValue = "All") String status,
                                 RedirectAttributes redirectAttributes) {
        boolean updated = updateStatusAndRemark(scheme, id, action, remark);
        if (updated) {
            redirectAttributes.addFlashAttribute("success", true);
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update the transaction.");
        }
        redirectAttributes.addAttribute("status", status);
        return "redirect:/approver_dashboard";
    }

    private boolean updateStatusAndRemark(String scheme, Long id, String newStatus, String remark) {
        switch (scheme.toLowerCase()) {
            case "apy":
                apyService.updateStatusAndRemark(id, newStatus, remark);
                return true;

            case "pmjjby":
                pmjjbyForm pmjj = pmjjbyFormRepository.findById(id).orElse(null);
                if (pmjj != null) {
                    pmjj.setStatus(newStatus);
                    pmjj.setApproverRemark(remark);
                    pmjjbyFormRepository.save(pmjj);
                    return true;
                }
                break;

            case "pmsby":
                pmsbyForm pms = pmsbyFormRepository.findById(id).orElse(null);
                if (pms != null) {
                    pms.setStatus(newStatus);
                    pms.setApproverRemark(remark);
                    pmsbyFormRepository.save(pms);
                    return true;
                }
                break;
        }
        return false;
    }


    @GetMapping("/role-redirect")
    public String redirectBasedOnRole(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MAKER"))) {
            return "redirect:/dashboard";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_APPROVER"))) {
            return "redirect:/approver_dashboard";
        } else {
            return "redirect:/login?error";
        }
    }
}
