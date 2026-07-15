package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.entites.Consultations;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.mappers.ConsultationMapper;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;
import com.bd.patientsmd.repository.ConsultationRepository;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.security.CurrentUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ConsultationsServiceImpl implements ConsultationsService{

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserService currentUserService;

    @Override
    public ConsultationsDto createConsultation(CreateConsultationRequest request) {
        Patients patient=patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        Consultations consultation= ConsultationMapper.toEntity(request,patient);
        consultation.stampCreated();
        Consultations consultationSaved=consultationRepository.saveAndFlush(consultation);
        return ConsultationMapper.toDto(consultationSaved);
    }

    @Override
    public Page<ConsultationsDto> getAllConsultations(Pageable pageable) {
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> consultationRepository.findByPatientUserId(userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(ConsultationMapper::toDto);
        }

        return consultationRepository.findAll(pageable)
                .map(ConsultationMapper::toDto);
    }

    @Override
    public ConsultationsDto getConsultationById(Long id) {
        Consultations consultation=consultationRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Consultation introuvable"));
        return ConsultationMapper.toDto(consultation);
    }

    @Override
    public ConsultationsDto updateConsultation(Long id, CreateConsultationRequest request) {
        Consultations consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable"));
        Patients patient=patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));

        ConsultationMapper.updateEntity(consultation,request,patient);
        consultation.stampUpdated();
        return ConsultationMapper.toDto(consultationRepository.saveAndFlush(consultation));
    }

    @Override
    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)){
            throw new ResourceNotFoundException("Appointment introuvable");
        }
        consultationRepository.deleteById(id);
    }

    @Override
    public Page<ConsultationsDto> getConsultationsByPatient(Long patientId, Pageable pageable) {
        return consultationRepository.findByPatientId(patientId, pageable)
                .map(ConsultationMapper::toDto);
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
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(consultationRepository::findTop10ByPatientUserIdOrderByConsultationDateDesc)
                    .orElseGet(List::of)
                    .stream()
                    .map(ConsultationMapper::toDto)
                    .toList();
        }

        return consultationRepository.findTop10ByOrderByConsultationDateDesc()
                .stream()
                .map(ConsultationMapper::toDto)
                .toList();
    }

    @Override
    public Page<ConsultationsDto> searchConsultations(String keyword, Pageable pageable) {
        String cleanedKeyword = keyword == null ? "" : keyword.trim();
        LocalDate consultationDate = parseDate(cleanedKeyword);
        Double numericValue = parseNumber(cleanedKeyword);

        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> consultationRepository.searchByPatientUserId(
                            cleanedKeyword,
                            consultationDate,
                            numericValue,
                            userId,
                            pageable
                    ))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(ConsultationMapper::toDto);
        }

        return consultationRepository.search(cleanedKeyword, consultationDate, numericValue, pageable)
                .map(ConsultationMapper::toDto);
    }

    private LocalDate parseDate(String keyword) {
        try {
            return LocalDate.parse(keyword);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Double parseNumber(String keyword) {
        try {
            return Double.valueOf(keyword.replace(',', '.'));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
