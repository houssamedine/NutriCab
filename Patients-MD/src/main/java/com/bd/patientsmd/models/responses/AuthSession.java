package com.bd.patientsmd.models.responses;

public record AuthSession(
        String accessToken,
        String refreshToken,
        String fullName,
        String role
) {
}
