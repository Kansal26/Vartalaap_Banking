package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final long OTP_EXPIRY = TimeUnit.MINUTES.toMillis(5);
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStorage.put(email, new OtpData(otp, System.currentTimeMillis() + OTP_EXPIRY));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpStorage.get(email);
        if (data == null)
            return false;

        if (System.currentTimeMillis() > data.expiryTime) {
            otpStorage.remove(email);
            return false;
        }

        boolean isValid = data.otp.equals(otp);
        if (isValid) {
            otpStorage.remove(email);
        }
        return isValid;
    }

    private static class OtpData {
        String otp;
        long expiryTime;

        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
