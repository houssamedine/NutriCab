package com.bd.patientsmd.repository;

import com.bd.patientsmd.models.entites.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan,Long> {
    List<MealPlan> findByPatientId(Long patientId);
    List<MealPlan> findByObjectiveContainingIgnoreCase(String objective);
    List<MealPlan> findByCaloriesBetween(Integer minCalories, Integer maxCalories);
    Optional<MealPlan> findFirstByPatientIdOrderByIdDesc(Long patientId);

}
