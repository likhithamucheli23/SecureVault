package com.securevault.service;

import com.securevault.dto.SecurityAnalyticsDTO;
import com.securevault.entity.LoginStatus;
import com.securevault.entity.SecurityEvent;
import com.securevault.entity.User;
import com.securevault.repository.CredentialRepository;
import com.securevault.repository.CredentialSharingRepository;
import com.securevault.repository.SecurityEventRepository;
import com.securevault.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityAnalyticsServiceTest {

    @Mock
    private SecurityEventRepository securityEventRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private CredentialSharingRepository credentialSharingRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private SecurityAnalyticsService securityAnalyticsService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        securityAnalyticsService = new SecurityAnalyticsService(
                securityEventRepository,
                credentialRepository,
                credentialSharingRepository,
                teamMemberRepository
        );

        user1 = User.builder()
                .id(1L)
                .name("Alice Security")
                .email("alice@securevault.com")
                .passwordHash("hashedpass")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Bob User")
                .email("bob@securevault.com")
                .passwordHash("hashedpass")
                .build();
    }

    @Test
    @DisplayName("Should return 0 metrics for user with no security events (Empty State)")
    void testEmptyStateForUserWithNoEvents() {
        when(securityEventRepository.findByUserAndTimestampAfterOrderByTimestampDesc(eq(user1), any()))
                .thenReturn(Collections.emptyList());
        when(securityEventRepository.findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(eq(user1.getEmail()), any()))
                .thenReturn(Collections.emptyList());
        when(credentialRepository.findByOwner(user1)).thenReturn(Collections.emptyList());
        when(credentialSharingRepository.findBySharedUserAndSharingStatus(eq(user1), any())).thenReturn(Collections.emptyList());
        when(teamMemberRepository.findByUser(user1)).thenReturn(Collections.emptyList());

        SecurityAnalyticsDTO analytics = securityAnalyticsService.getSecurityAnalytics(user1, "7days");

        assertNotNull(analytics);
        assertEquals(0, analytics.getSuccessfulLogins());
        assertEquals(0, analytics.getFailedLogins());
        assertEquals(0, analytics.getSuspiciousActivities());
        assertEquals(0, analytics.getActiveSecurityAlerts());
        assertEquals(0, analytics.getTotalCredentials());
        assertEquals(0, analytics.getSharedCredentials());
        assertEquals(0, analytics.getTeamVaults());
        assertTrue(analytics.getActiveAlertsList().isEmpty());
        assertTrue(analytics.getRecentActivity().isEmpty());
    }

    @Test
    @DisplayName("Should correctly calculate successful logins, failed logins, and suspicious activities")
    void testMetricsCalculation() {
        SecurityEvent event1 = SecurityEvent.builder()
                .id(101L)
                .user(user1)
                .attemptedEmail(user1.getEmail())
                .loginStatus(LoginStatus.SUCCESS)
                .timestamp(LocalDateTime.now().minusHours(1))
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla/5.0")
                .eventType("LOGIN_SUCCESS")
                .build();

        SecurityEvent event2 = SecurityEvent.builder()
                .id(102L)
                .user(user1)
                .attemptedEmail(user1.getEmail())
                .loginStatus(LoginStatus.FAILURE)
                .timestamp(LocalDateTime.now().minusHours(2))
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla/5.0")
                .eventType("LOGIN_FAILED")
                .failureReason("Invalid password")
                .build();

        when(securityEventRepository.findByUserAndTimestampAfterOrderByTimestampDesc(eq(user1), any()))
                .thenReturn(List.of(event1, event2));
        when(securityEventRepository.findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(eq(user1.getEmail()), any()))
                .thenReturn(Collections.emptyList());
        when(credentialRepository.findByOwner(user1)).thenReturn(Collections.emptyList());
        when(credentialSharingRepository.findBySharedUserAndSharingStatus(eq(user1), any())).thenReturn(Collections.emptyList());
        when(teamMemberRepository.findByUser(user1)).thenReturn(Collections.emptyList());

        SecurityAnalyticsDTO analytics = securityAnalyticsService.getSecurityAnalytics(user1, "7days");

        assertEquals(1, analytics.getSuccessfulLogins());
        assertEquals(1, analytics.getFailedLogins());
        assertEquals(1, analytics.getSuspiciousActivities());
        assertEquals(1, analytics.getActiveSecurityAlerts());
        assertEquals("WARNING", analytics.getActiveAlertsList().get(0).getSeverity());
        assertEquals(2, analytics.getRecentActivity().size());
    }

    @Test
    @DisplayName("Should trigger CRITICAL alert when 3 or more failed login attempts occur")
    void testCriticalAlertOnMultipleFailedLogins() {
        SecurityEvent e1 = SecurityEvent.builder().id(1L).user(user1).attemptedEmail(user1.getEmail()).loginStatus(LoginStatus.FAILURE).timestamp(LocalDateTime.now().minusMinutes(5)).ipAddress("10.0.0.1").build();
        SecurityEvent e2 = SecurityEvent.builder().id(2L).user(user1).attemptedEmail(user1.getEmail()).loginStatus(LoginStatus.FAILURE).timestamp(LocalDateTime.now().minusMinutes(4)).ipAddress("10.0.0.1").build();
        SecurityEvent e3 = SecurityEvent.builder().id(3L).user(user1).attemptedEmail(user1.getEmail()).loginStatus(LoginStatus.FAILURE).timestamp(LocalDateTime.now().minusMinutes(3)).ipAddress("10.0.0.1").build();

        when(securityEventRepository.findByUserAndTimestampAfterOrderByTimestampDesc(eq(user1), any()))
                .thenReturn(List.of(e1, e2, e3));
        when(securityEventRepository.findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(eq(user1.getEmail()), any()))
                .thenReturn(Collections.emptyList());

        SecurityAnalyticsDTO analytics = securityAnalyticsService.getSecurityAnalytics(user1, "today");

        assertEquals(0, analytics.getSuccessfulLogins());
        assertEquals(3, analytics.getFailedLogins());
        assertEquals(3, analytics.getSuspiciousActivities());
        assertEquals(1, analytics.getActiveSecurityAlerts());
        assertEquals("CRITICAL", analytics.getActiveAlertsList().get(0).getSeverity());
        assertTrue(analytics.getActiveAlertsList().get(0).getTitle().contains("Multiple Failed Login"));
    }

    @Test
    @DisplayName("Should enforce user isolation so User 1 only receives their own analytics")
    void testUserIsolation() {
        SecurityEvent user1Event = SecurityEvent.builder()
                .id(201L)
                .user(user1)
                .attemptedEmail(user1.getEmail())
                .loginStatus(LoginStatus.SUCCESS)
                .timestamp(LocalDateTime.now())
                .build();

        SecurityEvent user2Event = SecurityEvent.builder()
                .id(202L)
                .user(user2)
                .attemptedEmail(user2.getEmail())
                .loginStatus(LoginStatus.FAILURE)
                .timestamp(LocalDateTime.now())
                .build();

        // Stub user1 returns only user1Event
        when(securityEventRepository.findByUserAndTimestampAfterOrderByTimestampDesc(eq(user1), any()))
                .thenReturn(List.of(user1Event));
        when(securityEventRepository.findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(eq(user1.getEmail()), any()))
                .thenReturn(Collections.emptyList());

        // Stub user2 returns only user2Event
        when(securityEventRepository.findByUserAndTimestampAfterOrderByTimestampDesc(eq(user2), any()))
                .thenReturn(List.of(user2Event));
        when(securityEventRepository.findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(eq(user2.getEmail()), any()))
                .thenReturn(Collections.emptyList());

        SecurityAnalyticsDTO analyticsUser1 = securityAnalyticsService.getSecurityAnalytics(user1, "7days");
        SecurityAnalyticsDTO analyticsUser2 = securityAnalyticsService.getSecurityAnalytics(user2, "7days");

        assertEquals(1, analyticsUser1.getSuccessfulLogins());
        assertEquals(0, analyticsUser1.getFailedLogins());

        assertEquals(0, analyticsUser2.getSuccessfulLogins());
        assertEquals(1, analyticsUser2.getFailedLogins());
    }

    @Test
    @DisplayName("Should filter events correctly for 'all' time range")
    void testAllTimeRangeFiltering() {
        when(securityEventRepository.findByUserOrderByTimestampDesc(user1))
                .thenReturn(Collections.emptyList());
        when(securityEventRepository.findByAttemptedEmailOrderByTimestampDesc(user1.getEmail()))
                .thenReturn(Collections.emptyList());

        SecurityAnalyticsDTO analytics = securityAnalyticsService.getSecurityAnalytics(user1, "all");

        assertNotNull(analytics);
        assertEquals("all", analytics.getTimeRange());
    }
}
