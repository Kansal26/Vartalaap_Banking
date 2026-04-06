package com.example.demo.service;

import com.example.demo.dto.PmmyDTO;
import com.example.demo.model.pmmyForm;
import com.example.demo.repository.pmmyFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PmmyService {

    @Autowired
    private pmmyFormRepository pmmyRepo;

    public List<PmmyDTO> getAllPmmyTransactions() {
        List<PmmyDTO> transactions = new ArrayList<>();
        for (pmmyForm form : pmmyRepo.findAll()) {
            String complianceStatus = checkCompliance(form);
            String docStatus = (form.getAadharDocPath() != null && !form.getAadharDocPath().isEmpty() &&
                                form.getPanDocPath() != null && !form.getPanDocPath().isEmpty() &&
                                form.getCancelledChequeDocPath() != null && !form.getCancelledChequeDocPath().isEmpty() &&
                                form.getGeotaggedPhotoPath() != null && !form.getGeotaggedPhotoPath().isEmpty()) 
                                ? "All Present" : "Missing";

            transactions.add(new PmmyDTO(
                    form.getId(),
                    form.getFullName(),
                    form.getAadharCard(),
                    form.getBankAcc(),
                    form.getBusinessType(),
                    form.getLoanAmount(),
                    form.getBusinessAddress(),
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
                    form.getGeotaggedPhotoPath(),
                    docStatus));
        }
        return transactions;
    }

    private String checkCompliance(pmmyForm form) {
        if (form == null) return "Non-Compliant";
        if (form.getAadharCard() == null || form.getAadharCard().length() != 12) return "Non-Compliant (Invalid Aadhaar)";
        
        String amountStr = form.getLoanAmount();
        String type = form.getBusinessType();
        
        if (amountStr == null || amountStr.trim().isEmpty() || type == null) return "Non-Compliant (Missing Data)";
        
        try {
            double amt = Double.parseDouble(amountStr.replaceAll("[^\\d.]", ""));
            String businessTypeLower = type.toLowerCase();
            if (businessTypeLower.equals("shishu") && amt > 50000) return "Non-Compliant (Shishu max ₹50,000)";
            if (businessTypeLower.equals("kishore") && (amt <= 50000 || amt > 500000)) return "Non-Compliant (Kishore ₹50k - ₹5L)";
            if (businessTypeLower.equals("tarun") && (amt <= 500000 || amt > 1000000)) return "Non-Compliant (Tarun ₹5L - ₹10L)";
        } catch (Exception e) {
            return "Non-Compliant (Invalid Amount)";
        }
        return "Compliant";
    }

    public void updateStatusAndRemark(Long id, String status, String remark) {
        if (id == null) return;
        pmmyForm form = pmmyRepo.findById(id).orElse(null);
        if (form != null) {
            form.setStatus(status);
            form.setApproverRemark(remark);
            pmmyRepo.save(form);
        }
    }
}
