package com.bd.patientsmd.models.requests;

import com.bd.patientsmd.models.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        @NotNull(message = "L'ID du patient est obligatoire")
        Long patientId,

        @NotNull(message = "La date du rendez-vous est obligatoire")
        LocalDateTime appointmentDate,

        AppointmentStatus status,

        String notes
) {}
