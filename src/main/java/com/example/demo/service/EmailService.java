package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private com.example.demo.repository.SystemNotificationRepository notificationRepository;

    public void sendTemporaryPassword(String to, String loginId, String tempPassword, String branchName) {
        String subject = "Your Bank Portal Account Details";
        String body = String.format(
                "Dear %s,\n\nYour secure banking portal account has been successfully created.\n\nAccess Credentials:\nLogin ID: %s\nTemporary Password: %s\n\nPlease log in and change your password immediately.",
                (branchName != null && !branchName.isBlank()) ? branchName : "User", loginId, tempPassword);
        sendEmail(to, subject, body);
    }

    public void sendOtp(String to, String otp) {
        String subject = "Password Reset OTP";
        String body = String.format("Your OTP for password reset is: %s\nThis OTP is valid for 5 minutes.", otp);
        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            System.out.println("[MOCK EMAIL] To: " + to);
            System.out.println("[MOCK EMAIL] Subject: " + subject);
            System.out.println("[MOCK EMAIL] Body: " + body);
            return;
        }

        // Save to System Outbox (Always)
        com.example.demo.model.SystemNotification note = new com.example.demo.model.SystemNotification();
        note.setRecipient(to);
        note.setSubject(subject);
        note.setBody(body);
        note.setSentAt(java.time.LocalDateTime.now());
        notificationRepository.save(note);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@bankportal.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Log mock even on failure for demo purposes
            System.out.println("[MOCK EMAIL FALLBACK] To: " + to);
            System.out.println("[MOCK EMAIL FALLBACK] Subject: " + subject);
            System.out.println("[MOCK EMAIL FALLBACK] Body: " + body);
        }
    }
}
