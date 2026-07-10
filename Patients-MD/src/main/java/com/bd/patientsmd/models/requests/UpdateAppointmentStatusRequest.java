package com.bd.patientsmd.models.requests;

import com.bd.patientsmd.models.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAppointmentStatusRequest(
        @NotNull(message = "Le statut est obligatoire")
        AppointmentStatus status
) {
}
