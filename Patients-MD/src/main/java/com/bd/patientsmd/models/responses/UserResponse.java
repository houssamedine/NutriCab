package com.bd.patientsmd.models.responses;

import com.bd.patientsmd.models.enums.UserRole;

import java.util.List;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        List<PatientSummaryResponse> patients
) {
}
