package com.bd.patientsmd.models.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateConsultationRequest(
        @NotNull(message = "L'ID du patient est obligatoire")
        Long patientId,

        @NotNull(message = "La date de consultation est obligatoire")
        LocalDate consultationDate,

        @NotNull(message = "Le poids est obligatoire")
        @Positive(message = "Le poids doit etre positif")
        Double weightKg,

        @Positive(message = "Le tour de taille doit etre positif")
        Double waistCm,

        String notes,

        String recommendations
) {
}
