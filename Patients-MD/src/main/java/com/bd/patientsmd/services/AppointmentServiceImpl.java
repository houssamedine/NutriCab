package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.enums.AppointmentStatus;
import com.bd.patientsmd.models.mappers.AppointmentMapper;
import com.bd.patientsmd.models.requests.CreateAppointmentRequest;
import com.bd.patientsmd.repository.AppointmentRepository;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.security.CurrentUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class AppointmentServiceImpl  implements AppointmentService{

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserService currentUserService;

    @Override
    public AppointmentDto createAppointment(CreateAppointmentRequest appointmentRequest) {
        Patients patient = patientRepository.findById(appointmentRequest.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));

        Appointments appointment = AppointmentMapper.toEntity(appointmentRequest, patient);
        appointment.stampCreated();
        Appointments appointmentSaved=appointmentRepository.saveAndFlush(appointment);
        return AppointmentMapper.toDto(appointmentSaved);
    }

    @Override
    public AppointmentDto updateAppointment(Long id, CreateAppointmentRequest appointmentRequest) {
        Appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment introuvable"));
        Patients patient = patientRepository.findById(appointmentRequest.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        AppointmentMapper.updateEntity(appointment, appointmentRequest, patient);
        appointment.stampUpdated();
        return AppointmentMapper.toDto(appointmentRepository.saveAndFlush(appointment));
    }

    @Override
    public Page<AppointmentDto> getAllAppointment(Pageable pageable) {
        if (!currentUserService.isAdmin() && !currentUserService.isSecretary()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> appointmentRepository.findByPatientUserId(userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(AppointmentMapper::toDto);
        }

        return appointmentRepository.findAll(pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        Appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment introuvable"));
        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public Page<AppointmentDto> getPatientById(Long id, Pageable pageable) {
        return appointmentRepository.findByPatientId(id, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)){
            throw new ResourceNotFoundException("Appointment introuvable");
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    public Page<AppointmentDto> findByStatus(String keyword, Pageable pageable) {
        AppointmentStatus status = AppointmentStatus.valueOf(keyword.toUpperCase());

        if (!currentUserService.isAdmin() && !currentUserService.isSecretary()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> appointmentRepository.findByStatusAndPatientUserId(status, userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(AppointmentMapper::toDto);
        }

        return appointmentRepository.findByStatus(status, pageable)
                .map(AppointmentMapper::toDto);
    }
}
