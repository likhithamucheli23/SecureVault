package com.securevault.controller;

import com.securevault.dto.*;
import com.securevault.entity.User;
import com.securevault.service.AuthService;
import com.securevault.service.PasswordRecoveryService;
import com.securevault.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordRecoveryService passwordRecoveryService;
    private final UserService userService;

    public AuthController(AuthService authService,
                          PasswordRecoveryService passwordRecoveryService,
                          UserService userService) {
        this.authService = authService;
        this.passwordRecoveryService = passwordRecoveryService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-mfa")
    public ResponseEntity<AuthResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.verifyMfa(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa() {
        User currentUser = userService.getAuthenticatedUser();
        MfaSetupResponse response = authService.setupMfa(currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/enable")
    public ResponseEntity<UserDTO> enableMfa() {
        User currentUser = userService.getAuthenticatedUser();
        UserDTO updated = authService.toggleMfa(currentUser, true);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/mfa/disable")
    public ResponseEntity<UserDTO> disableMfa() {
        User currentUser = userService.getAuthenticatedUser();
        UserDTO updated = authService.toggleMfa(currentUser, false);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String resetToken = passwordRecoveryService.initiatePasswordRecovery(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Password recovery token generated and sent to user email.",
                "token", resetToken
        ));
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<Map<String, Boolean>> verifyResetToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        boolean valid = passwordRecoveryService.verifyToken(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordRecoveryService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful. You may now log in with your new password."));
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<AuthResponse> oauth2Login(@Valid @RequestBody OAuthLoginRequest request, HttpServletRequest httpRequest) {
        AuthResponse response = authService.oauth2Login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        return ResponseEntity.ok(Map.of("message", "Successfully logged out. Session revoked."));
    }
}
