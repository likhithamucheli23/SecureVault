package com.securevault.repository;

import com.securevault.entity.SecurityEvent;
import com.securevault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    List<SecurityEvent> findByUserOrderByTimestampDesc(User user);
    List<SecurityEvent> findByAttemptedEmailOrderByTimestampDesc(String attemptedEmail);

    List<SecurityEvent> findByUserAndTimestampAfterOrderByTimestampDesc(User user, LocalDateTime since);
    List<SecurityEvent> findByAttemptedEmailAndTimestampAfterOrderByTimestampDesc(String attemptedEmail, LocalDateTime since);
}
