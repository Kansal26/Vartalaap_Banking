package com.example.demo.service;

import com.example.demo.dto.PmjjbyDTO;
import com.example.demo.model.pmjjbyForm;
import com.example.demo.repository.pmjjbyFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PmjjbyService {

    @Autowired
    private pmjjbyFormRepository pmjjbyRepo;

    // ✅ Fetch all transactions and add compliance + remark
    public List<PmjjbyDTO> getAllPmjjbyTransactions() {
        List<PmjjbyDTO> transactions = new ArrayList<>();
        for (pmjjbyForm form : pmjjbyRepo.findAll()) {
            String complianceStatus = checkCompliance(form);
            transactions.add(new PmjjbyDTO(
                form.getId(),
                form.getFullName(),
                form.getAge(),
                form.getAadharCard(),
                form.getBankAcc(),
                form.getSchemeName(),
                form.getStatus(),
                form.getSubmissionDate(),
                form.getAmount(),
                complianceStatus,
                form.getApproverRemark(),
                form.getSubmittedBy()
            ));
        }
        return transactions;
    }

    // ✅ Logic to determine compliance
    private String checkCompliance(pmjjbyForm form) {
        if (form.getAge() < 18 || form.getAge() > 50) return "Non-Compliant";
        if (form.getAadharCard() == null || form.getAadharCard().isEmpty()) return "Non-Compliant";
        if (form.getBankAcc() == null || form.getBankAcc().isEmpty()) return "Non-Compliant";
        return "Compliant";
    }

    // ✅ To update status and approver remark
    public void updateStatusAndRemark(Long id, String status, String remark) {
        pmjjbyForm form = pmjjbyRepo.findById(id).orElse(null);
        if (form != null) {
            form.setStatus(status);
            form.setApproverRemark(remark);
            pmjjbyRepo.save(form);
        }
    }

    // ✅ Optional raw access
    public List<pmjjbyForm> getAllPmjjbyForms() {
        return pmjjbyRepo.findAll();
    }
}
