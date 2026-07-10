package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.entites.Consultations;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.mappers.ConsultationMapper;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;
import com.bd.patientsmd.repository.ConsultationRepository;
import com.bd.patientsmd.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ConsultationsServiceImpl implements ConsultationsService{

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;



    @Override
    public ConsultationsDto createConsultation(CreateConsultationRequest request) {
        Patients patient=patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        Consultations consultation= ConsultationMapper.toEntity(request,patient);
        Consultations consultationSaved=consultationRepository.save(consultation);
        return ConsultationMapper.toDto(consultationSaved);
    }

    @Override
    public List<ConsultationsDto> getAllConsultations() {
        return consultationRepository.findAll()
                .stream()
                .map(ConsultationMapper::toDto)
                .toList();
    }

    @Override
    public ConsultationsDto getConsultationById(Long id) {
        Consultations consultation=consultationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Consultation introuvable"));
        return ConsultationMapper.toDto(consultation);
    }

    @Override
    public ConsultationsDto updateConsultation(Long id, CreateConsultationRequest request) {
        Consultations consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable"));
        Patients patient=patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));

        ConsultationMapper.updateEntity(consultation,request,patient);
        return ConsultationMapper.toDto(consultation);
    }

    @Override
    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)){
            throw new ResourceNotFoundException("Appointment introuvable");
        }
        consultationRepository.deleteById(id);
    }

    @Override
    public List<ConsultationsDto> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId)
                .stream()
                .map(ConsultationMapper::toDto)
                .toList();
    }

    @Override
    public List<ConsultationsDto> getWeightHistoryByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient introuvable");
        }

        return consultationRepository.findByPatientIdOrderByConsultationDateAsc(patientId)
                .stream()
                .map(ConsultationMapper::toDto)
                .toList();
    }

    @Override
    public List<ConsultationsDto> getRecentConsultations() {
        return consultationRepository.findTop10ByOrderByConsultationDateDesc()
                .stream()
                .map(ConsultationMapper::toDto)
                .toList();
    }
}
