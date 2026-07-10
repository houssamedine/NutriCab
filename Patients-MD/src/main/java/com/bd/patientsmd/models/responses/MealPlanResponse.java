package com.bd.patientsmd.models.responses;

public record MealPlanResponse(
        Long id,
        PatientSummaryResponse patient,
        String title,
        String objective,
        Integer calories,
        String content
) {
}
