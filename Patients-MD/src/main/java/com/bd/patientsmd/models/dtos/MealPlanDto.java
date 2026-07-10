package com.bd.patientsmd.models.dtos;

public record MealPlanDto(
        Long id,
        PatientDto patient,
        String title,
        String objective,
        Integer calories,
        String content
) {
}
