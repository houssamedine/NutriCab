package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.requests.CreateAppointmentRequest;

import java.util.List;

public interface AppointmentService {

    AppointmentDto createAppointment(CreateAppointmentRequest appointmentRequest);
    AppointmentDto updateAppointment(Long id, CreateAppointmentRequest appointmentRequest);
    List<AppointmentDto> getAllAppointment();
    AppointmentDto getAppointmentById(Long id);
    List<AppointmentDto> getPatientById(Long id);
    void deleteAppointment(Long id);
    List<AppointmentDto> findByStatus(String keyword);
}
