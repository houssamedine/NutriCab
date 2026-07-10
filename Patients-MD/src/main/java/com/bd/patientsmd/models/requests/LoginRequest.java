package com.bd.patientsmd.models.requests;

public record LoginRequest(
        String email,
        String password
) {
}
