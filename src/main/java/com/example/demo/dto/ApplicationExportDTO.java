package com.example.demo.dto;

import java.time.LocalDate;

public class ApplicationExportDTO {
    private String schemeName;
    private String customerName;
    private String branchName;
    private String status;
    private String approverRemark;
    private LocalDate submissionDate;

    public ApplicationExportDTO() {}

    public ApplicationExportDTO(String schemeName, String customerName, String branchName, String status, String approverRemark, LocalDate submissionDate) {
        this.schemeName = schemeName;
        this.customerName = customerName;
        this.branchName = branchName;
        this.status = status;
        this.approverRemark = approverRemark;
        this.submissionDate = submissionDate;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }
}
