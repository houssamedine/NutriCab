package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.mappers.CreatePatientRequest;
import com.bd.patientsmd.models.mappers.PatientMapper;
import com.bd.patientsmd.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;

    @Override
    public PatientDto createPatient(CreatePatientRequest patientRequest) {
        Patients patient = Patients.builder()
                .fullName(patientRequest.fullName())
                .birthDate(patientRequest.birthDate())
                .phone(patientRequest.phone())
                .heightCm(patientRequest.heightCm())
                .initialWeightKg(patientRequest.initialWeightKg())
                .objective(patientRequest.objective())
                .build();
        Patients savedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(savedPatient);
    }

    @Override
    public PatientDto updatePatient(Long id, CreatePatientRequest patientRequest) {
        Patients patient=patientRepository.findById(id)
                .orElseThrow(()->new RuntimeException(
                        "Patient introuvable"
                ));
        patient.setFullName(patientRequest.fullName());
        patient.setBirthDate(patientRequest.birthDate());
        patient.setPhone(patientRequest.phone());
        patient.setHeightCm(patientRequest.heightCm());
        patient.setInitialWeightKg(patientRequest.initialWeightKg());
        patient.setObjective(patientRequest.objective());
        return PatientMapper.toDto(patient);
    }

    @Override
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    @Override
    public PatientDto getPatientById(Long id) {
        Patients patient=patientRepository.findById(id)
                .orElseThrow(()->new RuntimeException(
                        "Patient introuvable"
                ));
        return PatientMapper.toDto(patient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)){
            throw new RuntimeException("Patient introuvable");
        }
        patientRepository.deleteById(id);
    }

    @Override
    public List<PatientDto> searhPatient(String keyword) {
        return patientRepository.findByFullNameContainingIgnoreCase(keyword)
                .stream()
                .map(PatientMapper::toDto)
                .toList();
    }
}
