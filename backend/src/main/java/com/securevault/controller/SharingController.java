package com.securevault.controller;

import com.securevault.dto.CredentialResponse;
import com.securevault.dto.ShareCredentialRequest;
import com.securevault.dto.ShareCredentialResponse;
import com.securevault.service.SharingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class SharingController {

    private final SharingService sharingService;

    public SharingController(SharingService sharingService) {
        this.sharingService = sharingService;
    }

    @PostMapping("/credentials/{id}/share")
    public ResponseEntity<ShareCredentialResponse> shareCredential(
            @PathVariable Long id,
            @Valid @RequestBody ShareCredentialRequest request
    ) {
        ShareCredentialResponse response = sharingService.shareCredential(id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<List<CredentialResponse>> getSharedWithMe() {
        List<CredentialResponse> sharedCredentials = sharingService.getSharedWithMe();
        return ResponseEntity.ok(sharedCredentials);
    }

    @DeleteMapping("/credentials/{id}/share/{userId}")
    public ResponseEntity<Void> revokeSharing(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        sharingService.revokeSharing(id, userId);
        return ResponseEntity.noContent().build();
    }
}
