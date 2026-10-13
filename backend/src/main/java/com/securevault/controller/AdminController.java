package com.securevault.controller;

import com.securevault.dto.SecurityEventDTO;
import com.securevault.dto.UserDTO;
import com.securevault.entity.Role;
import com.securevault.entity.User;
import com.securevault.exception.ResourceNotFoundException;
import com.securevault.repository.SecurityEventRepository;
import com.securevault.repository.UserRepository;
import com.securevault.service.SecurityEventService;
import com.securevault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final SecurityEventRepository securityEventRepository;
    private final SecurityEventService securityEventService;

    public AdminController(UserRepository userRepository,
                           UserService userService,
                           SecurityEventRepository securityEventRepository,
                           SecurityEventService securityEventService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.securityEventRepository = securityEventRepository;
        this.securityEventService = securityEventService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String roleStr = body.get("role");
        Role role = Role.valueOf(roleStr.toUpperCase());
        user.setRole(role);
        User updated = userRepository.save(user);
        return ResponseEntity.ok(userService.convertToDTO(updated));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<SecurityEventDTO>> getAllAuditLogs() {
        List<SecurityEventDTO> logs = securityEventRepository.findAll().stream()
                .map(securityEventService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }
}
