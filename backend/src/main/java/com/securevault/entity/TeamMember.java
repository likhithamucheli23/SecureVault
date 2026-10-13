package com.securevault.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"team_id", "user_id"})
})
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TeamMember() {}

    public TeamMember(Long id, Team team, User user, TeamRole role, LocalDateTime createdAt) {
        this.id = id;
        this.team = team;
        this.user = user;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static TeamMemberBuilder builder() {
        return new TeamMemberBuilder();
    }

    public static class TeamMemberBuilder {
        private Long id;
        private Team team;
        private User user;
        private TeamRole role;
        private LocalDateTime createdAt;

        public TeamMemberBuilder id(Long id) { this.id = id; return this; }
        public TeamMemberBuilder team(Team team) { this.team = team; return this; }
        public TeamMemberBuilder user(User user) { this.user = user; return this; }
        public TeamMemberBuilder role(TeamRole role) { this.role = role; return this; }
        public TeamMemberBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TeamMember build() {
            return new TeamMember(id, team, user, role, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public TeamRole getRole() { return role; }
    public void setRole(TeamRole role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
