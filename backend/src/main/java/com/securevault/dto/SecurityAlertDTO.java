package com.securevault.dto;

import java.time.LocalDateTime;

public class SecurityAlertDTO {
    private String id;
    private String severity; // INFO, WARNING, HIGH, CRITICAL
    private String title;
    private String message;
    private LocalDateTime timestamp;

    public SecurityAlertDTO() {}

    public SecurityAlertDTO(String id, String severity, String title, String message, LocalDateTime timestamp) {
        this.id = id;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static SecurityAlertDTOBuilder builder() {
        return new SecurityAlertDTOBuilder();
    }

    public static class SecurityAlertDTOBuilder {
        private String id;
        private String severity;
        private String title;
        private String message;
        private LocalDateTime timestamp;

        public SecurityAlertDTOBuilder id(String id) { this.id = id; return this; }
        public SecurityAlertDTOBuilder severity(String severity) { this.severity = severity; return this; }
        public SecurityAlertDTOBuilder title(String title) { this.title = title; return this; }
        public SecurityAlertDTOBuilder message(String message) { this.message = message; return this; }
        public SecurityAlertDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public SecurityAlertDTO build() {
            return new SecurityAlertDTO(id, severity, title, message, timestamp);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
