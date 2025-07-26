package com.example.demo.dto;

import java.time.LocalDate;

public class PmsbyDTO {
    private Long requestID;
    private String fullName;
    private int age;
    private String bankAcc;
    private String panCard;
    private String aadharCard;
    private LocalDate submissionDate;
    private String schemeName;
    private String status;
    private String amount;
    private String complianceStatus;
    private String approverRemark;
    private String submittedBy;
    public PmsbyDTO() {}

    public PmsbyDTO(Long requestID, String fullName, int age, String bankAcc, String panCard,
                    String aadharCard, LocalDate submissionDate, String schemeName,
                    String status, String amount, String complianceStatus, String approverRemark, String submittedBy) {
        this.requestID = requestID;
        this.fullName = fullName;
        this.age = age;
        this.bankAcc = bankAcc;
        this.panCard = panCard;
        this.aadharCard = aadharCard;
        this.submissionDate = submissionDate;
        this.schemeName = schemeName;
        this.status = status;
        this.amount = amount;
        this.complianceStatus = complianceStatus;
        this.approverRemark = approverRemark;
        this.submittedBy = submittedBy;
    }

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
