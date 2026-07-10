package com.bd.patientsmd.models.responses;

public record AuthResponse(
        String token,
        String fullName,
        String role
) {
}
