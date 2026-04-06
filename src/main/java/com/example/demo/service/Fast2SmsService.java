package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@ConditionalOnProperty(name = "sms.provider", havingValue = "fast2sms")
public class Fast2SmsService implements SmsService {

    @Value("${fast2sms.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String sendOtp(String phoneNumber, String otp) {
        String url = "https://www.fast2sms.com/dev/bulkV2";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", apiKey);

        // Fast2SMS Quick SMS route — works without website verification
        String payload = String.format(
            "{\"route\":\"q\",\"message\":\"Your Vartaalap Banking OTP is: %s. Valid for 5 minutes. Do not share with anyone.\",\"language\":\"english\",\"numbers\":\"%s\"}",
            otp, phoneNumber);

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            return "Fast2SMS Response: " + response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send SMS: " + e.getMessage();
        }
    }
}
