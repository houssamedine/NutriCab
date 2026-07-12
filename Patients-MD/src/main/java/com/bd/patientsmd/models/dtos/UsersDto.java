package com.bd.patientsmd.models.dtos;

import com.bd.patientsmd.models.enums.UserRole;

import java.util.List;

public record UsersDto(
        Long id,
        String fullName,
        String email,
        UserRole role,
        boolean active,
        List<PatientDto> patients
) {
}
