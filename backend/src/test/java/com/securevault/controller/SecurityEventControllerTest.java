package com.securevault.controller;

import com.securevault.dto.SecurityAnalyticsDTO;
import com.securevault.entity.User;
import com.securevault.service.SecurityAnalyticsService;
import com.securevault.service.SecurityEventService;
import com.securevault.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SecurityEventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SecurityEventService securityEventService;

    @Mock
    private SecurityAnalyticsService securityAnalyticsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SecurityEventController securityEventController;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(securityEventController).build();

        testUser = User.builder()
                .id(1L)
                .name("Alice Security")
                .email("alice@securevault.com")
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    @DisplayName("GET /api/security/analytics should return security metrics JSON for authenticated user")
    void testGetSecurityAnalytics() throws Exception {
        SecurityAnalyticsDTO analytics = SecurityAnalyticsDTO.builder()
                .successfulLogins(5)
                .failedLogins(2)
                .suspiciousActivities(2)
                .activeSecurityAlerts(1)
                .totalCredentials(10)
                .sharedCredentials(3)
                .teamVaults(1)
                .timeRange("7days")
                .activeAlertsList(Collections.emptyList())
                .recentActivity(Collections.emptyList())
                .build();

        when(securityAnalyticsService.getSecurityAnalytics(eq(testUser), eq("7days"))).thenReturn(analytics);

        mockMvc.perform(get("/api/security/analytics")
                        .param("range", "7days")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successfulLogins").value(5))
                .andExpect(jsonPath("$.failedLogins").value(2))
                .andExpect(jsonPath("$.suspiciousActivities").value(2))
                .andExpect(jsonPath("$.activeSecurityAlerts").value(1))
                .andExpect(jsonPath("$.totalCredentials").value(10))
                .andExpect(jsonPath("$.sharedCredentials").value(3))
                .andExpect(jsonPath("$.teamVaults").value(1))
                .andExpect(jsonPath("$.timeRange").value("7days"));
    }

    @Test
    @DisplayName("GET /api/security/login-history should return login history list")
    void testGetLoginHistory() throws Exception {
        when(securityEventService.getUserLoginHistory(testUser)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/security/login-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
