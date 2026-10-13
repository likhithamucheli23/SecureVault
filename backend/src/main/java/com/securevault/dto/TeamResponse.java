package com.securevault.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TeamResponse {
    private Long id;
    private String teamName;
    private UserDTO owner;
    private List<TeamMemberDTO> members;
    private LocalDateTime createdAt;

    public TeamResponse() {}

    public TeamResponse(Long id, String teamName, UserDTO owner, List<TeamMemberDTO> members, LocalDateTime createdAt) {
        this.id = id;
        this.teamName = teamName;
        this.owner = owner;
        this.members = members;
        this.createdAt = createdAt;
    }

    public static TeamResponseBuilder builder() {
        return new TeamResponseBuilder();
    }

    public static class TeamResponseBuilder {
        private Long id;
        private String teamName;
        private UserDTO owner;
        private List<TeamMemberDTO> members;
        private LocalDateTime createdAt;

        public TeamResponseBuilder id(Long id) { this.id = id; return this; }
        public TeamResponseBuilder teamName(String teamName) { this.teamName = teamName; return this; }
        public TeamResponseBuilder owner(UserDTO owner) { this.owner = owner; return this; }
        public TeamResponseBuilder members(List<TeamMemberDTO> members) { this.members = members; return this; }
        public TeamResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TeamResponse build() {
            return new TeamResponse(id, teamName, owner, members, createdAt);
        }
    }

    public static class TeamMemberDTO {
        private Long id;
        private UserDTO user;
        private String role;
        private LocalDateTime createdAt;

        public TeamMemberDTO() {}

        public TeamMemberDTO(Long id, UserDTO user, String role, LocalDateTime createdAt) {
            this.id = id;
            this.user = user;
            this.role = role;
            this.createdAt = createdAt;
        }

        public static TeamMemberDTOBuilder builder() {
            return new TeamMemberDTOBuilder();
        }

        public static class TeamMemberDTOBuilder {
            private Long id;
            private UserDTO user;
            private String role;
            private LocalDateTime createdAt;

            public TeamMemberDTOBuilder id(Long id) { this.id = id; return this; }
            public TeamMemberDTOBuilder user(UserDTO user) { this.user = user; return this; }
            public TeamMemberDTOBuilder role(String role) { this.role = role; return this; }
            public TeamMemberDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

            public TeamMemberDTO build() {
                return new TeamMemberDTO(id, user, role, createdAt);
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public UserDTO getUser() { return user; }
        public void setUser(UserDTO user) { this.user = user; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public UserDTO getOwner() { return owner; }
    public void setOwner(UserDTO owner) { this.owner = owner; }
    public List<TeamMemberDTO> getMembers() { return members; }
    public void setMembers(List<TeamMemberDTO> members) { this.members = members; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
