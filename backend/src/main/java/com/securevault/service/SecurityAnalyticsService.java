package com.securevault.service;

import com.securevault.dto.SecurityAlertDTO;
import com.securevault.dto.SecurityAnalyticsDTO;
import com.securevault.dto.SecurityEventDTO;
import com.securevault.entity.LoginStatus;
import com.securevault.entity.SecurityEvent;
import com.securevault.entity.SharingStatus;
import com.securevault.entity.User;
import com.securevault.repository.CredentialRepository;
import com.securevault.repository.CredentialSharingRepository;
import com.securevault.repository.SecurityEventRepository;
import com.securevault.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SecurityAnalyticsService {

    private final SecurityEventRepository securityEventRepository;
    private final CredentialRepository credentialRepository;
    private final CredentialSharingRepository credentialSharingRepository;
    private final TeamMemberRepository teamMemberRepository;

    public SecurityAnalyticsService(SecurityEventRepository securityEventRepository,
                                   CredentialRepository credentialRepository,
                                   CredentialSharingRepository credentialSharingRepository,
                                   TeamMemberRepository teamMemberRepository) {
        this.securityEventRepository = securityEventRepository;
        this.credentialRepository = credentialRepository;
        this.credentialSharingRepository = credentialSharingRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Transactional(readOnly = true)
    public SecurityAnalyticsDTO getSecurityAnalytics(User user, String timeRange) {
        String normalizedRange = (timeRange != null) ? timeRange.toLowerCase().trim() : "7days";
        LocalDateTime since = resolveTimeCutoff(normalizedRange);

        List<SecurityEvent> eventsByUser;
        List<SecurityEvent> eventsByEmail;

        if (since != null) {
            eventsByUser = securityEventRepository.findByUserAndTimestampAfterOrderByTimestampDesc(user, since);
            eventsByEmail = securityEventRepository.findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(user.getEmail(), since);
        } else {
            eventsByUser = securityEventRepository.findByUserOrderByTimestampDesc(user);
            eventsByEmail = securityEventRepository.findByAttemptedEmailOrderByTimestampDesc(user.getEmail());
        }

        // Merge and deduplicate by event ID while preserving sort order (timestamp descending)
        Map<Long, SecurityEvent> eventMap = new LinkedHashMap<>();
        for (SecurityEvent event : eventsByUser) {
            if (event.getId() != null) {
                eventMap.put(event.getId(), event);
            }
        }
        for (SecurityEvent event : eventsByEmail) {
            if (event.getId() != null) {
                eventMap.putIfAbsent(event.getId(), event);
            }
        }

        List<SecurityEvent> allEvents = eventMap.values().stream()
                .sorted(Comparator.comparing(SecurityEvent::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());

        long successfulLogins = allEvents.stream()
                .filter(e -> e.getLoginStatus() == LoginStatus.SUCCESS)
                .count();

        long failedLogins = allEvents.stream()
                .filter(e -> e.getLoginStatus() == LoginStatus.FAILURE)
                .count();

        Set<String> distinctIps = allEvents.stream()
                .map(SecurityEvent::getIpAddress)
                .filter(ip -> ip != null && !ip.isBlank())
                .collect(Collectors.toSet());

        // Suspicious activity = failed logins count + (additional distinct IPs beyond 1)
        long suspiciousActivities = failedLogins + (distinctIps.size() > 1 ? distinctIps.size() - 1 : 0);

        List<SecurityAlertDTO> alerts = new ArrayList<>();
        if (failedLogins >= 3) {
            alerts.add(SecurityAlertDTO.builder()
                    .id("ALERT-FAILED-LOGINS-HIGH")
                    .severity("CRITICAL")
                    .title("Multiple Failed Login Attempts")
                    .message(failedLogins + " failed login attempts detected in the selected timeframe.")
                    .timestamp(LocalDateTime.now())
                    .build());
        } else if (failedLogins > 0) {
            alerts.add(SecurityAlertDTO.builder()
                    .id("ALERT-FAILED-LOGINS-LOW")
                    .severity("WARNING")
                    .title("Failed Login Attempt Recorded")
                    .message(failedLogins + " failed login attempt(s) recorded for your account.")
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        if (distinctIps.size() > 1) {
            alerts.add(SecurityAlertDTO.builder()
                    .id("ALERT-MULTIPLE-IPS")
                    .severity("WARNING")
                    .title("Multiple IP Addresses Detected")
                    .message("Authentication attempts recorded from " + distinctIps.size() + " different IP addresses: " + String.join(", ", distinctIps))
                    .timestamp(LocalDateTime.now())
                    .build());
        }

        long totalCredentials = credentialRepository.findByOwner(user).size();
        long sharedCredentials = credentialSharingRepository.findBySharedUserAndSharingStatus(user, SharingStatus.ACTIVE).size();
        long teamVaults = teamMemberRepository.findByUser(user).size();

        List<SecurityEventDTO> recentActivity = allEvents.stream()
                .limit(10)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return SecurityAnalyticsDTO.builder()
                .successfulLogins(successfulLogins)
                .failedLogins(failedLogins)
                .suspiciousActivities(suspiciousActivities)
                .activeSecurityAlerts(alerts.size())
                .totalCredentials(totalCredentials)
                .sharedCredentials(sharedCredentials)
                .teamVaults(teamVaults)
                .timeRange(normalizedRange)
                .activeAlertsList(alerts)
                .recentActivity(recentActivity)
                .build();
    }

    private LocalDateTime resolveTimeCutoff(String range) {
        if (range == null) return LocalDateTime.now().minusDays(7);
        switch (range) {
            case "today":
                return LocalDate.now().atStartOfDay();
            case "7days":
            case "last7days":
                return LocalDateTime.now().minusDays(7);
            case "30days":
            case "last30days":
                return LocalDateTime.now().minusDays(30);
            case "all":
            case "alltime":
                return null;
            default:
                return LocalDateTime.now().minusDays(7);
        }
    }

    private SecurityEventDTO convertToDTO(SecurityEvent event) {
        return SecurityEventDTO.builder()
                .id(event.getId())
                .attemptedEmail(event.getAttemptedEmail())
                .timestamp(event.getTimestamp())
                .loginStatus(event.getLoginStatus())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .eventType(event.getEventType())
                .failureReason(event.getFailureReason())
                .build();
    }
}
