package com.bd.patientsmd.models.responses;

import com.bd.patientsmd.models.entites.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointments, Long> {
}
