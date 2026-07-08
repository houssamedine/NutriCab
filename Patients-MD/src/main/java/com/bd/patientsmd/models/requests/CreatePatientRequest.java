package com.bd.patientsmd.models.mappers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;


public record CreatePatientRequest(
        @NotBlank(message = "Le nom complet est obligatoire")
        String fullName,
        @NotNull(message = "La date de naissance est obligatoire")
        LocalDate birthDate,
        @NotBlank(message = "Le téléphone est obligatoire")
        String phone,
        @NotNull(message = "La taille est obligatoire")
        @Positive(message = "La taille doit être positive")
        Double heightCm,
        @NotNull(message = "Le poids initial est obligatoire")
        @Positive(message = "Le poids doit être positif")
        Double initialWeightKg,
        String objective
) {
}
