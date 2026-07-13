package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments, Long> {
    Page<Appointments> findByPatientId(Long patientId, Pageable pageable);
    Page<Appointments> findByPatientUserId(Long userId, Pageable pageable);
    Page<Appointments> findByStatus(AppointmentStatus status, Pageable pageable);
    Page<Appointments> findByStatusAndPatientUserId(AppointmentStatus status, Long userId, Pageable pageable);
}
