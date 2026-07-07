package com.bd.patientsmd.models.mappers;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.entites.Patients;

public class PatientMapper {

    public static PatientDto toDto(Patients patient) {
        double heightM = patient.getHeightCm() / 100;
        double bmi = patient.getInitialWeightKg() / (heightM * heightM);

        return new PatientDto(
                patient.getId(),
                patient.getFullName(),
                patient.getBirthDate(),
                patient.getPhone(),
                patient.getHeightCm(),
                patient.getInitialWeightKg(),
                patient.getObjective(),
                Math.round(bmi * 100.0) / 100.0
        );
    }
}
