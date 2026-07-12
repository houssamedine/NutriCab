package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository  extends JpaRepository<Patients,Long> {
    List<Patients>findByFullNameContainingIgnoreCase(String keyword);
    List<Patients> findByUserId(Long userId);
}
