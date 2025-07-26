package com.example.demo.service;

import com.example.demo.dto.ApyDTO;
import com.example.demo.model.apyForm;
import com.example.demo.repository.apyFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApyService {

    @Autowired
    private apyFormRepository apyRepo;

    // ✅ Static contribution rules (systematic, no DB needed)
    private static final List<ContributionRule> apyChart = List.of(
        new ContributionRule(18, 1000, 42.00),
        new ContributionRule(18, 2000, 84.00),
        new ContributionRule(18, 3000, 126.00),
        new ContributionRule(18, 4000, 168.00),
        new ContributionRule(18, 5000, 210.00),

        new ContributionRule(20, 1000, 50.00),
        new ContributionRule(20, 2000, 100.00),
        new ContributionRule(20, 3000, 150.00),
        new ContributionRule(20, 4000, 198.00),
        new ContributionRule(20, 5000, 248.00),

        new ContributionRule(25, 1000, 76.00),
        new ContributionRule(25, 2000, 151.00),
        new ContributionRule(25, 3000, 226.00),
        new ContributionRule(25, 4000, 301.00),
        new ContributionRule(25, 5000, 376.00),

        new ContributionRule(30, 1000, 116.00),
        new ContributionRule(30, 2000, 231.00),
        new ContributionRule(30, 3000, 347.00),
        new ContributionRule(30, 4000, 462.00),
        new ContributionRule(30, 5000, 577.00),

        new ContributionRule(35, 1000, 181.00),
        new ContributionRule(35, 2000, 362.00),
        new ContributionRule(35, 3000, 543.00),
        new ContributionRule(35, 4000, 722.00),
        new ContributionRule(35, 5000, 902.00),

        new ContributionRule(40, 1000, 291.00),
        new ContributionRule(40, 2000, 582.00),
        new ContributionRule(40, 3000, 873.00),
        new ContributionRule(40, 4000, 1154.00),
        new ContributionRule(40, 5000, 1454.00)
    );

    // ✅ Return all APY transactions with compliance status
    public List<ApyDTO> getAllApyTransactions() {
        List<ApyDTO> transactions = new ArrayList<>();
        for (apyForm form : apyRepo.findAll()) {
            String complianceStatus = checkCompliance(form); // apply logic

            transactions.add(new ApyDTO(
                form.getId(),
                form.getFullName(),
                form.getAge(),
                form.getAadharCard(),
                parseContriAmount(form.getContriAmount()),
                form.getPensionTier(),
                form.getSubmissionDate(),
                form.getSchemeName(),
                form.getStatus(),
                complianceStatus,
                form.getApproverRemark(),
                form.getSubmittedBy()
            ));
        }
        return transactions;
    }

    // ✅ Raw access to all forms
    public List<apyForm> getAllApyForms() {
        return apyRepo.findAll();
    }

    // ✅ Compliance check using local chart
    private String checkCompliance(apyForm form) {
        int age = form.getAge();
        Double contriAmount = parseContriAmount(form.getContriAmount());
        Integer pensionAmt = parsePensionAmount(form.getPensionTier());

        if (contriAmount == null || pensionAmt == null) {
            return "Non-Compliant";
        }

        for (ContributionRule rule : apyChart) {
            if (rule.age == age && rule.pensionAmount == pensionAmt) {
                return contriAmount >= rule.requiredContribution ? "Compliant" : "Non-Compliant";
            }
        }

        return "Non-Compliant";
    }

    // ✅ Update status and remark
    public void updateStatusAndRemark(Long id, String status, String remark) {
        apyForm form = apyRepo.findById(id).orElse(null);
        if (form != null) {
            form.setStatus(status);
            form.setApproverRemark(remark);
            apyRepo.save(form);
        }
    }

    // ✅ Parse ₹ string to number
    private Integer parsePensionAmount(String pensionTier) {
        if (pensionTier == null) return null;
        try {
            return Integer.parseInt(pensionTier.replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ✅ Parse contribution safely
    private Double parseContriAmount(String amountStr) {
        try {
            return amountStr != null ? Double.parseDouble(amountStr) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ✅ Inner static rule class (systematic, readable)
    private static class ContributionRule {
        int age;
        int pensionAmount;
        double requiredContribution;

        ContributionRule(int age, int pensionAmount, double requiredContribution) {
            this.age = age;
            this.pensionAmount = pensionAmount;
            this.requiredContribution = requiredContribution;
        }
    }
}
