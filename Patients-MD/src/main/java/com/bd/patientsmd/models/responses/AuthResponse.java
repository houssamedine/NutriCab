package com.bd.patientsmd.models.responses;

public record AuthResponse(
        String token,
        String refreshToken,
        String fullName,
        String role
) {
}
