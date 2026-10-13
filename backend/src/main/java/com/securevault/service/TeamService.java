package com.securevault.service;

import com.securevault.dto.AddTeamMemberRequest;
import com.securevault.dto.CredentialResponse;
import com.securevault.dto.TeamRequest;
import com.securevault.dto.TeamResponse;
import com.securevault.entity.*;
import com.securevault.exception.BadRequestException;
import com.securevault.exception.ResourceNotFoundException;
import com.securevault.exception.UnauthorizedAccessException;
import com.securevault.repository.CredentialRepository;
import com.securevault.repository.TeamMemberRepository;
import com.securevault.repository.TeamRepository;
import com.securevault.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final UserService userService;
    private final CredentialService credentialService;

    public TeamService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserRepository userRepository, CredentialRepository credentialRepository, UserService userService, CredentialService credentialService) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.userService = userService;
        this.credentialService = credentialService;
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        User currentUser = userService.getAuthenticatedUser();

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .owner(currentUser)
                .build();

        Team savedTeam = teamRepository.save(team);

        TeamMember ownerMember = TeamMember.builder()
                .team(savedTeam)
                .user(currentUser)
                .role(TeamRole.OWNER)
                .build();

        teamMemberRepository.save(ownerMember);

        return convertToResponse(savedTeam);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getUserTeams() {
        User currentUser = userService.getAuthenticatedUser();
        List<TeamMember> memberships = teamMemberRepository.findByUser(currentUser);

        return memberships.stream()
                .map(membership -> convertToResponse(membership.getTeam()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamResponse.TeamMemberDTO addMember(Long teamId, AddTeamMemberRequest request) {
        User currentUser = userService.getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        TeamMember currentMember = teamMemberRepository.findByTeamAndUser(team, currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("You are not a member of this team"));

        if (currentMember.getRole() == TeamRole.MEMBER) {
            throw new UnauthorizedAccessException("Only Team Owners and Admins can add members");
        }

        User userToAdd = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + request.getEmail() + " not found"));

        if (teamMemberRepository.existsByTeamAndUser(team, userToAdd)) {
            throw new BadRequestException("User is already a member of this team");
        }

        TeamMember newMember = TeamMember.builder()
                .team(team)
                .user(userToAdd)
                .role(request.getRole() != null ? request.getRole() : TeamRole.MEMBER)
                .build();

        TeamMember saved = teamMemberRepository.save(newMember);

        return TeamResponse.TeamMemberDTO.builder()
                .id(saved.getId())
                .user(userService.convertToDTO(saved.getUser()))
                .role(saved.getRole().name())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TeamResponse.TeamMemberDTO> getTeamMembers(Long teamId) {
        User currentUser = userService.getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if (!teamMemberRepository.existsByTeamAndUser(team, currentUser)) {
            throw new UnauthorizedAccessException("You are not a member of this team");
        }

        List<TeamMember> members = teamMemberRepository.findByTeam(team);
        return members.stream()
                .map(m -> TeamResponse.TeamMemberDTO.builder()
                        .id(m.getId())
                        .user(userService.convertToDTO(m.getUser()))
                        .role(m.getRole().name())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CredentialResponse> getTeamVault(Long teamId) {
        User currentUser = userService.getAuthenticatedUser();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if (!teamMemberRepository.existsByTeamAndUser(team, currentUser)) {
            throw new UnauthorizedAccessException("You are not a member of this team");
        }

        List<Credential> credentials = credentialRepository.findByTeam(team);
        return credentials.stream()
                .map(cred -> credentialService.convertToResponse(cred, currentUser, null))
                .collect(Collectors.toList());
    }

    private TeamResponse convertToResponse(Team team) {
        List<TeamMember> members = teamMemberRepository.findByTeam(team);
        List<TeamResponse.TeamMemberDTO> memberDTOs = members.stream()
                .map(m -> TeamResponse.TeamMemberDTO.builder()
                        .id(m.getId())
                        .user(userService.convertToDTO(m.getUser()))
                        .role(m.getRole().name())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return TeamResponse.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .owner(userService.convertToDTO(team.getOwner()))
                .members(memberDTOs)
                .createdAt(team.getCreatedAt())
                .build();
    }
}
