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
        String subject = "Welcome to Vartaalap Banking Portal - Your Account is Ready";
        String resolvedBranch = (branchName != null && !branchName.isBlank()) ? branchName : "User";
        String body = String.format(
                "Dear %s,\n\n" +
                "Welcome to the Vartaalap Banking family!\n\n" +
                "Our team has successfully created your secure banking portal account. You can now access our digital services, explore banking schemes, and track your applications in real-time.\n\n" +
                "Access Credentials:\n" +
                "Login ID: %s\n" +
                "Temporary Password: %s\n\n" +
                "Important Next Steps:\n" +
                "1. Access the Portal: Navigate to the official bank login page.\n" +
                "2. First Login: Use the credentials provided above to sign in.\n" +
                "3. Password Security: For your protection, you will be required to change your password immediately upon your first login.\n\n" +
                "If you have any questions, please contact our support desk at support@vartaalapbank.com.\n\n" +
                "Welcome aboard!\n\n" +
                "Best regards,\n" +
                "The Vartaalap Administrative Team\n" +
                "Vartaalap Banking Digital Services",
                resolvedBranch, loginId, tempPassword);
        sendEmail(to, subject, body);
    }

    public void sendOtp(String to, String otp) {
        String subject = "Security Alert: Your Password Reset OTP";
        String body = String.format(
                "Hello,\n\n" +
                "We received a request to reset the password for your Vartaalap Banking account.\n\n" +
                "To proceed, please use the following One-Time Password (OTP):\n" +
                "%s\n\n" +
                "Note: This code is sensitive and will expire in 5 minutes. Do not share this code with anyone, including bank officials.\n\n" +
                "If you did not request this reset, please ignore this email or notify our security team immediately.\n\n" +
                "Best regards,\n" +
                "Vartaalap Security Team",
                otp);
        sendEmail(to, subject, body);
    }

    @org.springframework.scheduling.annotation.Async
    public void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            System.out.println("[MOCK EMAIL] To: " + to);
            return;
        }

        // Save to System Outbox (Initially PENDING)
        com.example.demo.model.SystemNotification note = new com.example.demo.model.SystemNotification();
        note.setRecipient(to);
        note.setSubject(subject);
        note.setBody(body);
        note.setStatus("PENDING");
        note.setSentAt(java.time.LocalDateTime.now());
        notificationRepository.save(note);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@bankportal.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            // Update status to SENT
            note.setStatus("SENT");
            notificationRepository.save(note);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();

            // Update status to FAILED
            note.setStatus("FAILED");
            notificationRepository.save(note);

            // Mock fallback log for visibility
            System.out.println("[MOCK EMAIL FALLBACK] To: " + to);
        }
    }
}
