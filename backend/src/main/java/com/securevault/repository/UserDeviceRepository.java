package com.securevault.repository;

import com.securevault.entity.User;
import com.securevault.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    List<UserDevice> findByUser(User user);
    Optional<UserDevice> findByUserAndDeviceName(User user, String deviceName);
    Optional<UserDevice> findByUserAndDeviceNameAndIpAddress(User user, String deviceName, String ipAddress);
}
