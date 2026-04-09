package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "form_config")
public class FormConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schemeName; // APY, PMJJBY, PMSBY
    private String fieldName; // camelCase name for input
    private String fieldLabel; // Display label
    private String fieldType; // text, number, date

    // true = seeded/hardcoded field, false = admin-added dynamic field
    @Column(name = "is_static")
    private boolean staticField = false;

    // true = field is rendered in the form, false = admin has disabled it
    private boolean enabled = true;

    // the bank this config belongs to (null for static/global fields)
    private String branch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isStaticField() {
        return staticField;
    }

    public void setStaticField(boolean staticField) {
        this.staticField = staticField;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
