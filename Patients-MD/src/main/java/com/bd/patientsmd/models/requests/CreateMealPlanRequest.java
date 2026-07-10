package com.bd.patientsmd.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMealPlanRequest(
        @NotNull(message = "L'ID du patient est obligatoire")
        Long patientId,

        @NotBlank(message = "Le titre est obligatoire")
        String title,

        String objective,

        @Positive(message = "Les calories doivent etre positives")
        Integer calories,

        String content
) {
}
