package com.securevault.service;

import com.securevault.dto.CredentialResponse;
import com.securevault.dto.ShareCredentialRequest;
import com.securevault.dto.ShareCredentialResponse;
import com.securevault.entity.*;
import com.securevault.exception.BadRequestException;
import com.securevault.exception.ResourceNotFoundException;
import com.securevault.exception.UnauthorizedAccessException;
import com.securevault.repository.CredentialRepository;
import com.securevault.repository.CredentialSharingRepository;
import com.securevault.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SharingService {

    private final CredentialRepository credentialRepository;
    private final CredentialSharingRepository sharingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CredentialService credentialService;

    public SharingService(CredentialRepository credentialRepository, CredentialSharingRepository sharingRepository, UserRepository userRepository, UserService userService, CredentialService credentialService) {
        this.credentialRepository = credentialRepository;
        this.sharingRepository = sharingRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.credentialService = credentialService;
    }

    @Transactional
    public ShareCredentialResponse shareCredential(Long credentialId, ShareCredentialRequest request) {
        User currentUser = userService.getAuthenticatedUser();
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found"));

        boolean isOwner = credential.getOwner().getId().equals(currentUser.getId());
        if (!isOwner) {
            CredentialSharing existingShare = sharingRepository.findByCredentialAndSharedUser(credential, currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("You are not authorized to share this credential"));
            if (existingShare.getPermissionLevel() != PermissionLevel.FULL_MANAGEMENT) {
                throw new UnauthorizedAccessException("Sharing credentials requires owner or Full Management permission");
            }
        }

        User recipient = userRepository.findByEmail(request.getUserEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient user with email " + request.getUserEmail() + " not found"));

        if (recipient.getId().equals(credential.getOwner().getId())) {
            throw new BadRequestException("You cannot share a credential with its owner");
        }

        Optional<CredentialSharing> existingOpt = sharingRepository.findByCredentialAndSharedUser(credential, recipient);
        CredentialSharing share;
        if (existingOpt.isPresent()) {
            share = existingOpt.get();
            share.setPermissionLevel(request.getPermissionLevel());
            share.setSharingStatus(SharingStatus.ACTIVE);
            share.setExpirationDate(request.getExpirationDate());
        } else {
            share = CredentialSharing.builder()
                    .credential(credential)
                    .owner(credential.getOwner())
                    .sharedUser(recipient)
                    .permissionLevel(request.getPermissionLevel())
                    .sharingStatus(SharingStatus.ACTIVE)
                    .expirationDate(request.getExpirationDate())
                    .build();
        }

        CredentialSharing saved = sharingRepository.save(share);
        return convertToShareResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CredentialResponse> getSharedWithMe() {
        User currentUser = userService.getAuthenticatedUser();
        List<CredentialSharing> shares = sharingRepository.findBySharedUserAndSharingStatus(currentUser, SharingStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        return shares.stream()
                .filter(share -> share.getExpirationDate() == null || share.getExpirationDate().isAfter(now))
                .map(share -> credentialService.convertToResponse(share.getCredential(), currentUser, share))
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeSharing(Long credentialId, Long userId) {
        User currentUser = userService.getAuthenticatedUser();
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found"));

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        boolean isOwner = credential.getOwner().getId().equals(currentUser.getId());
        boolean isSelfRemoval = currentUser.getId().equals(targetUser.getId());

        if (!isOwner && !isSelfRemoval) {
            CredentialSharing existingShare = sharingRepository.findByCredentialAndSharedUser(credential, currentUser)
                    .orElseThrow(() -> new UnauthorizedAccessException("Not authorized to manage access for this credential"));
            if (existingShare.getPermissionLevel() != PermissionLevel.FULL_MANAGEMENT) {
                throw new UnauthorizedAccessException("Revoking sharing requires owner or Full Management permission");
            }
        }

        CredentialSharing share = sharingRepository.findByCredentialAndSharedUser(credential, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("Sharing record not found"));

        share.setSharingStatus(SharingStatus.REVOKED);
        sharingRepository.save(share);
    }

    public ShareCredentialResponse convertToShareResponse(CredentialSharing share) {
        return ShareCredentialResponse.builder()
                .id(share.getId())
                .credentialId(share.getCredential().getId())
                .credentialTitle(share.getCredential().getTitle())
                .owner(userService.convertToDTO(share.getOwner()))
                .sharedUser(userService.convertToDTO(share.getSharedUser()))
                .permissionLevel(share.getPermissionLevel())
                .sharingStatus(share.getSharingStatus())
                .expirationDate(share.getExpirationDate())
                .createdAt(share.getCreatedAt())
                .build();
    }
}
