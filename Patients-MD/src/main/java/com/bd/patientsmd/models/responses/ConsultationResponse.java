package com.bd.patientsmd.models.responses;

import java.time.LocalDate;

public record ConsultationResponse(
        Long id,
        PatientSummaryResponse patient,
        LocalDate consultationDate,
        Double weightKg,
        Double waistCm,
        String notes,
        String recommendations
) {
}
