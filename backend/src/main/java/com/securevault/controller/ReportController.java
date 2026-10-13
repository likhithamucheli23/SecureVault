package com.securevault.controller;

import com.securevault.entity.User;
import com.securevault.service.ReportService;
import com.securevault.service.UserService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadReport(
            @RequestParam(defaultValue = "security") String type,
            @RequestParam(defaultValue = "pdf") String format
    ) throws Exception {
        User currentUser = userService.getAuthenticatedUser();

        ByteArrayInputStream stream;
        MediaType mediaType;
        String filename;

        if ("excel".equalsIgnoreCase(format) || "xlsx".equalsIgnoreCase(format)) {
            stream = reportService.generateExcelReport(currentUser, type);
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            filename = "SecureVault_" + type + "_Report.xlsx";
        } else {
            stream = reportService.generatePdfReport(currentUser, type);
            mediaType = MediaType.APPLICATION_PDF;
            filename = "SecureVault_" + type + "_Report.pdf";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(stream));
    }
}
