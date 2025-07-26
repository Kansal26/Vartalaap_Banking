package com.example.demo.service;

import com.example.demo.dto.PmsbyDTO;
import com.example.demo.model.pmsbyForm;
import com.example.demo.repository.pmsbyFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PmsbyService {

    @Autowired
    private pmsbyFormRepository pmsbyRepo;

    public List<PmsbyDTO> getAllPmsbyTransactions() {
        List<PmsbyDTO> transactions = new ArrayList<>();
        for (pmsbyForm form : pmsbyRepo.findAll()) {
            String complianceStatus = checkCompliance(form);

            transactions.add(new PmsbyDTO(
                form.getId(),
                form.getFullName(),
                form.getAge(),
                form.getBankAcc(),
                form.getPanCard(),
                form.getAadharCard(),
                form.getSubmissionDate(),
                form.getSchemeName(),
                form.getStatus(),
                form.getAmount(),
                complianceStatus,
                form.getApproverRemark(),
                form.getSubmittedBy()
            ));
        }
        return transactions;
    }

    // ✅ Compliance check logic
    private String checkCompliance(pmsbyForm form) {
        if (form.getAge() < 18 || form.getAge() > 70) return "Non-Compliant";
        if (form.getPanCard() == null || form.getPanCard().isEmpty()) return "Non-Compliant";
        if (form.getAadharCard() == null || form.getAadharCard().isEmpty()) return "Non-Compliant";
        return "Compliant";
    }

    // ✅ Status + Remark update
    public void updateStatusAndRemark(Long id, String status, String remark) {
        pmsbyForm form = pmsbyRepo.findById(id).orElse(null);
        if (form != null) {
            form.setStatus(status);
            form.setApproverRemark(remark);
            pmsbyRepo.save(form);
        }
    }

    // Optional raw access
    public List<pmsbyForm> getAllPmsbyForms() {
        return pmsbyRepo.findAll();
    }
}
