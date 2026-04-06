package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class MockUidaiService implements UidaiService {

    @Override
    public boolean verifyAadhar(String aadharNumber) {
        // Mock verification logic:
        // 1. Must be exactly 12 digits
        // 2. For demo purposes, we accept any 12-digit number
        // In a real scenario, this would call UIDAI APIs (auth/e-KYC)

        if (aadharNumber == null)
            return false;

        // Simple regex check for 12 digits
        return aadharNumber.matches("\\d{12}");
    }
}
