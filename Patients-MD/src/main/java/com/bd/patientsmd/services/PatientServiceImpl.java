package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.requests.CreatePatientRequest;
import com.bd.patientsmd.models.mappers.PatientMapper;
import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;
    private final UsersRepository usersRepository;

    @Override
    public PatientDto createPatient(CreatePatientRequest patientRequest) {
        Users user = getUserIfPresent(patientRequest.userId());

        Patients patient = Patients.builder()
                .fullName(patientRequest.fullName())
                .birthDate(patientRequest.birthDate())
                .phone(patientRequest.phone())
                .heightCm(patientRequest.heightCm())
                .initialWeightKg(patientRequest.initialWeightKg())
                .objective(patientRequest.objective())
                .user(user)
                .build();
        Patients savedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(savedPatient);
    }

    @Override
    public PatientDto updatePatient(Long id, CreatePatientRequest patientRequest) {
        Patients patient=patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException(
                        "Patient introuvable"
                ));
        patient.setFullName(patientRequest.fullName());
        patient.setBirthDate(patientRequest.birthDate());
        patient.setPhone(patientRequest.phone());
        patient.setHeightCm(patientRequest.heightCm());
        patient.setInitialWeightKg(patientRequest.initialWeightKg());
        patient.setObjective(patientRequest.objective());
        patient.setUser(getUserIfPresent(patientRequest.userId()));
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
                .orElseThrow(()->new ResourceNotFoundException(
                        "Patient introuvable"
                ));
        return PatientMapper.toDto(patient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)){
            throw new ResourceNotFoundException("Patient introuvable");
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

    private Users getUserIfPresent(Long userId) {
        if (userId == null) {
            return null;
        }

        return usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
    }
}
