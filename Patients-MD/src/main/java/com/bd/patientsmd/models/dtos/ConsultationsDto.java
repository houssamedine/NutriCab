package com.bd.patientsmd.models.dtos;

import java.time.Instant;
import java.time.LocalDate;

public record ConsultationsDto(
        Long id,
        PatientDto patient,
        LocalDate consultationDate,
        Double weightKg,
        Double waistCm,
        String notes,
        String recommendations,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
