package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class apyForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private int age;
    private String aadharCard;
    private String contriAmount;
    private String pensionTier;
    private String submittedBy;
    private LocalDate submissionDate;
    private String schemeName;
    private String status;

    // ✅ New field to store remark by approver
    private String approverRemark;

    @jakarta.persistence.Column(length = 5000)
    private String additionalData; // Stores JSON of dynamic fields

    private String aadharDocPath;
    private String panDocPath;
    private String cancelledChequeDocPath;

    // Automatically populate default values when called
    public void onCreate() {
        this.submissionDate = LocalDate.now();
        this.schemeName = "Atal Pension Yojana";
        this.status = "Pending";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAadharCard() {
        return aadharCard;
    }

    public void setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
    }

    public String getContriAmount() {
        return contriAmount;
    }

    public void setContriAmount(String contriAmount) {
        this.contriAmount = contriAmount;
    }

    public String getPensionTier() {
        return pensionTier;
    }

    public void setPensionTier(String pensionTier) {
        this.pensionTier = pensionTier;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApproverRemark() {
        return approverRemark;
    }

    public void setApproverRemark(String approverRemark) {
        this.approverRemark = approverRemark;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getAadharDocPath() {
        return aadharDocPath;
    }

    public void setAadharDocPath(String aadharDocPath) {
        this.aadharDocPath = aadharDocPath;
    }

    public String getPanDocPath() {
        return panDocPath;
    }

    public void setPanDocPath(String panDocPath) {
        this.panDocPath = panDocPath;
    }

    public String getCancelledChequeDocPath() {
        return cancelledChequeDocPath;
    }

    public void setCancelledChequeDocPath(String cancelledChequeDocPath) {
        this.cancelledChequeDocPath = cancelledChequeDocPath;
    }
}
