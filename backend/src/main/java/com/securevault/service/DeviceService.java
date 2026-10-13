package com.securevault.service;

import com.securevault.dto.DeviceDTO;
import com.securevault.entity.User;
import com.securevault.entity.UserDevice;
import com.securevault.exception.ResourceNotFoundException;
import com.securevault.repository.UserDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final UserDeviceRepository userDeviceRepository;

    public DeviceService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    @Transactional
    public UserDevice trackDevice(User user, String userAgent, String ipAddress) {
        String deviceName = parseDeviceName(userAgent);
        String browser = parseBrowser(userAgent);
        String os = parseOS(userAgent);

        Optional<UserDevice> existing = userDeviceRepository.findByUserAndDeviceName(user, deviceName);

        UserDevice device;
        if (existing.isPresent()) {
            device = existing.get();
            device.setIpAddress(ipAddress);
            device.setBrowser(browser);
            device.setOs(os);
            device.setLastActive(LocalDateTime.now());
        } else {
            device = new UserDevice(user, deviceName, browser, os, ipAddress);
        }

        return userDeviceRepository.save(device);
    }

    @Transactional(readOnly = true)
    public boolean isDeviceRevoked(User user, String userAgent) {
        String deviceName = parseDeviceName(userAgent);
        Optional<UserDevice> existing = userDeviceRepository.findByUserAndDeviceName(user, deviceName);
        return existing.map(UserDevice::isRevoked).orElse(false);
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> getUserDevices(User user) {
        return userDeviceRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeviceDTO revokeDevice(Long deviceId, User user) {
        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Device does not belong to user");
        }

        device.setRevoked(true);
        UserDevice updated = userDeviceRepository.save(device);
        return convertToDTO(updated);
    }

    @Transactional
    public DeviceDTO trustDevice(Long deviceId, User user) {
        UserDevice device = userDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        if (!device.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Device does not belong to user");
        }

        device.setTrusted(!device.isTrusted());
        UserDevice updated = userDeviceRepository.save(device);
        return convertToDTO(updated);
    }

    private String parseDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "Unknown Device";
        if (userAgent.contains("iPhone")) return "Apple iPhone";
        if (userAgent.contains("iPad")) return "Apple iPad";
        if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS")) return "Mac Workstation";
        if (userAgent.contains("Windows")) return "Windows PC";
        if (userAgent.contains("Linux")) return "Linux Desktop";
        if (userAgent.contains("Android")) return "Android Mobile";
        return "Desktop Client";
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Browser";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "Safari";
        if (userAgent.contains("Edg")) return "Edge";
        return "Web Browser";
    }

    private String parseOS(String userAgent) {
        if (userAgent == null) return "Unknown OS";
        if (userAgent.contains("Mac OS X")) return "macOS";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iOS") || userAgent.contains("iPhone")) return "iOS";
        return "Operating System";
    }

    public DeviceDTO convertToDTO(UserDevice device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .browser(device.getBrowser())
                .os(device.getOs())
                .ipAddress(device.getIpAddress())
                .trusted(device.isTrusted())
                .revoked(device.isRevoked())
                .lastActive(device.getLastActive())
                .build();
    }
}
