package com.example.demo.dto;

import java.time.LocalDate;

public class ApyDTO {
    private Long id;
    private String fullName;
    private Integer age;
    private String aadharCard;
    private Double contriAmount;
    private String pensionTier;
    private LocalDate submissionDate;
    private String schemeName;
    private String status;
    private String complianceStatus;
    private String approverRemark;
    private String submittedBy;
  
    public ApyDTO(Long id, String fullName, Integer age, String aadharCard,
                  Double contriAmount, String pensionTier, LocalDate submissionDate,
                  String schemeName, String status, String complianceStatus, String approverRemark, String submittedBy) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
        this.aadharCard = aadharCard;
        this.contriAmount = contriAmount;
        this.pensionTier = pensionTier;
        this.submissionDate = submissionDate;
        this.schemeName = schemeName;
        this.status = status;
        this.complianceStatus = complianceStatus;
        this.approverRemark = approverRemark;
        this.submittedBy = submittedBy;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getAge() {
        return age;
    }

    public String getAadharCard() {
        return aadharCard;
    }

    public Double getContriAmount() {
        return contriAmount;
    }

    public String getPensionTier() {
        return pensionTier;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public String getStatus() {
        return status;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public String getApproverRemark() {
        return approverRemark;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }
    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

}
