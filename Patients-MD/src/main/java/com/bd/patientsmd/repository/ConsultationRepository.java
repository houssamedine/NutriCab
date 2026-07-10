package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.entites.Consultations;
import com.bd.patientsmd.models.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultations, Long> {
    List<Consultations> findByPatientId(Long patientId);
    List<Consultations> findByPatientIdOrderByConsultationDateAsc(Long patientId);
    List<Consultations> findTop10ByOrderByConsultationDateDesc();
}
