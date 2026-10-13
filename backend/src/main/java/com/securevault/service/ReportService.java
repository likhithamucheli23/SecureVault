package com.securevault.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import com.securevault.dto.SecurityAnalyticsDTO;
import com.securevault.entity.Credential;
import com.securevault.entity.SecurityEvent;
import com.securevault.entity.User;
import com.securevault.repository.CredentialRepository;
import com.securevault.repository.SecurityEventRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    private final SecurityAnalyticsService securityAnalyticsService;
    private final SecurityEventRepository securityEventRepository;
    private final CredentialRepository credentialRepository;

    public ReportService(SecurityAnalyticsService securityAnalyticsService,
                         SecurityEventRepository securityEventRepository,
                         CredentialRepository credentialRepository) {
        this.securityAnalyticsService = securityAnalyticsService;
        this.securityEventRepository = securityEventRepository;
        this.credentialRepository = credentialRepository;
    }

    public ByteArrayInputStream generatePdfReport(User user, String reportType) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font subFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        document.add(new Paragraph("SecureVault Security Report", titleFont));
        document.add(new Paragraph("Generated for: " + user.getEmail() + " | Type: " + reportType.toUpperCase(), subFont));
        document.add(new Paragraph(" "));

        SecurityAnalyticsDTO analytics = securityAnalyticsService.getSecurityAnalytics(user, "alltime");

        Table table = new Table(2);
        table.addCell("Metric");
        table.addCell("Value");

        table.addCell("Successful Logins");
        table.addCell(String.valueOf(analytics.getSuccessfulLogins()));

        table.addCell("Failed Logins");
        table.addCell(String.valueOf(analytics.getFailedLogins()));

        table.addCell("Suspicious Activities");
        table.addCell(String.valueOf(analytics.getSuspiciousActivities()));

        table.addCell("Active Security Alerts");
        table.addCell(String.valueOf(analytics.getActiveSecurityAlerts()));

        table.addCell("Total Credentials");
        table.addCell(String.valueOf(analytics.getTotalCredentials()));

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Recent Audit Log Events:", titleFont));

        List<SecurityEvent> events = securityEventRepository.findByUserOrderByTimestampDesc(user);
        Table eventTable = new Table(4);
        eventTable.addCell("Timestamp");
        eventTable.addCell("Status");
        eventTable.addCell("IP Address");
        eventTable.addCell("Event Type");

        for (SecurityEvent e : events.stream().limit(10).toList()) {
            eventTable.addCell(String.valueOf(e.getTimestamp()));
            eventTable.addCell(String.valueOf(e.getLoginStatus()));
            eventTable.addCell(e.getIpAddress() != null ? e.getIpAddress() : "N/A");
            eventTable.addCell(e.getEventType() != null ? e.getEventType() : "LOGIN");
        }

        document.add(eventTable);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateExcelReport(User user, String reportType) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Report");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "User Email", "Event Type", "Status", "IP Address", "Timestamp"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            List<SecurityEvent> events = securityEventRepository.findByUserOrderByTimestampDesc(user);
            int rowIdx = 1;
            for (SecurityEvent e : events) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getId() != null ? e.getId() : 0);
                row.createCell(1).setCellValue(e.getAttemptedEmail() != null ? e.getAttemptedEmail() : user.getEmail());
                row.createCell(2).setCellValue(e.getEventType() != null ? e.getEventType() : "SECURITY_LOG");
                row.createCell(3).setCellValue(e.getLoginStatus() != null ? e.getLoginStatus().name() : "N/A");
                row.createCell(4).setCellValue(e.getIpAddress() != null ? e.getIpAddress() : "N/A");
                row.createCell(5).setCellValue(e.getTimestamp() != null ? e.getTimestamp().toString() : "");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
