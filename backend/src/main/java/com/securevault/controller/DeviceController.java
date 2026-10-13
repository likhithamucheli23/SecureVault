package com.securevault.controller;

import com.securevault.dto.DeviceDTO;
import com.securevault.entity.User;
import com.securevault.service.DeviceService;
import com.securevault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final UserService userService;

    public DeviceController(DeviceService deviceService, UserService userService) {
        this.deviceService = deviceService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        User currentUser = userService.getAuthenticatedUser();
        List<DeviceDTO> devices = deviceService.getUserDevices(currentUser);
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<DeviceDTO> revokeDevice(@PathVariable("id") Long id) {
        User currentUser = userService.getAuthenticatedUser();
        DeviceDTO updated = deviceService.revokeDevice(id, currentUser);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/trust")
    public ResponseEntity<DeviceDTO> trustDevice(@PathVariable("id") Long id) {
        User currentUser = userService.getAuthenticatedUser();
        DeviceDTO updated = deviceService.trustDevice(id, currentUser);
        return ResponseEntity.ok(updated);
    }
}
