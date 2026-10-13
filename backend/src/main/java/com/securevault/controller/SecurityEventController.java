package com.securevault.controller;

import com.securevault.dto.SecurityAnalyticsDTO;
import com.securevault.dto.SecurityEventDTO;
import com.securevault.entity.User;
import com.securevault.service.SecurityAnalyticsService;
import com.securevault.service.SecurityEventService;
import com.securevault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security")
public class SecurityEventController {

    private final SecurityEventService securityEventService;
    private final SecurityAnalyticsService securityAnalyticsService;
    private final UserService userService;

    public SecurityEventController(SecurityEventService securityEventService,
                                   SecurityAnalyticsService securityAnalyticsService,
                                   UserService userService) {
        this.securityEventService = securityEventService;
        this.securityAnalyticsService = securityAnalyticsService;
        this.userService = userService;
    }

    @GetMapping("/login-history")
    public ResponseEntity<List<SecurityEventDTO>> getLoginHistory() {
        User currentUser = userService.getAuthenticatedUser();
        List<SecurityEventDTO> history = securityEventService.getUserLoginHistory(currentUser);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/analytics")
    public ResponseEntity<SecurityAnalyticsDTO> getAnalytics(@RequestParam(value = "range", required = false, defaultValue = "7days") String range) {
        User currentUser = userService.getAuthenticatedUser();
        SecurityAnalyticsDTO analytics = securityAnalyticsService.getSecurityAnalytics(currentUser, range);
        return ResponseEntity.ok(analytics);
    }
}
