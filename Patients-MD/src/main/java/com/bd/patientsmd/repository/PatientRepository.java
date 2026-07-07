package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.Patients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository  extends JpaRepository<Patients,Long> {
    List<Patients>findByFullNameContainingIgnoreCase(String keyword);
}
