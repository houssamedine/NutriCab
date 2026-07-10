package com.bd.patientsmd.models.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        @NotNull(message = "L'ID du patient est obligatoire")
        Long patientId,

        @NotNull(message = "La date du rendez-vous est obligatoire")
        @Future(message = "La date doit être dans le futur")
        LocalDateTime appointmentDate,

        String notes
) {}