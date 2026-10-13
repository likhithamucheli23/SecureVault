package com.securevault.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamRequest {

    @NotBlank(message = "Team name is required")
    private String teamName;

    public TeamRequest() {}

    public TeamRequest(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
}
