package com.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credential_sharings")
public class CredentialSharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shared_user_id", nullable = false)
    private User sharedUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false)
    private PermissionLevel permissionLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "sharing_status", nullable = false)
    private SharingStatus sharingStatus;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CredentialSharing() {}

    public CredentialSharing(Long id, Credential credential, User owner, User sharedUser, PermissionLevel permissionLevel, SharingStatus sharingStatus, LocalDateTime expirationDate, LocalDateTime createdAt) {
        this.id = id;
        this.credential = credential;
        this.owner = owner;
        this.sharedUser = sharedUser;
        this.permissionLevel = permissionLevel;
        this.sharingStatus = sharingStatus;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
    }

    public static CredentialSharingBuilder builder() {
        return new CredentialSharingBuilder();
    }

    public static class CredentialSharingBuilder {
        private Long id;
        private Credential credential;
        private User owner;
        private User sharedUser;
        private PermissionLevel permissionLevel;
        private SharingStatus sharingStatus;
        private LocalDateTime expirationDate;
        private LocalDateTime createdAt;

        public CredentialSharingBuilder id(Long id) { this.id = id; return this; }
        public CredentialSharingBuilder credential(Credential credential) { this.credential = credential; return this; }
        public CredentialSharingBuilder owner(User owner) { this.owner = owner; return this; }
        public CredentialSharingBuilder sharedUser(User sharedUser) { this.sharedUser = sharedUser; return this; }
        public CredentialSharingBuilder permissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; return this; }
        public CredentialSharingBuilder sharingStatus(SharingStatus sharingStatus) { this.sharingStatus = sharingStatus; return this; }
        public CredentialSharingBuilder expirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; return this; }
        public CredentialSharingBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CredentialSharing build() {
            return new CredentialSharing(id, credential, owner, sharedUser, permissionLevel, sharingStatus, expirationDate, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.sharingStatus == null) {
            this.sharingStatus = SharingStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Credential getCredential() { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public User getSharedUser() { return sharedUser; }
    public void setSharedUser(User sharedUser) { this.sharedUser = sharedUser; }
    public PermissionLevel getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(PermissionLevel permissionLevel) { this.permissionLevel = permissionLevel; }
    public SharingStatus getSharingStatus() { return sharingStatus; }
    public void setSharingStatus(SharingStatus sharingStatus) { this.sharingStatus = sharingStatus; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
