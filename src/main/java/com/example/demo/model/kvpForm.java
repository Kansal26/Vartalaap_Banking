package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class kvpForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private int age;
    private String aadharCard;
    private String bankAcc;
    private String depositAmount;
    private String nomineeName;
    private String mobileNumber;

    private String schemeName;
    private String status;
    private LocalDate submissionDate;
    private String submittedBy;
    private String approverRemark;

    @Column(length = 5000)
    private String additionalData;

    private String aadharDocPath;
    private String panDocPath;
    private String cancelledChequeDocPath;

    public void onCreate() {
        this.submissionDate = LocalDate.now();
        this.status = "Pending";
        this.schemeName = "Kisan Vikas Patra";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getAadharCard() { return aadharCard; }
    public void setAadharCard(String aadharCard) { this.aadharCard = aadharCard; }
    public String getBankAcc() { return bankAcc; }
    public void setBankAcc(String bankAcc) { this.bankAcc = bankAcc; }
    public String getDepositAmount() { return depositAmount; }
    public void setDepositAmount(String depositAmount) { this.depositAmount = depositAmount; }
    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }
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
    public String getApproverRemark() { return approverRemark; }
    public void setApproverRemark(String approverRemark) { this.approverRemark = approverRemark; }
    public String getAdditionalData() { return additionalData; }
    public void setAdditionalData(String additionalData) { this.additionalData = additionalData; }

    public String getAadharDocPath() { return aadharDocPath; }
    public void setAadharDocPath(String aadharDocPath) { this.aadharDocPath = aadharDocPath; }
    public String getPanDocPath() { return panDocPath; }
    public void setPanDocPath(String panDocPath) { this.panDocPath = panDocPath; }
    public String getCancelledChequeDocPath() { return cancelledChequeDocPath; }
    public void setCancelledChequeDocPath(String cancelledChequeDocPath) { this.cancelledChequeDocPath = cancelledChequeDocPath; }
}
