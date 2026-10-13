package com.securevault.dto;

import com.securevault.entity.CredentialType;
import jakarta.validation.constraints.NotBlank;

public class CredentialRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String username;

    private String password;

    private String website;
    private String notes;
    private String category;
    private CredentialType type = CredentialType.LOGIN;
    private boolean favorite = false;
    private Long teamId;

    public CredentialRequest() {}

    public CredentialRequest(String title, String username, String password, String website, String notes, String category, CredentialType type, boolean favorite, Long teamId) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.notes = notes;
        this.category = category;
        this.type = type != null ? type : CredentialType.LOGIN;
        this.favorite = favorite;
        this.teamId = teamId;
    }

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
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
}
