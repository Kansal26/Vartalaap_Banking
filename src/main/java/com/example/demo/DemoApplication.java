package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UserRepository repo, BCryptPasswordEncoder encoder) {
        return args -> {
            User[] users = {
                createUser("admin",      "admin123",      "ADMIN",    "admin@cbi.com",    encoder),
                createUser("maker01",    "maker123",      "USER",    "maker@cbi.com",    encoder),
                createUser("approver01", "approver123",   "APPROVER", "approver@cbi.com", encoder)
            };

            for (User user : users) {
                if (repo.findByUsername(user.getUsername()) == null) {
                    repo.save(user);
                    System.out.println("Created user: " + user.getUsername() + " [" + user.getRole() + "]");
                } else {
                    System.out.println("ℹ️ User '" + user.getUsername() + "' already exists.");
                }
            }
        };
    }

    private User createUser(String username, String rawPassword, String role, String email, BCryptPasswordEncoder encoder) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(role);
        user.setEmail(email);
        return user;
    }
}