package com.securevault.dto;

import com.securevault.entity.CredentialType;
import java.time.LocalDateTime;

public class CredentialResponse {
    private Long id;
    private String title;
    private String username;
    private String password;
    private String website;
    private String notes;
    private String category;
    private CredentialType type;
    private boolean favorite;
    private UserDTO owner;
    private Long teamId;
    private String teamName;
    private String permissionLevel;
    private String sharingStatus;
    private LocalDateTime expirationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CredentialResponse() {}

    public CredentialResponse(Long id, String title, String username, String password, String website, String notes, String category, CredentialType type, boolean favorite, UserDTO owner, Long teamId, String teamName, String permissionLevel, String sharingStatus, LocalDateTime expirationDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.notes = notes;
        this.category = category;
        this.type = type;
        this.favorite = favorite;
        this.owner = owner;
        this.teamId = teamId;
        this.teamName = teamName;
        this.permissionLevel = permissionLevel;
        this.sharingStatus = sharingStatus;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CredentialResponseBuilder builder() {
        return new CredentialResponseBuilder();
    }

    public static class CredentialResponseBuilder {
        private Long id;
        private String title;
        private String username;
        private String password;
        private String website;
        private String notes;
        private String category;
        private CredentialType type = CredentialType.LOGIN;
        private boolean favorite = false;
        private UserDTO owner;
        private Long teamId;
        private String teamName;
        private String permissionLevel;
        private String sharingStatus;
        private LocalDateTime expirationDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CredentialResponseBuilder id(Long id) { this.id = id; return this; }
        public CredentialResponseBuilder title(String title) { this.title = title; return this; }
        public CredentialResponseBuilder username(String username) { this.username = username; return this; }
        public CredentialResponseBuilder password(String password) { this.password = password; return this; }
        public CredentialResponseBuilder website(String website) { this.website = website; return this; }
        public CredentialResponseBuilder notes(String notes) { this.notes = notes; return this; }
        public CredentialResponseBuilder category(String category) { this.category = category; return this; }
        public CredentialResponseBuilder type(CredentialType type) { this.type = type; return this; }
        public CredentialResponseBuilder favorite(boolean favorite) { this.favorite = favorite; return this; }
        public CredentialResponseBuilder owner(UserDTO owner) { this.owner = owner; return this; }
        public CredentialResponseBuilder teamId(Long teamId) { this.teamId = teamId; return this; }
        public CredentialResponseBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public CredentialResponseBuilder permissionLevel(String permissionLevel) { this.permissionLevel = permissionLevel; return this; }
        public CredentialResponseBuilder sharingStatus(String sharingStatus) { this.sharingStatus = sharingStatus; return this; }
        public CredentialResponseBuilder expirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; return this; }
        public CredentialResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public CredentialResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public CredentialResponse build() {
            return new CredentialResponse(id, title, username, password, website, notes, category, type, favorite, owner, teamId, teamName, permissionLevel, sharingStatus, expirationDate, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public CredentialType getType() { return type; }
    public void setType(CredentialType type) { this.type = type; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public UserDTO getOwner() { return owner; }
    public void setOwner(UserDTO owner) { this.owner = owner; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getPermissionLevel() { return permissionLevel; }
    public void setPermissionLevel(String permissionLevel) { this.permissionLevel = permissionLevel; }
    public String getSharingStatus() { return sharingStatus; }
    public void setSharingStatus(String sharingStatus) { this.sharingStatus = sharingStatus; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
