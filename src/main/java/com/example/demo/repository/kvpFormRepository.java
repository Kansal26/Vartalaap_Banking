package com.example.demo.repository;

import com.example.demo.model.kvpForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface kvpFormRepository extends JpaRepository<kvpForm, Long> {
}
