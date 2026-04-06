package com.example.demo.controller;

import com.example.demo.model.FormConfig;
import com.example.demo.model.User;
import com.example.demo.repository.FormConfigRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

// TransferService removed as per user request

    @Autowired
    private FormConfigRepository formConfigRepository;

    @Autowired
    private com.example.demo.repository.SystemNotificationRepository notificationRepository;

    @Autowired
    private com.example.demo.service.EmailService emailService;

    @GetMapping
    public String adminDashboard(Model model, java.security.Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);
            model.addAttribute("adminProfile", currentUser);
        }

        List<User> users = userRepository.findAll();
        users.sort((u1, u2) -> u1.getId().compareTo(u2.getId()));
        model.addAttribute("users", users);

        // Static (hardcoded) fields — all of them, enabled or disabled
        model.addAttribute("staticFormConfigs", formConfigRepository.findByStaticField(true));

        // Dynamic (admin-added) fields
        model.addAttribute("formConfigs", formConfigRepository.findByStaticField(false));

        // Pending Transfer Requests removed as per user request

        // System Notifications (Outbox)
        model.addAttribute("notifications", notificationRepository.findAllByOrderBySentAtDesc());

        return "admin_dashboard";
    }

    @PostMapping("/create-user")
    public String createUser(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam(required = false) String branch,
            RedirectAttributes redirectAttributes) {
        if (userRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Login ID (Username) already exists!");
            return "redirect:/admin";
        }
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already exists!");
            return "redirect:/admin";
        }

        // Generate temporary password
        String tempPassword = java.util.UUID.randomUUID().toString().substring(0, 8);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(tempPassword));
        newUser.setRole(role);
        if (branch != null && !branch.isBlank()) {
            newUser.setBranch(branch);
        }
        userRepository.save(newUser);

        // Send email with credentials
        emailService.sendTemporaryPassword(email, username, tempPassword, branch);

        redirectAttributes.addFlashAttribute("success", "User created successfully!");
        redirectAttributes.addFlashAttribute("newUserId", username);
        redirectAttributes.addFlashAttribute("newTempPass", tempPassword);
        redirectAttributes.addFlashAttribute("newUserEmail", email);
        return "redirect:/admin";
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam Long userId,
            @RequestParam String newRole,
            RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin";
        }
        User user = userOpt.get();

        user.setRole(newRole);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Role updated for user: " + user.getUsername());
        return "redirect:/admin";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: deleteUser called for ID: " + userId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin";
        }
        User user = userOpt.get();
        if ("ADMIN".equals(user.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete an ADMIN user!");
            return "redirect:/admin";
        }
        try {
            userRepository.delete(user);
            redirectAttributes.addFlashAttribute("success", "User " + user.getUsername() + " (ID: " + userId + ") deleted successfully!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete user " + user.getUsername() + ": They have associated records (like Transfer Requests) in the system. Use the 'Update Role' feature instead if you want to deactivate them.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "System error while deleting user: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/add-field")
    public String addField(@RequestParam String schemeName,
            @RequestParam String fieldName,
            @RequestParam String fieldLabel,
            @RequestParam String fieldType,
            RedirectAttributes redirectAttributes) {
        FormConfig config = new FormConfig();
        config.setSchemeName(schemeName);
        config.setFieldName(fieldName);
        config.setFieldLabel(fieldLabel);
        config.setFieldType(fieldType);
        config.setStaticField(false);
        config.setEnabled(true);
        formConfigRepository.save(config);
        redirectAttributes.addFlashAttribute("success", "Field added to " + schemeName);
        return "redirect:/admin";
    }

    @PostMapping("/delete-field")
    public String deleteField(@RequestParam Long fieldId, RedirectAttributes redirectAttributes) {
        Optional<FormConfig> configOpt = formConfigRepository.findById(fieldId);
        if (configOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Field not found!");
            return "redirect:/admin";
        }
        FormConfig config = configOpt.get();
        if (config.isStaticField()) {
            // For static fields: disable rather than delete
            config.setEnabled(false);
            formConfigRepository.save(config);
            redirectAttributes.addFlashAttribute("success",
                    "Field \"" + config.getFieldLabel() + "\" removed from form.");
        } else {
            // For dynamic fields: delete permanently
            formConfigRepository.deleteById(fieldId);
            redirectAttributes.addFlashAttribute("success", "Dynamic field deleted.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/enable-field")
    public String enableField(@RequestParam Long fieldId, RedirectAttributes redirectAttributes) {
        FormConfig config = formConfigRepository.findById(fieldId).orElse(null);
        if (config == null) {
            redirectAttributes.addFlashAttribute("error", "Field not found!");
            return "redirect:/admin";
        }
        config.setEnabled(true);
        formConfigRepository.save(config);
        redirectAttributes.addFlashAttribute("success", "Field \"" + config.getFieldLabel() + "\" re-enabled on form.");
        return "redirect:/admin";
    }

    // Transfer request approve/reject handlers removed as per user request
}
