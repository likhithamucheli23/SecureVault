package com.securevault.controller;

import com.securevault.dto.AddTeamMemberRequest;
import com.securevault.dto.CredentialResponse;
import com.securevault.dto.TeamRequest;
import com.securevault.dto.TeamResponse;
import com.securevault.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        TeamResponse team = teamService.createTeam(request);
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getUserTeams() {
        List<TeamResponse> teams = teamService.getUserTeams();
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamResponse.TeamMemberDTO> addMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddTeamMemberRequest request
    ) {
        TeamResponse.TeamMemberDTO member = teamService.addMember(teamId, request);
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamResponse.TeamMemberDTO>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamResponse.TeamMemberDTO> members = teamService.getTeamMembers(teamId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{teamId}/vault")
    public ResponseEntity<List<CredentialResponse>> getTeamVault(@PathVariable Long teamId) {
        List<CredentialResponse> vault = teamService.getTeamVault(teamId);
        return ResponseEntity.ok(vault);
    }
}
