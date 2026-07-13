package com.bd.patientsmd.models.mappers;

import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.enums.AppointmentStatus;
import com.bd.patientsmd.models.requests.CreateAppointmentRequest;
import com.bd.patientsmd.models.requests.UpdateAppointmentStatusRequest;
import com.bd.patientsmd.models.responses.AppointmentResponse;

public class AppointmentMapper {

    public static AppointmentDto toDto(Appointments appointment) {
        if (appointment == null) return null;

        return new AppointmentDto(
                appointment.getId(),
                PatientMapper.toDto(appointment.getPatient()),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt(),
                appointment.getCreatedBy(),
                appointment.getUpdatedBy()
        );
    }

    public static AppointmentResponse toResponse(Appointments appointment) {
        if (appointment == null) return null;

        return new AppointmentResponse(
                appointment.getId(),
                PatientMapper.toSummaryResponse(appointment.getPatient()),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }

    public static Appointments toEntity(CreateAppointmentRequest request, Patients patient) {
        if (request == null) return null;

        return Appointments.builder()
                .patient(patient)
                .appointmentDate(request.appointmentDate())
                .status(AppointmentStatus.PLANNED)
                .notes(request.notes())
                .build();
    }

    public static void updateEntity(Appointments appointment, CreateAppointmentRequest request, Patients patient) {
        if (appointment == null || request == null) return;

        appointment.setPatient(patient);
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setNotes(request.notes());
    }

    public static void updateStatus(Appointments appointment, UpdateAppointmentStatusRequest request) {
        if (appointment == null || request == null) return;

        appointment.setStatus(request.status());
    }
}
