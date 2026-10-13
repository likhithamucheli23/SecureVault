package com.securevault.dto;

import com.securevault.entity.PermissionLevel;
import com.securevault.entity.SharingStatus;
import java.time.LocalDateTime;

public class ShareCredentialResponse {
    private Long id;
    private Long credentialId;
    private String credentialTitle;
    private UserDTO owner;
    private UserDTO sharedUser;
    private PermissionLevel permissionLevel;
    private SharingStatus sharingStatus;
    private LocalDateTime expirationDate;
    private LocalDateTime createdAt;

    public ShareCredentialResponse() {}

    public ShareCredentialResponse(Long id, Long credentialId, String credentialTitle, UserDTO owner, UserDTO sharedUser, PermissionLevel permissionLevel, SharingStatus sharingStatus, LocalDateTime expirationDate, LocalDateTime createdAt) {
        this.id = id;
        this.credentialId = credentialId;
        this.credentialTitle = credentialTitle;
        this.owner = owner;
        this.sharedUser = sharedUser;
        this.permissionLevel = permissionLevel;
        this.sharingStatus = sharingStatus;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
    }

    public static ShareCredentialResponseBuilder builder() {
        return new ShareCredentialResponseBuilder();
    }

    public static class ShareCredentialResponseBuilder {
        private Long id;
        private Long credentialId;
        private String credentialTitle;
        private UserDTO owner;
        private UserDTO sharedUser;
        private PermissionLevel permissionLevel;
        private SharingStatus sharingStatus;
        private LocalDateTime expirationDate;
        private LocalDateTime createdAt;

        public ShareCredentialResponseBuilder id(Long id) { this.id = id; return this; }
        public ShareCredentialResponseBuilder credentialId(Long credentialId) { this.credentialId = credentialId; return this; }
        public ShareCredentialResponseBuilder credentialTitle(String credentialTitle) { this.credentialTitle = credentialTitle; return this; }
        public ShareCredentialResponseBuilder owner(UserDTO owner) { this.owner = owner; return this; }
        public ShareCredentialResponseBuilder sharedUser(UserDTO sharedUser) { this.sharedUser = sharedUser; return this; }
        public ShareCredentialResponseBuilder permissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; return this; }
        public ShareCredentialResponseBuilder sharingStatus(SharingStatus sharingStatus) { this.sharingStatus = sharingStatus; return this; }
        public ShareCredentialResponseBuilder expirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; return this; }
        public ShareCredentialResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ShareCredentialResponse build() {
            return new ShareCredentialResponse(id, credentialId, credentialTitle, owner, sharedUser, permissionLevel, sharingStatus, expirationDate, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCredentialId() { return credentialId; }
    public void setCredentialId(Long credentialId) { this.credentialId = credentialId; }
    public String getCredentialTitle() { return credentialTitle; }
    public void setCredentialTitle(String credentialTitle) { this.credentialTitle = credentialTitle; }
    public UserDTO getOwner() { return owner; }
    public void setOwner(UserDTO owner) { this.owner = owner; }
    public UserDTO getSharedUser() { return sharedUser; }
    public void setSharedUser(UserDTO sharedUser) { this.sharedUser = sharedUser; }
    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }
    public SharingStatus getSharingStatus() { return sharingStatus; }
    public void setSharingStatus(SharingStatus sharingStatus) { this.sharingStatus = sharingStatus; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
