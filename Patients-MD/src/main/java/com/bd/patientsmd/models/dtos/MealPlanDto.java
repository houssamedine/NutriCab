package com.bd.patientsmd.models.dtos;

import java.time.Instant;

public record MealPlanDto(
        Long id,
        PatientDto patient,
        String title,
        String objective,
        Integer calories,
        String content,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
