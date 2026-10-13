package com.securevault.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class OAuthLoginRequest {

    @NotBlank
    private String provider; // e.g. "google", "github"

    @NotBlank
    private String oauthId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    public OAuthLoginRequest() {}

    public OAuthLoginRequest(String provider, String oauthId, String email, String name) {
        this.provider = provider;
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getOauthId() { return oauthId; }
    public void setOauthId(String oauthId) { this.oauthId = oauthId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
