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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
@Transactional
public class AppointmentServiceImpl  implements AppointmentService{

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Override
    public AppointmentDto createAppointment(CreateAppointmentRequest appointmentRequest) {
        Patients patient = patientRepository.findById(appointmentRequest.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));

        Appointments appointment = AppointmentMapper.toEntity(appointmentRequest, patient);
        Appointments appointmentSaved=appointmentRepository.save(appointment);
        return AppointmentMapper.toDto(appointmentSaved);
    }

    @Override
    public AppointmentDto updateAppointment(Long id, CreateAppointmentRequest appointmentRequest) {
        Appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment introuvable"));
        Patients patient = patientRepository.findById(appointmentRequest.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
        AppointmentMapper.updateEntity(appointment, appointmentRequest, patient);
        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public List<AppointmentDto> getAllAppointment() {
        return appointmentRepository.findAll()
                .stream().map(AppointmentMapper::toDto).toList();
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        Appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment introuvable"));
        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public List<AppointmentDto> getPatientById(Long id) {
        return appointmentRepository.findByPatientId(id)
                .stream()
                .map(AppointmentMapper::toDto)
                .toList();
    }

    @Override
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)){
            throw new ResourceNotFoundException("Appointment introuvable");
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<AppointmentDto> findByStatus(String keyword) {
        AppointmentStatus status = AppointmentStatus.valueOf(keyword.toUpperCase());

        return appointmentRepository.findByStatus(status)
                .stream()
                .map(AppointmentMapper::toDto)
                .toList();
    }
}
