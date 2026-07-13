package com.bd.patientsmd.models.dtos;

import java.time.Instant;
import java.time.LocalDate;

public record PatientDto(
        Long id,String fullName,
        LocalDate birthDate,
        String phone,Double heightCm,
        Double initialWeightKg,
        String objective,
        Double initialBmi,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
