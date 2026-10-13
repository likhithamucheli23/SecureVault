package com.securevault.dto;

import java.util.List;

public class SecurityAnalyticsDTO {
    private long successfulLogins;
    private long failedLogins;
    private long suspiciousActivities;
    private long activeSecurityAlerts;
    private long totalCredentials;
    private long sharedCredentials;
    private long teamVaults;
    private String timeRange;
    private List<SecurityAlertDTO> activeAlertsList;
    private List<SecurityEventDTO> recentActivity;

    public SecurityAnalyticsDTO() {}

    public SecurityAnalyticsDTO(long successfulLogins, long failedLogins, long suspiciousActivities, long activeSecurityAlerts, long totalCredentials, long sharedCredentials, long teamVaults, String timeRange, List<SecurityAlertDTO> activeAlertsList, List<SecurityEventDTO> recentActivity) {
        this.successfulLogins = successfulLogins;
        this.failedLogins = failedLogins;
        this.suspiciousActivities = suspiciousActivities;
        this.activeSecurityAlerts = activeSecurityAlerts;
        this.totalCredentials = totalCredentials;
        this.sharedCredentials = sharedCredentials;
        this.teamVaults = teamVaults;
        this.timeRange = timeRange;
        this.activeAlertsList = activeAlertsList;
        this.recentActivity = recentActivity;
    }

    public static SecurityAnalyticsDTOBuilder builder() {
        return new SecurityAnalyticsDTOBuilder();
    }

    public static class SecurityAnalyticsDTOBuilder {
        private long successfulLogins;
        private long failedLogins;
        private long suspiciousActivities;
        private long activeSecurityAlerts;
        private long totalCredentials;
        private long sharedCredentials;
        private long teamVaults;
        private String timeRange;
        private List<SecurityAlertDTO> activeAlertsList;
        private List<SecurityEventDTO> recentActivity;

        public SecurityAnalyticsDTOBuilder successfulLogins(long successfulLogins) { this.successfulLogins = successfulLogins; return this; }
        public SecurityAnalyticsDTOBuilder failedLogins(long failedLogins) { this.failedLogins = failedLogins; return this; }
        public SecurityAnalyticsDTOBuilder suspiciousActivities(long suspiciousActivities) { this.suspiciousActivities = suspiciousActivities; return this; }
        public SecurityAnalyticsDTOBuilder activeSecurityAlerts(long activeSecurityAlerts) { this.activeSecurityAlerts = activeSecurityAlerts; return this; }
        public SecurityAnalyticsDTOBuilder totalCredentials(long totalCredentials) { this.totalCredentials = totalCredentials; return this; }
        public SecurityAnalyticsDTOBuilder sharedCredentials(long sharedCredentials) { this.sharedCredentials = sharedCredentials; return this; }
        public SecurityAnalyticsDTOBuilder teamVaults(long teamVaults) { this.teamVaults = teamVaults; return this; }
        public SecurityAnalyticsDTOBuilder timeRange(String timeRange) { this.timeRange = timeRange; return this; }
        public SecurityAnalyticsDTOBuilder activeAlertsList(List<SecurityAlertDTO> activeAlertsList) { this.activeAlertsList = activeAlertsList; return this; }
        public SecurityAnalyticsDTOBuilder recentActivity(List<SecurityEventDTO> recentActivity) { this.recentActivity = recentActivity; return this; }

        public SecurityAnalyticsDTO build() {
            return new SecurityAnalyticsDTO(successfulLogins, failedLogins, suspiciousActivities, activeSecurityAlerts, totalCredentials, sharedCredentials, teamVaults, timeRange, activeAlertsList, recentActivity);
        }
    }

    public long getSuccessfulLogins() { return successfulLogins; }
    public void setSuccessfulLogins(long successfulLogins) { this.successfulLogins = successfulLogins; }

    public long getFailedLogins() { return failedLogins; }
    public void setFailedLogins(long failedLogins) { this.failedLogins = failedLogins; }

    public long getSuspiciousActivities() { return suspiciousActivities; }
    public void setSuspiciousActivities(long suspiciousActivities) { this.suspiciousActivities = suspiciousActivities; }

    public long getActiveSecurityAlerts() { return activeSecurityAlerts; }
    public void setActiveSecurityAlerts(long activeSecurityAlerts) { this.activeSecurityAlerts = activeSecurityAlerts; }

    public long getTotalCredentials() { return totalCredentials; }
    public void setTotalCredentials(long totalCredentials) { this.totalCredentials = totalCredentials; }

    public long getSharedCredentials() { return sharedCredentials; }
    public void setSharedCredentials(long sharedCredentials) { this.sharedCredentials = sharedCredentials; }

    public long getTeamVaults() { return teamVaults; }
    public void setTeamVaults(long teamVaults) { this.teamVaults = teamVaults; }

    public String getTimeRange() { return timeRange; }
    public void setTimeRange(String timeRange) { this.timeRange = timeRange; }

    public List<SecurityAlertDTO> getActiveAlertsList() { return activeAlertsList; }
    public void setActiveAlertsList(List<SecurityAlertDTO> activeAlertsList) { this.activeAlertsList = activeAlertsList; }

    public List<SecurityEventDTO> getRecentActivity() { return recentActivity; }
    public void setRecentActivity(List<SecurityEventDTO> recentActivity) { this.recentActivity = recentActivity; }
}
