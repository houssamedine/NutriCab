package com.bd.patientsmd.models.responses;

import com.bd.patientsmd.models.enums.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        PatientSummaryResponse patient,
        LocalDateTime appointmentDate,
        AppointmentStatus status,
        String notes
) {
}
