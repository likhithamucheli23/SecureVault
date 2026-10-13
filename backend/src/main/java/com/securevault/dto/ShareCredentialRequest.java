package com.securevault.dto;

import com.securevault.entity.PermissionLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ShareCredentialRequest {

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotNull(message = "Permission level is required")
    private PermissionLevel permissionLevel;

    private LocalDateTime expirationDate;

    public ShareCredentialRequest() {}

    public ShareCredentialRequest(String userEmail, PermissionLevel permissionLevel, LocalDateTime expirationDate) {
        this.userEmail = userEmail;
        this.permissionLevel = permissionLevel;
        this.expirationDate = expirationDate;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
}
