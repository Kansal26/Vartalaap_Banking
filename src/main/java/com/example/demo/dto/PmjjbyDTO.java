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
    private String complianceStatus;
    private String approverRemark; 
    private String submittedBy;
    private String additionalData;
    private String consentDocPath;
    private String autoDebitDocPath;
    private String aadharDocPath;
    private String panDocPath;
    private String cancelledChequeDocPath;
    private String supportingDocumentsStatus;

    public PmjjbyDTO() {}

    public PmjjbyDTO(Long requestID, String fullName, int age, String aadharCard, String bankAcc,
                     String schemeName, String status, LocalDate submissionDate, String amount,
                     String complianceStatus, String approverRemark, String submittedBy, String additionalData,
                     String consentDocPath, String autoDebitDocPath, String aadharDocPath, String panDocPath, String cancelledChequeDocPath,
                     String supportingDocumentsStatus) {
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
        this.additionalData = additionalData;
        this.consentDocPath = consentDocPath;
        this.autoDebitDocPath = autoDebitDocPath;
        this.aadharDocPath = aadharDocPath;
        this.panDocPath = panDocPath;
        this.cancelledChequeDocPath = cancelledChequeDocPath;
        this.supportingDocumentsStatus = supportingDocumentsStatus;
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

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getConsentDocPath() {
        return consentDocPath;
    }

    public void setConsentDocPath(String consentDocPath) {
        this.consentDocPath = consentDocPath;
    }

    public String getAutoDebitDocPath() {
        return autoDebitDocPath;
    }

    public void setAutoDebitDocPath(String autoDebitDocPath) {
        this.autoDebitDocPath = autoDebitDocPath;
    }

    public String getAadharDocPath() { return aadharDocPath; }
    public String getPanDocPath() { return panDocPath; }
    public String getCancelledChequeDocPath() { return cancelledChequeDocPath; }
    public String getSupportingDocumentsStatus() { return supportingDocumentsStatus; }
}
