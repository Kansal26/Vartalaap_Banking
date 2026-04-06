package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Service
@Primary
@ConditionalOnProperty(name = "sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsService implements SmsService {

    @Override
    public String sendOtp(String phoneNumber, String otp) {
        System.out.println("==================================================");
        System.out.println("MOCK SMS PROVIDER");
        System.out.println("To: " + phoneNumber);
        System.out.println("OTP: " + otp);
        System.out.println("==================================================");
        return "OTP sent (Mock)";
    }
}
