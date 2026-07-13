package com.bd.patientsmd.models.mappers;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.requests.CreatePatientRequest;
import com.bd.patientsmd.models.responses.PatientResponse;
import com.bd.patientsmd.models.responses.PatientSummaryResponse;

public class PatientMapper {

    public static PatientDto toDto(Patients patient) {
        if (patient == null) return null;

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
                Math.round(bmi * 100.0) / 100.0,
                patient.getCreatedAt(),
                patient.getUpdatedAt(),
                patient.getCreatedBy(),
                patient.getUpdatedBy()
        );
    }

    public static PatientSummaryResponse toSummaryResponse(Patients patient) {
        if (patient == null) return null;

        return new PatientSummaryResponse(
                patient.getId(),
                patient.getFullName()
        );
    }

    public static PatientResponse toResponse(Patients patient) {
        if (patient == null) return null;

        PatientDto patientDto = toDto(patient);

        return new PatientResponse(
                patientDto.id(),
                patientDto.fullName(),
                patientDto.birthDate(),
                patientDto.phone(),
                patientDto.heightCm(),
                patientDto.initialWeightKg(),
                patientDto.objective(),
                patientDto.initialBmi(),
                UserMapper.toSummaryResponse(patient.getUser())
        );
    }

    public static Patients toEntity(CreatePatientRequest request) {
        return toEntity(request, null);
    }

    public static Patients toEntity(CreatePatientRequest request, Users user) {
        if (request == null) return null;

        return Patients.builder()
                .fullName(request.fullName())
                .birthDate(request.birthDate())
                .phone(request.phone())
                .heightCm(request.heightCm())
                .initialWeightKg(request.initialWeightKg())
                .objective(request.objective())
                .user(user)
                .build();
    }

    public static void updateEntity(Patients patient, CreatePatientRequest request) {
        if (patient == null || request == null) return;

        patient.setFullName(request.fullName());
        patient.setBirthDate(request.birthDate());
        patient.setPhone(request.phone());
        patient.setHeightCm(request.heightCm());
        patient.setInitialWeightKg(request.initialWeightKg());
        patient.setObjective(request.objective());
    }
}
