package com.bd.patientsmd.models.mappers;

import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.entites.Consultations;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;
import com.bd.patientsmd.models.responses.ConsultationResponse;

public class ConsultationMapper {

    public static ConsultationsDto toDto(Consultations consultation) {
        if (consultation == null) return null;

        return new ConsultationsDto(
                consultation.getId(),
                PatientMapper.toDto(consultation.getPatient()),
                consultation.getConsultationDate(),
                consultation.getWeightKg(),
                consultation.getWaistCm(),
                consultation.getNotes(),
                consultation.getRecommendations(),
                consultation.getCreatedAt(),
                consultation.getUpdatedAt(),
                consultation.getCreatedBy(),
                consultation.getUpdatedBy()
        );
    }

    public static ConsultationResponse toResponse(Consultations consultation) {
        if (consultation == null) return null;

        return new ConsultationResponse(
                consultation.getId(),
                PatientMapper.toSummaryResponse(consultation.getPatient()),
                consultation.getConsultationDate(),
                consultation.getWeightKg(),
                consultation.getWaistCm(),
                consultation.getNotes(),
                consultation.getRecommendations()
        );
    }

    public static Consultations toEntity(CreateConsultationRequest request, Patients patient) {
        if (request == null) return null;

        return Consultations.builder()
                .patient(patient)
                .consultationDate(request.consultationDate())
                .weightKg(request.weightKg())
                .waistCm(request.waistCm())
                .notes(request.notes())
                .recommendations(request.recommendations())
                .build();
    }

    public static void updateEntity(Consultations consultation, CreateConsultationRequest request, Patients patient) {
        if (consultation == null || request == null) return;

        consultation.setPatient(patient);
        consultation.setConsultationDate(request.consultationDate());
        consultation.setWeightKg(request.weightKg());
        consultation.setWaistCm(request.waistCm());
        consultation.setNotes(request.notes());
        consultation.setRecommendations(request.recommendations());
    }
}
