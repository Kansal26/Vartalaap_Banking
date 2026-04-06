package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            @RequestParam String currentPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "No account found with this email address.");
            return "forgot_password";
        }

        // Validate current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect. Please try again.");
            return "forgot_password";
        }

        // Generate and send OTP
        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp);

        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtp(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "verify_otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam String email, @RequestParam String otp,
            RedirectAttributes redirectAttributes, Model model) {
        if (otpService.validateOtp(email, otp)) {
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired OTP!");
            model.addAttribute("email", email);
            return "verify_otp";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Double-check passwords match server-side
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match. Please try again.");
            model.addAttribute("email", email);
            return "reset_password";
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "redirect:/forgot-password";
        }

        // Ensure new password is different from current
        if (passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "New password must be different from your current password.");
            model.addAttribute("email", email);
            return "reset_password";
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Password updated successfully! You can now log in with your new password.");
        return "redirect:/login";
    }
}
