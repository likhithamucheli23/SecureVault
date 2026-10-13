package com.securevault.repository;

import com.securevault.entity.Credential;
import com.securevault.entity.CredentialSharing;
import com.securevault.entity.SharingStatus;
import com.securevault.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialSharingRepository extends JpaRepository<CredentialSharing, Long> {
    List<CredentialSharing> findBySharedUserAndSharingStatus(User sharedUser, SharingStatus sharingStatus);
    List<CredentialSharing> findByCredential(Credential credential);
    Optional<CredentialSharing> findByCredentialAndSharedUser(Credential credential, User sharedUser);
    void deleteByCredentialAndSharedUser(Credential credential, User sharedUser);
}
