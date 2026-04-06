package com.example.demo.service;

import com.example.demo.model.TransferRequest;
import com.example.demo.model.User;
import com.example.demo.repository.TransferRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    private TransferRequestRepository transferRepo;

    @Autowired
    private UserRepository userRepo;

    public void createTransferRequest(User employee, String targetBranch, String targetRegion, String replacementName) {
        TransferRequest request = new TransferRequest();
        request.setEmployee(employee);
        request.setCurrentBranch(employee.getBranch());
        request.setCurrentRegion(employee.getRegion());
        request.setTargetBranch(targetBranch);
        request.setTargetRegion(targetRegion);
        request.setReplacementEmployeeName(replacementName);
        request.onCreate();
        transferRepo.save(request);
    }

    public List<TransferRequest> getPendingRequests() {
        return transferRepo.findByStatus("Pending");
    }

    public List<TransferRequest> getMyRequests(User employee) {
        return transferRepo.findByEmployee(employee);
    }

    public void approveRequest(Long requestId) {
        TransferRequest request = transferRepo.findById(requestId).orElse(null);
        if (request != null && "Pending".equals(request.getStatus())) {
            User employee = request.getEmployee();
            // Update employee details
            employee.setBranch(request.getTargetBranch());
            employee.setRegion(request.getTargetRegion());
            userRepo.save(employee);

            request.setStatus("Approved");
            transferRepo.save(request);
        }
    }

    public void rejectRequest(Long requestId) {
        TransferRequest request = transferRepo.findById(requestId).orElse(null);
        if (request != null && "Pending".equals(request.getStatus())) {
            request.setStatus("Rejected");
            transferRepo.save(request);
        }
    }
}
