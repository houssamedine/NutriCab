package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.entites.Users;
import com.bd.patientsmd.models.requests.CreatePatientRequest;
import com.bd.patientsmd.models.mappers.PatientMapper;
import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.repository.UsersRepository;
import com.bd.patientsmd.security.CurrentUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;
    private final UsersRepository usersRepository;
    private final CurrentUserService currentUserService;

    @Override
    public PatientDto createPatient(CreatePatientRequest patientRequest) {
        Users user = resolvePatientUser(patientRequest.userId(), null);

        Patients patient = Patients.builder()
                .fullName(patientRequest.fullName())
                .birthDate(patientRequest.birthDate())
                .phone(patientRequest.phone())
                .heightCm(patientRequest.heightCm())
                .initialWeightKg(patientRequest.initialWeightKg())
                .objective(patientRequest.objective())
                .user(user)
                .build();
        patient.stampCreated();
        Patients savedPatient = patientRepository.saveAndFlush(patient);
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
        patient.setUser(resolvePatientUser(patientRequest.userId(), patient.getUser()));
        patient.stampUpdated();
        return PatientMapper.toDto(patientRepository.saveAndFlush(patient));
    }

    @Override
    public Page<PatientDto> getAllPatients(Pageable pageable) {
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> patientRepository.findByUserId(userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(PatientMapper::toDto);
        }

        return patientRepository.findAll(pageable)
                .map(PatientMapper::toDto);
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
    public Page<PatientDto> searhPatient(String keyword, Pageable pageable) {
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> patientRepository.findByFullNameContainingIgnoreCaseAndUserId(keyword, userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(PatientMapper::toDto);
        }

        return patientRepository.findByFullNameContainingIgnoreCase(keyword, pageable)
                .map(PatientMapper::toDto);
    }

    private Users resolvePatientUser(Long userId, Users currentUser) {
        if (userId == null) {
            if (currentUserService.isNutritionist()) {
                return currentUserService.getCurrentUserId()
                        .flatMap(usersRepository::findById)
                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
            }

            return currentUser;
        }

        return usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
    }
}
