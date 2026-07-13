package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConsultationsService {
    ConsultationsDto createConsultation(CreateConsultationRequest request);
    Page<ConsultationsDto> getAllConsultations(Pageable pageable);
    ConsultationsDto getConsultationById(Long id);
    ConsultationsDto updateConsultation(Long id, CreateConsultationRequest request);
    void deleteConsultation(Long id);
    Page<ConsultationsDto> getConsultationsByPatient(Long patientId, Pageable pageable);
    List<ConsultationsDto> getWeightHistoryByPatient(Long patientId);
    List<ConsultationsDto> getRecentConsultations();
}
