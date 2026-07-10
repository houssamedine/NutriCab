package com.bd.patientsmd.models.responses;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String fullName,
        LocalDate birthDate,
        String phone,
        Double heightCm,
        Double initialWeightKg,
        String objective,
        Double initialBmi,
        UserSummaryResponse user
) {
}
