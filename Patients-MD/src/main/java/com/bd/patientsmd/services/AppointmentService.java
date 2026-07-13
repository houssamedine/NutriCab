package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.requests.CreateAppointmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

    AppointmentDto createAppointment(CreateAppointmentRequest appointmentRequest);
    AppointmentDto updateAppointment(Long id, CreateAppointmentRequest appointmentRequest);
    Page<AppointmentDto> getAllAppointment(Pageable pageable);
    AppointmentDto getAppointmentById(Long id);
    Page<AppointmentDto> getPatientById(Long id, Pageable pageable);
    void deleteAppointment(Long id);
    Page<AppointmentDto> findByStatus(String keyword, Pageable pageable);
}
