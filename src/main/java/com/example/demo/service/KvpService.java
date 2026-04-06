package com.example.demo.service;

import com.example.demo.dto.KvpDTO;
import com.example.demo.model.kvpForm;
import com.example.demo.repository.kvpFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KvpService {

    @Autowired
    private kvpFormRepository kvpRepo;

    public List<KvpDTO> getAllKvpTransactions() {
        List<KvpDTO> transactions = new ArrayList<>();
        for (kvpForm form : kvpRepo.findAll()) {
            String complianceStatus = checkCompliance(form);
            String docStatus = (form.getAadharDocPath() != null && !form.getAadharDocPath().isEmpty() &&
                                form.getPanDocPath() != null && !form.getPanDocPath().isEmpty() &&
                                form.getCancelledChequeDocPath() != null && !form.getCancelledChequeDocPath().isEmpty()) 
                                ? "All Present" : "Missing";

            transactions.add(new KvpDTO(
                    form.getId(),
                    form.getFullName(),
                    form.getAge(),
                    form.getAadharCard(),
                    form.getBankAcc(),
                    form.getDepositAmount(),
                    form.getNomineeName(),
                    form.getMobileNumber(),
                    form.getSchemeName(),
                    form.getStatus(),
                    form.getSubmissionDate(),
                    complianceStatus,
                    form.getApproverRemark(),
                    form.getSubmittedBy(),
                    form.getAdditionalData(),
                    form.getAadharDocPath(),
                    form.getPanDocPath(),
                    form.getCancelledChequeDocPath(),
                    docStatus));
        }
        return transactions;
    }

    private String checkCompliance(kvpForm form) {
        if (form == null) return "Non-Compliant";
        if (form.getAge() < 18) return "Non-Compliant (Minor)";
        if (form.getAadharCard() == null || form.getAadharCard().length() != 12) return "Non-Compliant (Invalid Aadhaar)";
        
        String amountStr = form.getDepositAmount();
        if (amountStr == null || amountStr.trim().isEmpty()) return "Non-Compliant (Missing Amount)";
        
        try {
            double amt = Double.parseDouble(amountStr.replaceAll("[^\\d.]", ""));
            if (amt < 1000) return "Non-Compliant (Min ₹1000)";
        } catch (Exception e) {
            return "Non-Compliant (Invalid Amount)";
        }
        return "Compliant";
    }

    public void updateStatusAndRemark(Long id, String status, String remark) {
        if (id == null) return;
        kvpForm form = kvpRepo.findById(id).orElse(null);
        if (form != null) {
            form.setStatus(status);
            form.setApproverRemark(remark);
            kvpRepo.save(form);
        }
    }
}
