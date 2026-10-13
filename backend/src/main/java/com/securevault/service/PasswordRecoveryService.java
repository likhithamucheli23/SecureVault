package com.securevault.service;

import com.securevault.entity.NotificationType;
import com.securevault.entity.PasswordResetToken;
import com.securevault.entity.User;
import com.securevault.exception.BadRequestException;
import com.securevault.exception.ResourceNotFoundException;
import com.securevault.repository.PasswordResetTokenRepository;
import com.securevault.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final SecurityEventService securityEventService;

    public PasswordRecoveryService(UserRepository userRepository,
                                   PasswordResetTokenRepository resetTokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   NotificationService notificationService,
                                   SecurityEventService securityEventService) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.securityEventService = securityEventService;
    }

    @Transactional
    public String initiatePasswordRecovery(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(15));
        resetTokenRepository.save(resetToken);

        notificationService.sendNotification(
                user,
                "Password Reset Request",
                "A password reset was requested for your account. Use recovery token: " + token,
                NotificationType.SECURITY_ALERT
        );

        return token;
    }

    @Transactional(readOnly = true)
    public boolean verifyToken(String token) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token is expired or already used");
        }

        return true;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token is expired or already used");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        securityEventService.recordLoginEvent(
                user,
                user.getEmail(),
                com.securevault.entity.LoginStatus.SUCCESS,
                "INTERNAL",
                "Password Recovery Service",
                "PASSWORD_RESET_SUCCESS",
                "Password successfully recovered and reset"
        );

        notificationService.sendNotification(
                user,
                "Password Reset Successful",
                "Your SecureVault password was successfully updated.",
                NotificationType.SECURITY_ALERT
        );
    }
}
