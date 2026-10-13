package com.securevault.dto;

import com.securevault.entity.TeamRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AddTeamMemberRequest {

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String email;

    private TeamRole role = TeamRole.MEMBER;

    public AddTeamMemberRequest() {}

    public AddTeamMemberRequest(String email, TeamRole role) {
        this.email = email;
        this.role = role != null ? role : TeamRole.MEMBER;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public TeamRole getRole() { return role; }
    public void setRole(TeamRole role) { this.role = role; }
}
