package com.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_events")
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "attempted_email", nullable = false)
    private String attemptedEmail;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status", nullable = false)
    private LoginStatus loginStatus;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "failure_reason")
    private String failureReason;

    public SecurityEvent() {}

    public SecurityEvent(Long id, User user, String attemptedEmail, LocalDateTime timestamp, LoginStatus loginStatus, String ipAddress, String userAgent, String eventType, String failureReason) {
        this.id = id;
        this.user = user;
        this.attemptedEmail = attemptedEmail;
        this.timestamp = timestamp;
        this.loginStatus = loginStatus;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.eventType = eventType;
        this.failureReason = failureReason;
    }

    public static SecurityEventBuilder builder() {
        return new SecurityEventBuilder();
    }

    public static class SecurityEventBuilder {
        private Long id;
        private User user;
        private String attemptedEmail;
        private LocalDateTime timestamp;
        private LoginStatus loginStatus;
        private String ipAddress;
        private String userAgent;
        private String eventType;
        private String failureReason;

        public SecurityEventBuilder id(Long id) { this.id = id; return this; }
        public SecurityEventBuilder user(User user) { this.user = user; return this; }
        public SecurityEventBuilder attemptedEmail(String attemptedEmail) { this.attemptedEmail = attemptedEmail; return this; }
        public SecurityEventBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public SecurityEventBuilder loginStatus(LoginStatus loginStatus) { this.loginStatus = loginStatus; return this; }
        public SecurityEventBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public SecurityEventBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public SecurityEventBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public SecurityEventBuilder failureReason(String failureReason) { this.failureReason = failureReason; return this; }

        public SecurityEvent build() {
            return new SecurityEvent(id, user, attemptedEmail, timestamp, loginStatus, ipAddress, userAgent, eventType, failureReason);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
