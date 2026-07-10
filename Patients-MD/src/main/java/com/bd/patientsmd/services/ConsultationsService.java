package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;

import java.util.List;

public interface ConsultationsService {
    ConsultationsDto createConsultation(CreateConsultationRequest request);
    List<ConsultationsDto> getAllConsultations();
    ConsultationsDto getConsultationById(Long id);
    ConsultationsDto updateConsultation(Long id, CreateConsultationRequest request);
    void deleteConsultation(Long id);
    List<ConsultationsDto> getConsultationsByPatient(Long patientId);
    List<ConsultationsDto> getWeightHistoryByPatient(Long patientId);
    List<ConsultationsDto> getRecentConsultations();
}
