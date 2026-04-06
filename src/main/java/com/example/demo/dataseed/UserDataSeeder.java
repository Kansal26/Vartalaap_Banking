package com.example.demo.dataseed;

import com.example.demo.model.FormConfig;
import com.example.demo.model.User;
import com.example.demo.repository.FormConfigRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FormConfigRepository formConfigRepository;

    @Override
    public void run(String... args) throws Exception {
        // Admin - Employee ID 0000
        createUserIfNotExists("0000", "admin123", "ADMIN", "System Administrator", "2015", "Corporate Center");

        // Users - Branch Users
        createUserIfNotExists("1001", "branch123", "USER", "Meerut City Branch", "2018", "Meerut City");
        createUserIfNotExists("1002", "branch123", "USER", "Meerut Cantt Branch", "2019", "Meerut Cantt");
        createUserIfNotExists("1003", "branch123", "USER", "Manzol Branch", "2021", "Manzol");

        // Approver
        createUserIfNotExists("2001", "admin123", "APPROVER", "Approver Name", "2020", "Head Office");

        // Manager
        createUserIfNotExists("3001", "manager123", "MANAGER", "Regional Manager", "2016", "Mumbai Region");

        // Seed static form fields for PMJJBY
        seedStaticField("PMJJBY", "fullName", "Customer Name", "text");
        seedStaticField("PMJJBY", "age", "Age", "text");
        seedStaticField("PMJJBY", "aadharCard", "Aadhaar Card Number", "text");
        seedStaticField("PMJJBY", "bankAcc", "Saving Bank Account Number", "text");
        seedStaticField("PMJJBY", "ifscCode", "IFSC Code", "text");
        seedStaticField("PMJJBY", "panCard", "PAN Card Number", "text");
        seedStaticField("PMJJBY", "mobileNumber", "Mobile Number", "text");

        // Seed static form fields for PMSBY
        seedStaticField("PMSBY", "fullName", "Customer Name", "text");
        seedStaticField("PMSBY", "age", "Age", "text");
        seedStaticField("PMSBY", "aadharCard", "Aadhaar Card Number", "text");
        seedStaticField("PMSBY", "bankAcc", "Saving Bank Account Number", "text");
        seedStaticField("PMSBY", "ifscCode", "IFSC Code", "text");
        seedStaticField("PMSBY", "panCard", "PAN Card Number", "text");
        seedStaticField("PMSBY", "mobileNumber", "Mobile Number", "text");

        // Seed static form fields for APY
        seedStaticField("APY", "fullName", "Customer Name", "text");
        seedStaticField("APY", "age", "Age", "text");
        seedStaticField("APY", "aadharCard", "Aadhaar Card Number", "text");
        seedStaticField("APY", "pensionTier", "Pension Tier", "text");
        seedStaticField("APY", "contributionAmount", "Contribution Amount", "number");
        seedStaticField("APY", "mobileNumber", "Mobile Number", "text");

        // Seed static form fields for KVP
        seedStaticField("KVP", "fullName", "Customer Name", "text");
        seedStaticField("KVP", "age", "Age", "text");
        seedStaticField("KVP", "aadharCard", "Aadhaar Card Number", "text");
        seedStaticField("KVP", "bankAcc", "Saving Bank Account Number", "text");
        seedStaticField("KVP", "depositAmount", "Deposit Amount (Min ₹1000)", "number");
        seedStaticField("KVP", "nomineeName", "Nominee Name", "text");
        seedStaticField("KVP", "mobileNumber", "Mobile Number", "text");

        // Seed static form fields for PMMY
        seedStaticField("PMMY", "fullName", "Applicant Name", "text");
        seedStaticField("PMMY", "aadharCard", "Aadhaar Card Number", "text");
        seedStaticField("PMMY", "bankAcc", "Saving Bank Account Number", "text");
        seedStaticField("PMMY", "businessType", "Business Category (Shishu/Kishore/Tarun)", "text");
        seedStaticField("PMMY", "loanAmount", "Required Loan Amount", "number");
        seedStaticField("PMMY", "businessAddress", "Business Address", "text");
        seedStaticField("PMMY", "mobileNumber", "Mobile Number", "text");
    }

    private void seedStaticField(String scheme, String fieldName, String label, String type) {
        Optional<FormConfig> existing = formConfigRepository.findBySchemeNameAndFieldName(scheme, fieldName);
        if (existing.isPresent()) {
            // Ensure the row is correctly marked as static (in case DB had old data)
            FormConfig fc = existing.get();
            if (!fc.isStaticField()) {
                fc.setStaticField(true);
                formConfigRepository.save(fc);
                System.out.println("🔧 Updated static flag for: [" + scheme + "] " + label);
            }
        } else {
            FormConfig fc = new FormConfig();
            fc.setSchemeName(scheme);
            fc.setFieldName(fieldName);
            fc.setFieldLabel(label);
            fc.setFieldType(type);
            fc.setStaticField(true);
            fc.setEnabled(true);
            formConfigRepository.save(fc);
            System.out.println("✅ Static field seeded: [" + scheme + "] " + label);
        }
    }

    private void createUserIfNotExists(String username, String rawPassword, String role, String fullName,
            String yearOfJoining, String branch) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setFullName(fullName);
            user.setYearOfJoining(yearOfJoining);
            user.setBranch(branch);
            userRepository.save(user);
            System.out.println("✅ User '" + username + "' created.");
        } else {
            System.out.println("ℹ️ User '" + username + "' already exists. Updating details.");
            User user = userRepository.findByUsername(username);
            user.setFullName(fullName);
            user.setYearOfJoining(yearOfJoining);
            user.setBranch(branch);
            userRepository.save(user);
        }
    }
}
