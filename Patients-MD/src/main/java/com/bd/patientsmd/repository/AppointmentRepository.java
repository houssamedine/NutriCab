package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, Long> {
    List<Appointments> findByPatientId(Long patientId);
    List<Appointments> findByStatus(AppointmentStatus status);
}
