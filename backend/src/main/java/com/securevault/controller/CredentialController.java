package com.securevault.controller;

import com.securevault.dto.CredentialRequest;
import com.securevault.dto.CredentialResponse;
import com.securevault.entity.CredentialType;
import com.securevault.service.CredentialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping
    public ResponseEntity<CredentialResponse> createCredential(@Valid @RequestBody CredentialRequest request) {
        CredentialResponse response = credentialService.createCredential(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CredentialResponse>> getUserCredentials() {
        List<CredentialResponse> credentials = credentialService.getUserCredentials();
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CredentialResponse>> searchCredentials(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CredentialType type,
            @RequestParam(required = false) Boolean favorite
    ) {
        List<CredentialResponse> credentials = credentialService.searchAndFilterCredentials(query, category, type, favorite);
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CredentialResponse> getCredentialById(@PathVariable("id") Long id) {
        CredentialResponse response = credentialService.getCredentialById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CredentialResponse> updateCredential(
            @PathVariable("id") Long id,
            @Valid @RequestBody CredentialRequest request
    ) {
        CredentialResponse response = credentialService.updateCredential(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<CredentialResponse> toggleFavorite(@PathVariable("id") Long id) {
        CredentialResponse response = credentialService.toggleFavorite(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable("id") Long id) {
        credentialService.deleteCredential(id);
        return ResponseEntity.noContent().build();
    }
}
