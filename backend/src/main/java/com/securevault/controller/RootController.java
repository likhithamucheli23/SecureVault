package com.securevault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> getRootStatus() {
        return ResponseEntity.ok(Map.of(
            "name", "SecureVault Backend API",
            "status", "UP",
            "message", "Backend is running successfully. Access the frontend app at http://localhost:5173"
        ));
    }
}
