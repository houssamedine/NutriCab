package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.requests.CreatePatientRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientDto createPatient(CreatePatientRequest patientRequest);
    PatientDto updatePatient(Long id,CreatePatientRequest patientRequest);
    Page<PatientDto> getAllPatients(Pageable pageable);
    PatientDto getPatientById(Long id);
    void deletePatient(Long id);
    Page<PatientDto> searhPatient(String keyword, Pageable pageable);
}
