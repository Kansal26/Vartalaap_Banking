package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsByUsername(String username);

    User findByEmail(String email);

    boolean existsByEmail(String email);
    
    List<User> findByBranch(String branch);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT u.branch) FROM User u WHERE u.branch IS NOT NULL")
    long countDistinctBranches();
}