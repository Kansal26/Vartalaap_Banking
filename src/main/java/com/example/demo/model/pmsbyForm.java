package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class pmsbyForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private int age;
    private String bankAcc;
    private String panCard;
    private String aadharCard;
    private LocalDate submissionDate;
    private String schemeName;
    private String status;
    private String amount;
    private String submittedBy;

    // ✅ New field: Approver remark
    private String approverRemark;

    // Auto-populate values when form is created
    public void onCreate() {
        this.submissionDate = LocalDate.now();
        this.status = "Pending";
        this.schemeName = "Pradhan Mantri Suraksha Bima Yojana";
        this.amount = "20"; // Annual premium for PMSBY
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

    public String getBankAcc() {
        return bankAcc;
    }
    public void setBankAcc(String bankAcc) {
        this.bankAcc = bankAcc;
    }

    public String getPanCard() {
        return panCard;
    }
    public void setPanCard(String panCard) {
        this.panCard = panCard;
    }

    public String getAadharCard() {
        return aadharCard;
    }
    public void setAadharCard(String aadharCard) {
        this.aadharCard = aadharCard;
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

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    // ✅ Getter and setter for approver remark
    public String getApproverRemark() {
        return approverRemark;
    }
    public void setApproverRemark(String approverRemark) {
        this.approverRemark = approverRemark;
    }
    public String getSubmittedBy() {
        return submittedBy;
    }
    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }
}
