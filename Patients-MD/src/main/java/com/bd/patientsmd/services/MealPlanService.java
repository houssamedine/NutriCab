package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MealPlanService {
    MealPlanDto createMealPlan(CreateMealPlanRequest request);
    Page<MealPlanDto> getAllMealPlans(Pageable pageable);
    MealPlanDto getMealPlanById(Long id);
    MealPlanDto updateMealPlan(Long id, CreateMealPlanRequest request);
    void deleteMealPlan(Long id);
    Page<MealPlanDto> getMealPlansByPatient(Long patientId, Pageable pageable);
    Page<MealPlanDto> getMealPlansByObjective(String objective, Pageable pageable);
    Page<MealPlanDto> searchMealPlans(String keyword, Pageable pageable);
    Page<MealPlanDto> getMealPlansByCalorieRange(Integer minCalories, Integer maxCalories, Pageable pageable);
    MealPlanDto getActiveMealPlanByPatient(Long patientId);
}
