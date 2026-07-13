package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.InvalidCredentialsException;
import com.bd.patientsmd.models.entites.RefreshToken;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.responses.AuthResponse;
import com.bd.patientsmd.repository.RefreshTokenRepository;
import com.bd.patientsmd.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final long refreshExpirationDays;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            @Value("${app.security.jwt.refresh-expiration-days}") long refreshExpirationDays
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String createRefreshToken(Users user) {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS))
                .build();
        refreshToken.stampCreated();

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    public AuthResponse refreshAccessToken(String token) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Refresh token invalide"));

        validateRefreshToken(oldRefreshToken);

        Users user = oldRefreshToken.getUser();
        oldRefreshToken.setRevoked(true);
        oldRefreshToken.stampUpdated();

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = createRefreshToken(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getFullName(),
                user.getRole().name()
        );
    }

    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshToken.stampUpdated();
                });
    }

    private void validateRefreshToken(RefreshToken refreshToken) {
        if (refreshToken.isRevoked()) {
            throw new InvalidCredentialsException("Refresh token revoque");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshToken.stampUpdated();
            throw new InvalidCredentialsException("Refresh token expire");
        }

        if (!refreshToken.getUser().isActive()) {
            throw new InvalidCredentialsException("Utilisateur desactive");
        }
    }
}
