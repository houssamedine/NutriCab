package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.mappers.CreatePatientRequest;

import java.util.List;

public interface PatientService {
    PatientDto createPatient(CreatePatientRequest patientRequest);
    PatientDto updatePatient(Long id,CreatePatientRequest patientRequest);
    List<PatientDto> getAllPatients();
    PatientDto getPatientById(Long id);
    void deletePatient(Long id);
    List<PatientDto> searhPatient(String keyword);
}
