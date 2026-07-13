package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Patients;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository  extends JpaRepository<Patients,Long> {
    Page<Patients> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Patients> findByFullNameContainingIgnoreCaseAndUserId(String keyword, Long userId, Pageable pageable);
    Page<Patients> findByUserId(Long userId, Pageable pageable);
    List<Patients> findByUserId(Long userId);
}
