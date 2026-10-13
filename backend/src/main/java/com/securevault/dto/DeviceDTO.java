package com.securevault.dto;

import java.time.LocalDateTime;

public class DeviceDTO {
    private Long id;
    private String deviceName;
    private String browser;
    private String os;
    private String ipAddress;
    private boolean trusted;
    private boolean revoked;
    private LocalDateTime lastActive;

    public DeviceDTO() {}

    public DeviceDTO(Long id, String deviceName, String browser, String os, String ipAddress, boolean trusted, boolean revoked, LocalDateTime lastActive) {
        this.id = id;
        this.deviceName = deviceName;
        this.browser = browser;
        this.os = os;
        this.ipAddress = ipAddress;
        this.trusted = trusted;
        this.revoked = revoked;
        this.lastActive = lastActive;
    }

    public static DeviceDTOBuilder builder() {
        return new DeviceDTOBuilder();
    }

    public static class DeviceDTOBuilder {
        private Long id;
        private String deviceName;
        private String browser;
        private String os;
        private String ipAddress;
        private boolean trusted;
        private boolean revoked;
        private LocalDateTime lastActive;

        public DeviceDTOBuilder id(Long id) { this.id = id; return this; }
        public DeviceDTOBuilder deviceName(String deviceName) { this.deviceName = deviceName; return this; }
        public DeviceDTOBuilder browser(String browser) { this.browser = browser; return this; }
        public DeviceDTOBuilder os(String os) { this.os = os; return this; }
        public DeviceDTOBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public DeviceDTOBuilder trusted(boolean trusted) { this.trusted = trusted; return this; }
        public DeviceDTOBuilder revoked(boolean revoked) { this.revoked = revoked; return this; }
        public DeviceDTOBuilder lastActive(LocalDateTime lastActive) { this.lastActive = lastActive; return this; }

        public DeviceDTO build() {
            return new DeviceDTO(id, deviceName, browser, os, ipAddress, trusted, revoked, lastActive);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public boolean isTrusted() { return trusted; }
    public void setTrusted(boolean trusted) { this.trusted = trusted; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }
}
