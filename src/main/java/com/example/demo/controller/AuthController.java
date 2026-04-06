package com.example.demo.controller;

import com.example.demo.dto.ApyDTO;
import com.example.demo.dto.PmjjbyDTO;
import com.example.demo.dto.PmsbyDTO;
import com.example.demo.dto.KvpDTO;
import com.example.demo.dto.PmmyDTO;
import com.example.demo.model.FormConfig;
import com.example.demo.model.apyForm;
import com.example.demo.model.pmjjbyForm;
import com.example.demo.model.pmsbyForm;
import com.example.demo.model.kvpForm;
import com.example.demo.model.pmmyForm;
import com.example.demo.repository.apyFormRepository;
import com.example.demo.repository.pmjjbyFormRepository;
import com.example.demo.repository.pmsbyFormRepository;
import com.example.demo.repository.kvpFormRepository;
import com.example.demo.repository.pmmyFormRepository;
import com.example.demo.service.ApyService;
import com.example.demo.service.PmjjbyService;
import com.example.demo.service.PmsbyService;
import com.example.demo.service.KvpService;
import com.example.demo.service.PmmyService;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private kvpFormRepository kvpFormRepository;

    @Autowired
    private pmmyFormRepository pmmyFormRepository;

    @Autowired
    private ApyService apyService;

    @Autowired
    private PmjjbyService pmjjbyService;

    @Autowired
    private PmsbyService pmsbyService;

    @Autowired
    private KvpService kvpService;

    @Autowired
    private PmmyService pmmyService;

    @Autowired
    private com.example.demo.repository.FormConfigRepository formConfigRepository;

    @Autowired
    private UserRepository userRepository;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/apy")
    public String apy(Model model) {
        model.addAttribute("form", new apyForm());
        // Only dynamic (non-static) fields rendered via th:each
        model.addAttribute("formConfigs", formConfigRepository.findBySchemeNameAndStaticField("APY", false));
        // Set of fieldNames for enabled static fields — used to conditionally render
        // hardcoded fields
        Set<String> enabledFields = formConfigRepository.findBySchemeNameAndStaticFieldAndEnabled("APY", true, true)
                .stream().map(FormConfig::getFieldName).collect(Collectors.toSet());
        model.addAttribute("enabledFields", enabledFields);
        return "apy";
    }

    @PostMapping("/submitApy")
    public String handleApySubmit(@ModelAttribute("form") apyForm form,
            @RequestParam java.util.Map<String, String> allParams,
            @RequestParam(value = "aadharDoc", required = false) MultipartFile aadharDoc,
            @RequestParam(value = "panDoc", required = false) MultipartFile panDoc,
            @RequestParam(value = "cancelledChequeDoc", required = false) MultipartFile cancelledChequeDoc,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        processDynamicFields("APY", allParams, form);

        if (aadharDoc != null && !aadharDoc.isEmpty()) { form.setAadharDocPath(saveFile(aadharDoc)); }
        if (panDoc != null && !panDoc.isEmpty()) { form.setPanDocPath(saveFile(panDoc)); }
        if (cancelledChequeDoc != null && !cancelledChequeDoc.isEmpty()) { form.setCancelledChequeDocPath(saveFile(cancelledChequeDoc)); }

        apyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/pmjjby")
    public String pmjjby(Model model) {
        model.addAttribute("form", new pmjjbyForm());
        // Only dynamic (non-static) fields rendered via th:each
        model.addAttribute("formConfigs", formConfigRepository.findBySchemeNameAndStaticField("PMJJBY", false));
        // Set of fieldNames for enabled static fields — used to conditionally render
        // hardcoded fields
        Set<String> enabledFields = formConfigRepository.findBySchemeNameAndStaticFieldAndEnabled("PMJJBY", true, true)
                .stream().map(FormConfig::getFieldName).collect(Collectors.toSet());
        model.addAttribute("enabledFields", enabledFields);
        return "pmjjby";
    }

    @PostMapping("/submitPmjjby")
    public String handlePmjjbySubmit(
            @ModelAttribute("form") pmjjbyForm form,
            @RequestParam java.util.Map<String, String> allParams,
            @RequestParam("consentDoc") MultipartFile consentDoc,
            @RequestParam("autoDebitDoc") MultipartFile autoDebitDoc,
            @RequestParam(value = "aadharDoc", required = false) MultipartFile aadharDoc,
            @RequestParam(value = "panDoc", required = false) MultipartFile panDoc,
            @RequestParam(value = "cancelledChequeDoc", required = false) MultipartFile cancelledChequeDoc,
            RedirectAttributes redirectAttributes, Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        processDynamicFields("PMJJBY", allParams, form);

        if (!consentDoc.isEmpty()) { form.setConsentDocPath(saveFile(consentDoc)); }
        if (!autoDebitDoc.isEmpty()) { form.setAutoDebitDocPath(saveFile(autoDebitDoc)); }
        if (aadharDoc != null && !aadharDoc.isEmpty()) { form.setAadharDocPath(saveFile(aadharDoc)); }
        if (panDoc != null && !panDoc.isEmpty()) { form.setPanDocPath(saveFile(panDoc)); }
        if (cancelledChequeDoc != null && !cancelledChequeDoc.isEmpty()) { form.setCancelledChequeDocPath(saveFile(cancelledChequeDoc)); }

        pmjjbyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/pmsby")
    public String pmsby(Model model) {
        model.addAttribute("form", new pmsbyForm());
        // Only dynamic (non-static) fields rendered via th:each
        model.addAttribute("formConfigs", formConfigRepository.findBySchemeNameAndStaticField("PMSBY", false));
        // Set of fieldNames for enabled static fields — used to conditionally render
        // hardcoded fields
        Set<String> enabledFields = formConfigRepository.findBySchemeNameAndStaticFieldAndEnabled("PMSBY", true, true)
                .stream().map(FormConfig::getFieldName).collect(Collectors.toSet());
        model.addAttribute("enabledFields", enabledFields);
        return "pmsby";
    }

    @PostMapping("/submitPmsby")
    public String handlePmsbySubmit(@ModelAttribute("form") pmsbyForm form,
            @RequestParam java.util.Map<String, String> allParams,
            @RequestParam("consentDoc") MultipartFile consentDoc,
            @RequestParam("autoDebitDoc") MultipartFile autoDebitDoc,
            @RequestParam(value = "aadharDoc", required = false) MultipartFile aadharDoc,
            @RequestParam(value = "panDoc", required = false) MultipartFile panDoc,
            @RequestParam(value = "cancelledChequeDoc", required = false) MultipartFile cancelledChequeDoc,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        processDynamicFields("PMSBY", allParams, form);

        if (!consentDoc.isEmpty()) { form.setConsentDocPath(saveFile(consentDoc)); }
        if (!autoDebitDoc.isEmpty()) { form.setAutoDebitDocPath(saveFile(autoDebitDoc)); }
        if (aadharDoc != null && !aadharDoc.isEmpty()) { form.setAadharDocPath(saveFile(aadharDoc)); }
        if (panDoc != null && !panDoc.isEmpty()) { form.setPanDocPath(saveFile(panDoc)); }
        if (cancelledChequeDoc != null && !cancelledChequeDoc.isEmpty()) { form.setCancelledChequeDocPath(saveFile(cancelledChequeDoc)); }

        pmsbyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/kvp")
    public String kvp(Model model) {
        model.addAttribute("form", new kvpForm());
        model.addAttribute("formConfigs", formConfigRepository.findBySchemeNameAndStaticField("KVP", false));
        Set<String> enabledFields = formConfigRepository.findBySchemeNameAndStaticFieldAndEnabled("KVP", true, true)
                .stream().map(FormConfig::getFieldName).collect(Collectors.toSet());
        model.addAttribute("enabledFields", enabledFields);
        return "kvp";
    }

    @PostMapping("/submitKvp")
    public String handleKvpSubmit(@ModelAttribute("form") kvpForm form,
                                  @RequestParam java.util.Map<String, String> allParams,
                                  @RequestParam(value = "aadharDoc", required = false) MultipartFile aadharDoc,
                                  @RequestParam(value = "panDoc", required = false) MultipartFile panDoc,
                                  @RequestParam(value = "cancelledChequeDoc", required = false) MultipartFile cancelledChequeDoc,
                                  RedirectAttributes redirectAttributes,
                                  Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        processDynamicFields("KVP", allParams, form);

        if (aadharDoc != null && !aadharDoc.isEmpty()) { form.setAadharDocPath(saveFile(aadharDoc)); }
        if (panDoc != null && !panDoc.isEmpty()) { form.setPanDocPath(saveFile(panDoc)); }
        if (cancelledChequeDoc != null && !cancelledChequeDoc.isEmpty()) { form.setCancelledChequeDocPath(saveFile(cancelledChequeDoc)); }

        kvpFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    @GetMapping("/pmmy")
    public String pmmy(Model model) {
        model.addAttribute("form", new pmmyForm());
        model.addAttribute("formConfigs", formConfigRepository.findBySchemeNameAndStaticField("PMMY", false));
        Set<String> enabledFields = formConfigRepository.findBySchemeNameAndStaticFieldAndEnabled("PMMY", true, true)
                .stream().map(FormConfig::getFieldName).collect(Collectors.toSet());
        model.addAttribute("enabledFields", enabledFields);
        return "pmmy";
    }

    @PostMapping("/submitPmmy")
    public String handlePmmySubmit(@ModelAttribute("form") pmmyForm form,
                                   @RequestParam java.util.Map<String, String> allParams,
                                   @RequestParam(value = "aadharDoc", required = false) MultipartFile aadharDoc,
                                   @RequestParam(value = "panDoc", required = false) MultipartFile panDoc,
                                   @RequestParam(value = "cancelledChequeDoc", required = false) MultipartFile cancelledChequeDoc,
                                   @RequestParam(value = "geotaggedPhoto", required = false) MultipartFile geotaggedPhoto,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) {
        form.setSubmittedBy(authentication.getName());
        form.onCreate();
        processDynamicFields("PMMY", allParams, form);

        if (aadharDoc != null && !aadharDoc.isEmpty()) { form.setAadharDocPath(saveFile(aadharDoc)); }
        if (panDoc != null && !panDoc.isEmpty()) { form.setPanDocPath(saveFile(panDoc)); }
        if (cancelledChequeDoc != null && !cancelledChequeDoc.isEmpty()) { form.setCancelledChequeDocPath(saveFile(cancelledChequeDoc)); }
        if (geotaggedPhoto != null && !geotaggedPhoto.isEmpty()) { form.setGeotaggedPhotoPath(saveFile(geotaggedPhoto)); }

        pmmyFormRepository.save(form);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/dashboard";
    }

    private void processDynamicFields(String schemeName, java.util.Map<String, String> allParams, Object form) {
        java.util.Map<String, String> dynamicData = new java.util.HashMap<>();
        List<com.example.demo.model.FormConfig> configs = formConfigRepository.findBySchemeName(schemeName);
        for (com.example.demo.model.FormConfig config : configs) {
            if (allParams.containsKey(config.getFieldName())) {
                dynamicData.put(config.getFieldLabel(), allParams.get(config.getFieldName()));
            }
        }
        try {
            if (!dynamicData.isEmpty()) {
                String json = objectMapper.writeValueAsString(dynamicData);
                if (form instanceof kvpForm) ((kvpForm) form).setAdditionalData(json);
                else if (form instanceof pmmyForm) ((pmmyForm) form).setAdditionalData(json);
                else if (form instanceof apyForm) ((apyForm) form).setAdditionalData(json);
                else if (form instanceof pmjjbyForm) ((pmjjbyForm) form).setAdditionalData(json);
                else if (form instanceof pmsbyForm) ((pmsbyForm) form).setAdditionalData(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "All") String status,
            Model model,
            Authentication authentication,
            @ModelAttribute("success") Object success) {
        List<ApyDTO> apyTransactions = apyService.getAllApyTransactions();
        List<PmjjbyDTO> pmjjbyTransactions = pmjjbyService.getAllPmjjbyTransactions();
        List<PmsbyDTO> pmsbyTransactions = pmsbyService.getAllPmsbyTransactions();
        List<KvpDTO> kvpTransactions = kvpService.getAllKvpTransactions();
        List<PmmyDTO> pmmyTransactions = pmmyService.getAllPmmyTransactions();

        String currentUser = authentication.getName();
        apyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));
        pmjjbyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));
        pmsbyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));
        kvpTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));
        pmmyTransactions.removeIf(txn -> txn.getSubmittedBy() == null || !txn.getSubmittedBy().equals(currentUser));

        if (!status.equalsIgnoreCase("All")) {
            apyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            pmjjbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            pmsbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            kvpTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            pmmyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
        }

        model.addAttribute("apyTransactions", apyTransactions);
        model.addAttribute("pmjjbyTransactions", pmjjbyTransactions);
        model.addAttribute("pmsbyTransactions", pmsbyTransactions);
        model.addAttribute("kvpTransactions", kvpTransactions);
        model.addAttribute("pmmyTransactions", pmmyTransactions);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("success", success);

        // Fetch User and set Branch Name
        User user = userRepository.findByUsername(currentUser);
        String branchName = (user != null) ? user.getBranch() : currentUser;
        model.addAttribute("branchName", branchName);

        return "dashboard";
    }

    @GetMapping("/approver_dashboard")
    public String showApproverDashboard(@RequestParam(defaultValue = "All") String status, Model model) {
        try {
            List<ApyDTO> apyTransactions = apyService.getAllApyTransactions();
            List<PmjjbyDTO> pmjjbyTransactions = pmjjbyService.getAllPmjjbyTransactions();
            List<PmsbyDTO> pmsbyTransactions = pmsbyService.getAllPmsbyTransactions();
            List<KvpDTO> kvpTransactions = kvpService.getAllKvpTransactions();
            List<PmmyDTO> pmmyTransactions = pmmyService.getAllPmmyTransactions();

            if (!status.equalsIgnoreCase("All")) {
                apyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
                pmjjbyTransactions
                        .removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
                pmsbyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
                kvpTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
                pmmyTransactions.removeIf(txn -> txn.getStatus() == null || !txn.getStatus().equalsIgnoreCase(status));
            }

            model.addAttribute("apyTransactions", apyTransactions);
            model.addAttribute("pmjjbyTransactions", pmjjbyTransactions);
            model.addAttribute("pmsbyTransactions", pmsbyTransactions);
            model.addAttribute("kvpTransactions", kvpTransactions);
            model.addAttribute("pmmyTransactions", pmmyTransactions);
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
        if (id == null) return false;
        switch (scheme.toLowerCase()) {
            case "apy":
                apyService.updateStatusAndRemark(id, newStatus, remark);
                return true;
            case "pmjjby":
                pmjjbyService.updateStatusAndRemark(id, newStatus, remark);
                return true;
            case "pmsby":
                pmsbyService.updateStatusAndRemark(id, newStatus, remark);
                return true;
            case "kvp":
                kvpService.updateStatusAndRemark(id, newStatus, remark);
                return true;
            case "pmmy":
                pmmyService.updateStatusAndRemark(id, newStatus, remark);
                return true;
        }
        return false;
    }

    @GetMapping("/role-redirect")
    public String redirectBasedOnRole(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER") || a.getAuthority().equals("ROLE_MAKER"))) {
            return "redirect:/dashboard";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_APPROVER"))) {
            return "redirect:/approver_dashboard";
        } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            return "redirect:/manager_dashboard";
        } else {
            return "redirect:/login?error";
        }
    }
}
