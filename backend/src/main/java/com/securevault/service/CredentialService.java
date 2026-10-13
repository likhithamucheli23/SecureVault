package com.securevault.service;

import com.securevault.dto.CredentialRequest;
import com.securevault.dto.CredentialResponse;
import com.securevault.entity.*;
import com.securevault.exception.ResourceNotFoundException;
import com.securevault.exception.UnauthorizedAccessException;
import com.securevault.repository.CredentialRepository;
import com.securevault.repository.CredentialSharingRepository;
import com.securevault.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final CredentialSharingRepository sharingRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialRepository credentialRepository, CredentialSharingRepository sharingRepository, TeamRepository teamRepository, UserService userService, EncryptionService encryptionService) {
        this.credentialRepository = credentialRepository;
        this.sharingRepository = sharingRepository;
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    @Transactional
    public CredentialResponse createCredential(CredentialRequest request) {
        User currentUser = userService.getAuthenticatedUser();

        Team team = null;
        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        }

        String rawPassword = request.getPassword() != null ? request.getPassword() : "";
        String encryptedPassword = encryptionService.encrypt(rawPassword);
        String encryptedNotes = request.getNotes() != null ? encryptionService.encrypt(request.getNotes()) : null;

        Credential credential = Credential.builder()
                .owner(currentUser)
                .title(request.getTitle())
                .username(request.getUsername() != null ? request.getUsername() : "")
                .encryptedPassword(encryptedPassword)
                .encryptedNotes(encryptedNotes)
                .website(request.getWebsite())
                .category(request.getCategory() != null ? request.getCategory() : "General")
                .type(request.getType() != null ? request.getType() : CredentialType.LOGIN)
                .isFavorite(request.isFavorite())
                .team(team)
                .build();

        Credential saved = credentialRepository.save(credential);
        return convertToResponse(saved, currentUser, null);
    }

    @Transactional(readOnly = true)
    public List<CredentialResponse> getUserCredentials() {
        User currentUser = userService.getAuthenticatedUser();
        List<Credential> credentials = credentialRepository.findByOwner(currentUser);

        return credentials.stream()
                .map(cred -> convertToResponse(cred, currentUser, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CredentialResponse> searchAndFilterCredentials(String query, String category, CredentialType type, Boolean favorite) {
        User currentUser = userService.getAuthenticatedUser();
        List<Credential> credentials = credentialRepository.findByOwner(currentUser);

        return credentials.stream()
                .filter(cred -> {
                    if (query != null && !query.isBlank()) {
                        String q = query.toLowerCase();
                        boolean matchTitle = cred.getTitle() != null && cred.getTitle().toLowerCase().contains(q);
                        boolean matchUser = cred.getUsername() != null && cred.getUsername().toLowerCase().contains(q);
                        boolean matchCategory = cred.getCategory() != null && cred.getCategory().toLowerCase().contains(q);
                        if (!matchTitle && !matchUser && !matchCategory) return false;
                    }
                    if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
                        if (cred.getCategory() == null || !cred.getCategory().equalsIgnoreCase(category)) return false;
                    }
                    if (type != null) {
                        if (cred.getType() != type) return false;
                    }
                    if (favorite != null) {
                        if (cred.isFavorite() != favorite) return false;
                    }
                    return true;
                })
                .map(cred -> convertToResponse(cred, currentUser, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CredentialResponse getCredentialById(Long id) {
        User currentUser = userService.getAuthenticatedUser();
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));

        if (credential.getOwner().getId().equals(currentUser.getId())) {
            return convertToResponse(credential, currentUser, null);
        }

        Optional<CredentialSharing> sharingOpt = sharingRepository.findByCredentialAndSharedUser(credential, currentUser);
        if (sharingOpt.isPresent()) {
            CredentialSharing sharing = sharingOpt.get();
            validateSharingActive(sharing);
            return convertToResponse(credential, currentUser, sharing);
        }

        throw new UnauthorizedAccessException("You do not have permission to view this credential");
    }

    @Transactional
    public CredentialResponse updateCredential(Long id, CredentialRequest request) {
        User currentUser = userService.getAuthenticatedUser();
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));

        boolean isOwner = credential.getOwner().getId().equals(currentUser.getId());
        CredentialSharing sharing = null;

        if (!isOwner) {
            sharing = sharingRepository.findByCredentialAndSharedUser(credential, currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("You do not have permission to modify this credential"));

            validateSharingActive(sharing);

            if (sharing.getPermissionLevel() == PermissionLevel.VIEW_ONLY) {
                throw new UnauthorizedAccessException("View-only access level does not permit editing credentials");
            }
        }

        credential.setTitle(request.getTitle());
        if (request.getUsername() != null) {
            credential.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            credential.setEncryptedPassword(encryptionService.encrypt(request.getPassword()));
        }
        if (request.getNotes() != null) {
            credential.setEncryptedNotes(encryptionService.encrypt(request.getNotes()));
        }
        if (request.getWebsite() != null) {
            credential.setWebsite(request.getWebsite());
        }
        if (request.getCategory() != null) {
            credential.setCategory(request.getCategory());
        }
        if (request.getType() != null) {
            credential.setType(request.getType());
        }
        credential.setFavorite(request.isFavorite());

        if (request.getTeamId() != null) {
            Team team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            credential.setTeam(team);
        }

        Credential updated = credentialRepository.save(credential);
        return convertToResponse(updated, currentUser, sharing);
    }

    @Transactional
    public CredentialResponse toggleFavorite(Long id) {
        User currentUser = userService.getAuthenticatedUser();
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));

        if (!credential.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("Only the owner can toggle favorite status");
        }

        credential.setFavorite(!credential.isFavorite());
        Credential updated = credentialRepository.save(credential);
        return convertToResponse(updated, currentUser, null);
    }

    @Transactional
    public void deleteCredential(Long id) {
        User currentUser = userService.getAuthenticatedUser();
        Credential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));

        boolean isOwner = credential.getOwner().getId().equals(currentUser.getId());

        if (!isOwner) {
            CredentialSharing sharing = sharingRepository.findByCredentialAndSharedUser(credential, currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("You do not have permission to delete this credential"));

            validateSharingActive(sharing);

            if (sharing.getPermissionLevel() != PermissionLevel.FULL_MANAGEMENT) {
                throw new UnauthorizedAccessException("Deleting a credential requires owner or Full Management permissions");
            }
        }

        List<CredentialSharing> shares = sharingRepository.findByCredential(credential);
        sharingRepository.deleteAll(shares);

        credentialRepository.delete(credential);
    }

    private void validateSharingActive(CredentialSharing sharing) {
        if (sharing.getSharingStatus() == SharingStatus.REVOKED) {
            throw new UnauthorizedAccessException("This shared access has been revoked");
        }
        if (sharing.getExpirationDate() != null && sharing.getExpirationDate().isBefore(LocalDateTime.now())) {
            sharing.setSharingStatus(SharingStatus.EXPIRED);
            sharingRepository.save(sharing);
            throw new UnauthorizedAccessException("This shared access has expired");
        }
    }

    public CredentialResponse convertToResponse(Credential credential, User caller, CredentialSharing sharing) {
        String decryptedPassword = encryptionService.decrypt(credential.getEncryptedPassword());
        String decryptedNotes = credential.getEncryptedNotes() != null ?
                encryptionService.decrypt(credential.getEncryptedNotes()) : null;

        String permissionStr = "OWNER";
        String sharingStatusStr = "ACTIVE";
        LocalDateTime expirationDate = null;

        if (sharing != null) {
            permissionStr = sharing.getPermissionLevel().name();
            sharingStatusStr = sharing.getSharingStatus().name();
            expirationDate = sharing.getExpirationDate();
        }

        return CredentialResponse.builder()
                .id(credential.getId())
                .title(credential.getTitle())
                .username(credential.getUsername())
                .password(decryptedPassword)
                .website(credential.getWebsite())
                .notes(decryptedNotes)
                .category(credential.getCategory())
                .type(credential.getType())
                .favorite(credential.isFavorite())
                .owner(userService.convertToDTO(credential.getOwner()))
                .teamId(credential.getTeam() != null ? credential.getTeam().getId() : null)
                .teamName(credential.getTeam() != null ? credential.getTeam().getTeamName() : null)
                .permissionLevel(permissionStr)
                .sharingStatus(sharingStatusStr)
                .expirationDate(expirationDate)
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }
}
