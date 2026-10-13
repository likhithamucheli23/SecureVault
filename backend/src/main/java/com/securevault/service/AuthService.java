package com.securevault.service;

import com.securevault.dto.*;
import com.securevault.entity.*;
import com.securevault.exception.BadRequestException;
import com.securevault.repository.RevokedTokenRepository;
import com.securevault.repository.UserRepository;
import com.securevault.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final SecurityEventService securityEventService;
    private final DeviceService deviceService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final NotificationService notificationService;

    // Temporary store for pending MFA challenge tokens -> email
    private final Map<String, String> pendingMfaChallenges = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       UserService userService,
                       SecurityEventService securityEventService,
                       DeviceService deviceService,
                       RevokedTokenRepository revokedTokenRepository,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.securityEventService = securityEventService;
        this.deviceService = deviceService;
        this.revokedTokenRepository = revokedTokenRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public UserDTO register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .mfaEnabled(false)
                .build();

        User savedUser = userRepository.save(user);
        return userService.convertToDTO(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String email = request.getEmail().toLowerCase().trim();

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            securityEventService.recordLoginEvent(
                    null,
                    email,
                    LoginStatus.FAILURE,
                    clientIp,
                    userAgent,
                    "LOGIN_FAILED",
                    "User account does not exist"
            );
            throw new BadRequestException("Invalid email or password.");
        }

        User user = userOptional.get();

        if (deviceService.isDeviceRevoked(user, userAgent)) {
            securityEventService.recordLoginEvent(
                    user,
                    email,
                    LoginStatus.FAILURE,
                    clientIp,
                    userAgent,
                    "LOGIN_REJECTED",
                    "Device access has been revoked"
            );
            throw new BadRequestException("Access denied: Device has been revoked by account owner.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            securityEventService.recordLoginEvent(
                    user,
                    email,
                    LoginStatus.FAILURE,
                    clientIp,
                    userAgent,
                    "LOGIN_FAILED",
                    "Invalid password"
            );
            throw new BadRequestException("Invalid email or password.");
        }

        deviceService.trackDevice(user, userAgent, clientIp);

        if (user.isMfaEnabled()) {
            String mfaToken = "MFA-CHALLENGE-" + UUID.randomUUID();
            pendingMfaChallenges.put(mfaToken, user.getEmail());

            securityEventService.recordLoginEvent(
                    user,
                    email,
                    LoginStatus.SUCCESS,
                    clientIp,
                    userAgent,
                    "MFA_CHALLENGE_ISSUED",
                    "Primary auth passed, MFA verification pending"
            );

            return AuthResponse.builder()
                    .mfaRequired(true)
                    .mfaToken(mfaToken)
                    .message("Multi-Factor Authentication code required")
                    .build();
        }

        String token = jwtUtils.generateToken(user.getEmail());

        securityEventService.recordLoginEvent(
                user,
                email,
                LoginStatus.SUCCESS,
                clientIp,
                userAgent,
                "LOGIN_SUCCESS",
                null
        );

        notificationService.sendNotification(
                user,
                "New Login Recorded",
                "Successful login from IP " + clientIp,
                NotificationType.LOGIN_ALERT
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userService.convertToDTO(user))
                .build();
    }

    @Transactional
    public AuthResponse verifyMfa(MfaVerifyRequest request, HttpServletRequest httpRequest) {
        String email = pendingMfaChallenges.get(request.getMfaToken());
        if (email == null) {
            throw new BadRequestException("Invalid or expired MFA challenge token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found for MFA challenge."));

        // Accepts '123456' or matching code/secret
        boolean isValidCode = "123456".equals(request.getCode()) ||
                (user.getMfaSecret() != null && user.getMfaSecret().equals(request.getCode()));

        String clientIp = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (!isValidCode) {
            securityEventService.recordLoginEvent(
                    user,
                    email,
                    LoginStatus.FAILURE,
                    clientIp,
                    userAgent,
                    "MFA_FAILED",
                    "Incorrect MFA verification code"
            );
            throw new BadRequestException("Invalid MFA verification code.");
        }

        pendingMfaChallenges.remove(request.getMfaToken());
        String token = jwtUtils.generateToken(user.getEmail());

        securityEventService.recordLoginEvent(
                user,
                email,
                LoginStatus.SUCCESS,
                clientIp,
                userAgent,
                "LOGIN_MFA_SUCCESS",
                "MFA verification successful"
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userService.convertToDTO(user))
                .build();
    }

    @Transactional
    public AuthResponse oauth2Login(OAuthLoginRequest request, HttpServletRequest httpRequest) {
        String email = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .name(request.getName())
                    .email(email)
                    .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.USER)
                    .oauthProvider(request.getProvider())
                    .oauthId(request.getOauthId())
                    .build();
            return userRepository.save(newUser);
        });

        String clientIp = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        deviceService.trackDevice(user, userAgent, clientIp);

        String token = jwtUtils.generateToken(user.getEmail());

        securityEventService.recordLoginEvent(
                user,
                email,
                LoginStatus.SUCCESS,
                clientIp,
                userAgent,
                "OAUTH2_LOGIN_SUCCESS",
                "Logged in via " + request.getProvider()
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userService.convertToDTO(user))
                .build();
    }

    @Transactional
    public MfaSetupResponse setupMfa(User user) {
        String secret = "SECUREVAULT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        user.setMfaSecret(secret);
        userRepository.save(user);

        return new MfaSetupResponse(secret, "otpauth://totp/SecureVault:" + user.getEmail() + "?secret=" + secret + "&issuer=SecureVault");
    }

    @Transactional
    public UserDTO toggleMfa(User user, boolean enable) {
        user.setMfaEnabled(enable);
        User updated = userRepository.save(user);
        return userService.convertToDTO(updated);
    }

    @Transactional
    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (!revokedTokenRepository.existsByToken(token)) {
                revokedTokenRepository.save(new RevokedToken(token, LocalDateTime.now()));
            }
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }
}
