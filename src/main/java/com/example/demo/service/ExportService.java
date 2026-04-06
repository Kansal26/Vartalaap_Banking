package com.example.demo.service;

import com.example.demo.dto.ApplicationExportDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private apyFormRepository apyRepo;
    @Autowired
    private pmjjbyFormRepository pmjjbyRepo;
    @Autowired
    private pmsbyFormRepository pmsbyRepo;
    @Autowired
    private kvpFormRepository kvpRepo;
    @Autowired
    private pmmyFormRepository pmmyRepo;
    @Autowired
    private UserRepository userRepo;

    public List<String> getDistinctBranches() {
        return userRepo.findAll().stream()
                .map(User::getBranch)
                .filter(b -> b != null && !b.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<ApplicationExportDTO> getFilteredApplications(String scheme, String branch) {
        Map<String, String> userBranchMap = userRepo.findAll().stream()
                .filter(u -> u.getUsername() != null)
                .collect(Collectors.toMap(User::getUsername, u -> u.getBranch() != null ? u.getBranch() : "Unknown", (a, b) -> a));

        List<ApplicationExportDTO> results = new ArrayList<>();
        boolean allSchemes = scheme == null || "All".equalsIgnoreCase(scheme) || scheme.isBlank();

        if (allSchemes || "APY".equalsIgnoreCase(scheme)) {
            apyRepo.findAll().forEach(f -> addIfMatches(results, "APY", f.getFullName(), f.getSubmittedBy(), f.getStatus(), f.getApproverRemark(), f.getSubmissionDate(), branch, userBranchMap));
        }
        if (allSchemes || "PMJJBY".equalsIgnoreCase(scheme)) {
            pmjjbyRepo.findAll().forEach(f -> addIfMatches(results, "PMJJBY", f.getFullName(), f.getSubmittedBy(), f.getStatus(), f.getApproverRemark(), f.getSubmissionDate(), branch, userBranchMap));
        }
        if (allSchemes || "PMSBY".equalsIgnoreCase(scheme)) {
            pmsbyRepo.findAll().forEach(f -> addIfMatches(results, "PMSBY", f.getFullName(), f.getSubmittedBy(), f.getStatus(), f.getApproverRemark(), f.getSubmissionDate(), branch, userBranchMap));
        }
        if (allSchemes || "KVP".equalsIgnoreCase(scheme)) {
            kvpRepo.findAll().forEach(f -> addIfMatches(results, "KVP", f.getFullName(), f.getSubmittedBy(), f.getStatus(), f.getApproverRemark(), f.getSubmissionDate(), branch, userBranchMap));
        }
        if (allSchemes || "PMMY".equalsIgnoreCase(scheme)) {
            pmmyRepo.findAll().forEach(f -> addIfMatches(results, "PMMY", f.getFullName(), f.getSubmittedBy(), f.getStatus(), f.getApproverRemark(), f.getSubmissionDate(), branch, userBranchMap));
        }

        return results;
    }

    private void addIfMatches(List<ApplicationExportDTO> results, String schemeName, String fullName, String submittedBy, String status, String remark, java.time.LocalDate date, String branchFilter, Map<String, String> userBranchMap) {
        String branch = userBranchMap.getOrDefault(submittedBy, "Unknown");
        if (branchFilter == null || "All".equalsIgnoreCase(branchFilter) || branchFilter.isBlank() || branch.equalsIgnoreCase(branchFilter)) {
            results.add(new ApplicationExportDTO(schemeName, fullName, branch, status, remark, date));
        }
    }

    public List<ApplicationExportDTO> getAllApplications() {
        return getFilteredApplications("All", "All");
    }

    public byte[] generateExcel(List<ApplicationExportDTO> apps) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Applications");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Scheme Name", "Customer Name", "Branch Name", "Status", "Approver Remark", "Submission Date"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Data
            int rowIdx = 1;
            for (ApplicationExportDTO app : apps) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(app.getSchemeName() != null ? app.getSchemeName() : "");
                row.createCell(1).setCellValue(app.getCustomerName() != null ? app.getCustomerName() : "");
                row.createCell(2).setCellValue(app.getBranchName() != null ? app.getBranchName() : "");
                row.createCell(3).setCellValue(app.getStatus() != null ? app.getStatus() : "");
                row.createCell(4).setCellValue(app.getApproverRemark() != null ? app.getApproverRemark() : "");
                row.createCell(5).setCellValue(app.getSubmissionDate() != null ? app.getSubmissionDate().toString() : "");
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateCsv(List<ApplicationExportDTO> apps) {
        StringBuilder csv = new StringBuilder();
        csv.append("Scheme Name,Customer Name,Branch Name,Status,Approver Remark,Submission Date\n");

        for (ApplicationExportDTO app : apps) {
            csv.append(escapeCsv(app.getSchemeName())).append(",")
               .append(escapeCsv(app.getCustomerName())).append(",")
               .append(escapeCsv(app.getBranchName())).append(",")
               .append(escapeCsv(app.getStatus())).append(",")
               .append(escapeCsv(app.getApproverRemark())).append(",")
               .append(escapeCsv(app.getSubmissionDate() != null ? app.getSubmissionDate().toString() : "")).append("\n");
        }
        return csv.toString().getBytes();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
