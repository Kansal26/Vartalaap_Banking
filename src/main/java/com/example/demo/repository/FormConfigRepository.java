package com.example.demo.repository;

import com.example.demo.model.FormConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FormConfigRepository extends JpaRepository<FormConfig, Long> {
    List<FormConfig> findBySchemeName(String schemeName);

    List<FormConfig> findByStaticField(boolean staticField);

    List<FormConfig> findBySchemeNameAndStaticField(String schemeName, boolean staticField);

    List<FormConfig> findBySchemeNameAndStaticFieldAndEnabled(String schemeName, boolean staticField, boolean enabled);

    Optional<FormConfig> findBySchemeNameAndFieldName(String schemeName, String fieldName);
}
