package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Consultations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultations, Long> {
    Page<Consultations> findByPatientId(Long patientId, Pageable pageable);
    Page<Consultations> findByPatientUserId(Long userId, Pageable pageable);
    List<Consultations> findByPatientIdOrderByConsultationDateAsc(Long patientId);
    List<Consultations> findTop10ByOrderByConsultationDateDesc();
    List<Consultations> findTop10ByPatientUserIdOrderByConsultationDateDesc(Long userId);
}
