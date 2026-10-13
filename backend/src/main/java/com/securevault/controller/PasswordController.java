package com.securevault.controller;

import com.securevault.dto.PasswordGeneratorOptions;
import com.securevault.dto.PasswordStrengthResponse;
import com.securevault.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generatePassword(@RequestBody(required = false) PasswordGeneratorOptions options) {
        if (options == null) {
            options = new PasswordGeneratorOptions();
        }
        String password = passwordService.generatePassword(options);
        return ResponseEntity.ok(Map.of("password", password));
    }

    @PostMapping("/analyze")
    public ResponseEntity<PasswordStrengthResponse> analyzePassword(@RequestBody Map<String, String> payload) {
        String password = payload.get("password");
        PasswordStrengthResponse response = passwordService.analyzeStrength(password);
        return ResponseEntity.ok(response);
    }
}
