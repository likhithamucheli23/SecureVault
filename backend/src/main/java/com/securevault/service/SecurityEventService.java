package com.securevault.service;

import com.securevault.dto.SecurityEventDTO;
import com.securevault.entity.LoginStatus;
import com.securevault.entity.SecurityEvent;
import com.securevault.entity.User;
import com.securevault.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityEventService {

    private final SecurityEventRepository securityEventRepository;

    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    @Transactional
    public void recordLoginEvent(User user, String attemptedEmail, LoginStatus status, String ipAddress, String userAgent, String eventType, String failureReason) {
        try {
            SecurityEvent event = SecurityEvent.builder()
                    .user(user)
                    .attemptedEmail(attemptedEmail)
                    .timestamp(LocalDateTime.now())
                    .loginStatus(status)
                    .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                    .userAgent(userAgent != null ? userAgent : "Unknown Client")
                    .eventType(eventType)
                    .failureReason(failureReason)
                    .build();

            securityEventRepository.save(event);
        } catch (Exception e) {
            // Logging failure should never break authentication flow
        }
    }

    @Transactional(readOnly = true)
    public List<SecurityEventDTO> getUserLoginHistory(User user) {
        List<SecurityEvent> events = securityEventRepository.findByUserOrderByTimestampDesc(user);
        if (events.isEmpty()) {
            events = securityEventRepository.findByAttemptedEmailOrderByTimestampDesc(user.getEmail());
        }

        return events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SecurityEventDTO convertToDTO(SecurityEvent event) {
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
