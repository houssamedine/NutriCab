package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.MealPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan,Long> {
    Page<MealPlan> findByPatientId(Long patientId, Pageable pageable);
    Page<MealPlan> findByPatientUserId(Long userId, Pageable pageable);
    Page<MealPlan> findByObjectiveContainingIgnoreCase(String objective, Pageable pageable);
    Page<MealPlan> findByObjectiveContainingIgnoreCaseAndPatientUserId(String objective, Long userId, Pageable pageable);
    Page<MealPlan> findByCaloriesBetween(Integer minCalories, Integer maxCalories, Pageable pageable);
    Page<MealPlan> findByCaloriesBetweenAndPatientUserId(Integer minCalories, Integer maxCalories, Long userId, Pageable pageable);
    Optional<MealPlan> findFirstByPatientIdOrderByIdDesc(Long patientId);

}
