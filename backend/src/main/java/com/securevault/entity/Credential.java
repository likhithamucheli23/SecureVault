package com.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credentials")
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String username;

    @Column(name = "encrypted_password", nullable = false, columnDefinition = "TEXT")
    private String encryptedPassword;

    @Column(name = "encrypted_notes", columnDefinition = "TEXT")
    private String encryptedNotes;

    @Column
    private String website;

    @Column
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CredentialType type = CredentialType.LOGIN;

    @Column(name = "is_favorite")
    private boolean isFavorite = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Credential() {}

    public Credential(Long id, User owner, String title, String username, String encryptedPassword, String encryptedNotes, String website, Team team, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.encryptedNotes = encryptedNotes;
        this.website = website;
        this.team = team;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.category = "General";
        this.type = CredentialType.LOGIN;
        this.isFavorite = false;
    }

    public Credential(Long id, User owner, String title, String username, String encryptedPassword, String encryptedNotes, String website, String category, CredentialType type, boolean isFavorite, Team team, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.encryptedNotes = encryptedNotes;
        this.website = website;
        this.category = category;
        this.type = type != null ? type : CredentialType.LOGIN;
        this.isFavorite = isFavorite;
        this.team = team;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CredentialBuilder builder() {
        return new CredentialBuilder();
    }

    public static class CredentialBuilder {
        private Long id;
        private User owner;
        private String title;
        private String username;
        private String encryptedPassword;
        private String encryptedNotes;
        private String website;
        private String category = "General";
        private CredentialType type = CredentialType.LOGIN;
        private boolean isFavorite = false;
        private Team team;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CredentialBuilder id(Long id) { this.id = id; return this; }
        public CredentialBuilder owner(User owner) { this.owner = owner; return this; }
        public CredentialBuilder title(String title) { this.title = title; return this; }
        public CredentialBuilder username(String username) { this.username = username; return this; }
        public CredentialBuilder encryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; return this; }
        public CredentialBuilder encryptedNotes(String encryptedNotes) { this.encryptedNotes = encryptedNotes; return this; }
        public CredentialBuilder website(String website) { this.website = website; return this; }
        public CredentialBuilder category(String category) { this.category = category; return this; }
        public CredentialBuilder type(CredentialType type) { this.type = type != null ? type : CredentialType.LOGIN; return this; }
        public CredentialBuilder isFavorite(boolean isFavorite) { this.isFavorite = isFavorite; return this; }
        public CredentialBuilder team(Team team) { this.team = team; return this; }
        public CredentialBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CredentialBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Credential build() {
            return new Credential(id, owner, title, username, encryptedPassword, encryptedNotes, website, category, type, isFavorite, team, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.type == null) {
            this.type = CredentialType.LOGIN;
        }
        if (this.category == null) {
            this.category = "General";
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }
    public String getEncryptedNotes() { return encryptedNotes; }
    public void setEncryptedNotes(String encryptedNotes) { this.encryptedNotes = encryptedNotes; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public CredentialType getType() { return type; }
    public void setType(CredentialType type) { this.type = type; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
