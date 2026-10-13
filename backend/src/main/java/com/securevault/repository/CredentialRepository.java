package com.securevault.repository;

import com.securevault.entity.Credential;
import com.securevault.entity.CredentialType;
import com.securevault.entity.User;
import com.securevault.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    List<Credential> findByOwner(User owner);
    List<Credential> findByTeam(Team team);
    List<Credential> findByOwnerAndCategory(User owner, String category);
    List<Credential> findByOwnerAndType(User owner, CredentialType type);
    List<Credential> findByOwnerAndIsFavorite(User owner, boolean isFavorite);
    List<Credential> findByOwnerAndTitleContainingIgnoreCase(User owner, String title);
}
