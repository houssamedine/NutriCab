package com.bd.patientsmd.models.dtos;

import com.bd.patientsmd.models.enums.AppointmentStatus;

import java.time.Instant;
import java.time.LocalDateTime;

public record AppointmentDto(
        Long id,
        PatientDto patient,
        LocalDateTime appointmentDate,
        AppointmentStatus status,
        String notes,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy
) {
}
