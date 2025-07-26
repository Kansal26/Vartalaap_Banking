package com.example.demo.dataseed;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createUserIfNotExists("approve", "admin123", "APPROVER");
        createUserIfNotExists("Meerut City", "branch123", "MAKER");
        createUserIfNotExists("Meerut Cantt", "branch123", "MAKER");
        createUserIfNotExists("Manzol", "branch123", "MAKER");

    }

    private void createUserIfNotExists(String username, String rawPassword, String role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            userRepository.save(user);
            System.out.println("✅ User '" + username + "' created.");
        } else {
            System.out.println("ℹ️ User '" + username + "' already exists.");
        }
    }
}
