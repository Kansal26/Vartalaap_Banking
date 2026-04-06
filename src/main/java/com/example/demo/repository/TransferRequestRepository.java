package com.example.demo.repository;

import com.example.demo.model.TransferRequest;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {
    List<TransferRequest> findByStatus(String status);

    List<TransferRequest> findByEmployee(User employee);
}
