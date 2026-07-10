package com.bd.patientsmd.models.responses;

import com.bd.patientsmd.models.enums.UserRole;

public record UserSummaryResponse(
        Long id,
        String fullName,
        String email,
        UserRole role
) {
}
