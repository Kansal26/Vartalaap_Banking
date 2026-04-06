package com.example.demo.dto;

import java.time.LocalDate;

public class PmmyDTO {
    private Long requestID;
    private String fullName;
    private String aadharCard;
    private String bankAcc;
    private String businessType;
    private String loanAmount;
    private String businessAddress;
    private String mobileNumber;
    private String schemeName;
    private String status;
    private LocalDate submissionDate;
    private String submittedBy;
    private String complianceStatus;
    private String approverRemark;
    private String additionalData;
    private String aadharDocPath;
    private String panDocPath;
    private String cancelledChequeDocPath;
    private String geotaggedPhotoPath;
    private String supportingDocumentsStatus;

    public PmmyDTO() {}

    public PmmyDTO(Long requestID, String fullName, String aadharCard, String bankAcc,
                   String businessType, String loanAmount, String businessAddress,
                   String mobileNumber, String schemeName, String status,
                   LocalDate submissionDate, String complianceStatus, 
                   String approverRemark, String submittedBy, String additionalData,
                   String aadharDocPath, String panDocPath, String cancelledChequeDocPath, String geotaggedPhotoPath,
                   String supportingDocumentsStatus) {
        this.requestID = requestID;
        this.fullName = fullName;
        this.aadharCard = aadharCard;
        this.bankAcc = bankAcc;
        this.businessType = businessType;
        this.loanAmount = loanAmount;
        this.businessAddress = businessAddress;
        this.mobileNumber = mobileNumber;
        this.schemeName = schemeName;
        this.status = status;
        this.submissionDate = submissionDate;
        this.complianceStatus = complianceStatus;
        this.approverRemark = approverRemark;
        this.submittedBy = submittedBy;
        this.additionalData = additionalData;
        this.aadharDocPath = aadharDocPath;
        this.panDocPath = panDocPath;
        this.cancelledChequeDocPath = cancelledChequeDocPath;
        this.geotaggedPhotoPath = geotaggedPhotoPath;
        this.supportingDocumentsStatus = supportingDocumentsStatus;
    }

    // Getters and Setters
    public Long getRequestID() { return requestID; }
    public void setRequestID(Long requestID) { this.requestID = requestID; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAadharCard() { return aadharCard; }
    public void setAadharCard(String aadharCard) { this.aadharCard = aadharCard; }
    public String getBankAcc() { return bankAcc; }
    public void setBankAcc(String bankAcc) { this.bankAcc = bankAcc; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getLoanAmount() { return loanAmount; }
    public void setLoanAmount(String loanAmount) { this.loanAmount = loanAmount; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }
    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
    public String getApproverRemark() { return approverRemark; }
    public void setApproverRemark(String approverRemark) { this.approverRemark = approverRemark; }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getAadharDocPath() { return aadharDocPath; }
    public String getPanDocPath() { return panDocPath; }
    public String getCancelledChequeDocPath() { return cancelledChequeDocPath; }
    public String getGeotaggedPhotoPath() { return geotaggedPhotoPath; }
    public String getSupportingDocumentsStatus() { return supportingDocumentsStatus; }
}
