package com.example.demo.repository;

import com.example.demo.model.pmmyForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface pmmyFormRepository extends JpaRepository<pmmyForm, Long> {
}
