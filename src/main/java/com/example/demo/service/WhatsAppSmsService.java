package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnProperty(name = "sms.provider", havingValue = "whatsapp")
public class WhatsAppSmsService implements SmsService {

    @Value("${whatsapp.access.token}")
    private String accessToken;

    @Value("${whatsapp.phone.number.id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String sendOtp(String phoneNumber, String otp) {
        // Ensure number is in international format (add 91 for India if not present)
        String formattedNumber = phoneNumber.startsWith("91") ? phoneNumber : "91" + phoneNumber;

        String url = "https://graph.facebook.com/v19.0/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            headers.setBearerAuth(accessToken);
        }

        // Send OTP as a plain text WhatsApp message
        String payload = "{"
                + "\"messaging_product\": \"whatsapp\","
                + "\"to\": \"" + formattedNumber + "\","
                + "\"type\": \"text\","
                + "\"text\": {"
                + "  \"body\": \"Your Vartaalap Banking OTP is: *" + otp + "*. Valid for 5 minutes. Do not share this with anyone.\""
                + "}"
                + "}";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, Objects.requireNonNull(HttpMethod.POST), request, String.class);
            System.out.println("WhatsApp OTP sent to " + formattedNumber + " | Response: " + response.getBody());
            return "WhatsApp OTP sent successfully";
        } catch (Exception e) {
            System.err.println("WhatsApp OTP failed: " + e.getMessage());
            return "Failed to send WhatsApp OTP: " + e.getMessage();
        }
    }
}
