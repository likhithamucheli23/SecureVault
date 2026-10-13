package com.securevault.service;

import com.securevault.dto.*;
import com.securevault.entity.*;
import com.securevault.exception.BadRequestException;
import com.securevault.repository.RevokedTokenRepository;
import com.securevault.repository.UserRepository;
import com.securevault.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceExtendedTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private SecurityEventService securityEventService;

    @Mock
    private DeviceService deviceService;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest httpRequest;

    private AuthService authService;
    private User testUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtUtils,
                userService,
                securityEventService,
                deviceService,
                revokedTokenRepository,
                notificationService
        );

        testUser = User.builder()
                .id(10L)
                .name("Test User")
                .email("test@securevault.com")
                .passwordHash("$2a$10$encodedPasswordHash")
                .role(Role.USER)
                .mfaEnabled(false)
                .build();
    }

    @Test
    @DisplayName("Task 1: Standard login when MFA disabled returns JWT token directly")
    void testStandardLoginNoMfa() {
        LoginRequest loginReq = new LoginRequest("test@securevault.com", "Password123!");
        when(userRepository.findByEmail("test@securevault.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", testUser.getPasswordHash())).thenReturn(true);
        when(deviceService.isDeviceRevoked(any(), any())).thenReturn(false);
        when(jwtUtils.generateToken("test@securevault.com")).thenReturn("valid.jwt.token");
        when(userService.convertToDTO(testUser)).thenReturn(new UserDTO(10L, "Test User", "test@securevault.com", Role.USER, false, null));

        AuthResponse response = authService.login(loginReq, httpRequest);

        assertNotNull(response);
        assertFalse(response.isMfaRequired());
        assertEquals("valid.jwt.token", response.getToken());
    }

    @Test
    @DisplayName("Task 1: Login with MFA enabled returns MFA challenge token, NOT authenticated JWT")
    void testMfaRequiredFlow() {
        testUser.setMfaEnabled(true);
        LoginRequest loginReq = new LoginRequest("test@securevault.com", "Password123!");
        when(userRepository.findByEmail("test@securevault.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", testUser.getPasswordHash())).thenReturn(true);
        when(deviceService.isDeviceRevoked(any(), any())).thenReturn(false);

        AuthResponse response = authService.login(loginReq, httpRequest);

        assertNotNull(response);
        assertTrue(response.isMfaRequired());
        assertNull(response.getToken(), "JWT Token must NOT be issued before MFA verification!");
        assertNotNull(response.getMfaToken());
    }

    @Test
    @DisplayName("Task 1: Correct MFA challenge code completes authentication and returns JWT")
    void testVerifyMfaSuccess() {
        testUser.setMfaEnabled(true);
        LoginRequest loginReq = new LoginRequest("test@securevault.com", "Password123!");
        when(userRepository.findByEmail("test@securevault.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", testUser.getPasswordHash())).thenReturn(true);

        AuthResponse challengeResponse = authService.login(loginReq, httpRequest);

        MfaVerifyRequest verifyReq = new MfaVerifyRequest(challengeResponse.getMfaToken(), "123456");
        when(jwtUtils.generateToken("test@securevault.com")).thenReturn("mfa.verified.jwt.token");
        when(userService.convertToDTO(testUser)).thenReturn(new UserDTO(10L, "Test User", "test@securevault.com", Role.USER, true, null));

        AuthResponse authResult = authService.verifyMfa(verifyReq, httpRequest);

        assertNotNull(authResult);
        assertEquals("mfa.verified.jwt.token", authResult.getToken());
    }

    @Test
    @DisplayName("Task 1: Incorrect MFA challenge code is rejected")
    void testVerifyMfaFailure() {
        testUser.setMfaEnabled(true);
        LoginRequest loginReq = new LoginRequest("test@securevault.com", "Password123!");
        when(userRepository.findByEmail("test@securevault.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", testUser.getPasswordHash())).thenReturn(true);

        AuthResponse challengeResponse = authService.login(loginReq, httpRequest);

        MfaVerifyRequest verifyReq = new MfaVerifyRequest(challengeResponse.getMfaToken(), "999999");

        assertThrows(BadRequestException.class, () -> authService.verifyMfa(verifyReq, httpRequest));
    }

    @Test
    @DisplayName("Task 4: Session logout invalidates JWT token via revocation repository")
    void testLogoutRevokesToken() {
        authService.logout("Bearer jwt.token.to.revoke");
        verify(revokedTokenRepository, times(1)).save(any(RevokedToken.class));
    }

    @Test
    @DisplayName("Task 5: Login attempt from revoked device is rejected")
    void testRevokedDeviceLoginRejected() {
        LoginRequest loginReq = new LoginRequest("test@securevault.com", "Password123!");
        when(userRepository.findByEmail("test@securevault.com")).thenReturn(Optional.of(testUser));
        when(deviceService.isDeviceRevoked(any(), any())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.login(loginReq, httpRequest));
    }
}
