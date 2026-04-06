package com.example.demo.controller;

import com.example.demo.service.SmsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestParam String phoneNumber, HttpSession session) {
        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Store OTP in session
        session.setAttribute("otp", otp);
        session.setAttribute("otp_phone", phoneNumber);

        // Use SmsService to send OTP
        smsService.sendOtp(phoneNumber, otp);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully");
        response.put("otp", otp); // Re-added for testing visibility in browser

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestParam String otp, @RequestParam String phoneNumber,
            HttpSession session) {
        String sessionOtp = (String) session.getAttribute("otp");
        String sessionPhone = (String) session.getAttribute("otp_phone");

        Map<String, Object> response = new HashMap<>();

        if (sessionOtp != null && sessionPhone != null && sessionOtp.equals(otp) && sessionPhone.equals(phoneNumber)) {
            response.put("success", true);
            response.put("message", "OTP verified successfully");
            session.removeAttribute("otp"); // Clear OTP after usage
            session.removeAttribute("otp_phone");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid OTP or Phone Number");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
