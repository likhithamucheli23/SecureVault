package com.securevault.dto;

import com.securevault.entity.LoginStatus;
import java.time.LocalDateTime;

public class SecurityEventDTO {
    private Long id;
    private String attemptedEmail;
    private LocalDateTime timestamp;
    private LoginStatus loginStatus;
    private String ipAddress;
    private String userAgent;
    private String eventType;
    private String failureReason;

    public SecurityEventDTO() {}

    public SecurityEventDTO(Long id, String attemptedEmail, LocalDateTime timestamp, LoginStatus loginStatus, String ipAddress, String userAgent, String eventType, String failureReason) {
        this.id = id;
        this.attemptedEmail = attemptedEmail;
        this.timestamp = timestamp;
        this.loginStatus = loginStatus;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.eventType = eventType;
        this.failureReason = failureReason;
    }

    public static SecurityEventDTOBuilder builder() {
        return new SecurityEventDTOBuilder();
    }

    public static class SecurityEventDTOBuilder {
        private Long id;
        private String attemptedEmail;
        private LocalDateTime timestamp;
        private LoginStatus loginStatus;
        private String ipAddress;
        private String userAgent;
        private String eventType;
        private String failureReason;

        public SecurityEventDTOBuilder id(Long id) { this.id = id; return this; }
        public SecurityEventDTOBuilder attemptedEmail(String attemptedEmail) { this.attemptedEmail = attemptedEmail; return this; }
        public SecurityEventDTOBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public SecurityEventDTOBuilder loginStatus(LoginStatus loginStatus) { this.loginStatus = loginStatus; return this; }
        public SecurityEventDTOBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public SecurityEventDTOBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public SecurityEventDTOBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public SecurityEventDTOBuilder failureReason(String failureReason) { this.failureReason = failureReason; return this; }

        public SecurityEventDTO build() {
            return new SecurityEventDTO(id, attemptedEmail, timestamp, loginStatus, ipAddress, userAgent, eventType, failureReason);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAttemptedEmail() { return attemptedEmail; }
    public void setAttemptedEmail(String attemptedEmail) { this.attemptedEmail = attemptedEmail; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public LoginStatus getLoginStatus() { return loginStatus; }
    public void setLoginStatus(LoginStatus loginStatus) { this.loginStatus = loginStatus; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
