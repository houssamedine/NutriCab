package com.bd.patientsmd.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    public Optional<Long> getCurrentUserId() {
        return getCurrentJwt()
                .map(jwt -> jwt.getClaim("userId"))
                .map(this::toLong);
    }

    public boolean hasRole(String role) {
        return getCurrentJwt()
                .map(jwt -> role.equals(jwt.getClaimAsString("role")))
                .orElse(false);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isNutritionist() {
        return hasRole("NUTRITIONIST");
    }

    public boolean isSecretary() {
        return hasRole("SECRETARY");
    }

    public boolean isPatient() {
        return hasRole("PATIENT");
    }

    private Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }

        return Optional.of(jwt);
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(value.toString());
    }
}
