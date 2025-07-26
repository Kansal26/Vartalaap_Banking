package com.example.demo.dto;

import java.time.LocalDate;

public class PmjjbyDTO {
    private Long requestID;
    private String fullName;
    private int age;
    private String aadharCard;
    private String bankAcc;
    private String schemeName;
    private String status;
    private LocalDate submissionDate;
    private String amount;
    private String submittedBy;
    private String complianceStatus; 
    private String approverRemark; 

    public PmjjbyDTO() {}

    public PmjjbyDTO(Long requestID, String fullName, int age, String aadharCard, String bankAcc,
                     String schemeName, String status, LocalDate submissionDate, String amount,
                     String complianceStatus, String approverRemark, String submittedBy) {
        this.requestID = requestID;
        this.fullName = fullName;
        this.age = age;
        this.aadharCard = aadharCard;
        this.bankAcc = bankAcc;
        this.schemeName = schemeName;
        this.status = status;
        this.submissionDate = submissionDate;
        this.amount = amount;
        this.complianceStatus = complianceStatus;
        this.approverRemark = approverRemark;
        this.submittedBy=submittedBy;
    }

    // Getters and Setters
    public Long getRequestID() {
        return requestID;
    }

    public void setRequestID(Long requestID) {
        this.requestID = requestID;
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

    public String getBankAcc() {
        return bankAcc;
    }

    public void setBankAcc(String bankAcc) {
        this.bankAcc = bankAcc;
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

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

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
