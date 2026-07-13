package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.RefreshToken;
import com.bd.patientsmd.models.entites.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByExpiresAtBefore(Instant now);
    void deleteByUser(Users user);
}
