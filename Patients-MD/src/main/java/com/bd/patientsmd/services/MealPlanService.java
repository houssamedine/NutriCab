package com.bd.patientsmd.services;

import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;

import java.util.List;

public interface MealPlanService {
    MealPlanDto createMealPlan(CreateMealPlanRequest request);
    List<MealPlanDto> getAllMealPlans();
    MealPlanDto getMealPlanById(Long id);
    MealPlanDto updateMealPlan(Long id, CreateMealPlanRequest request);
    void deleteMealPlan(Long id);
    List<MealPlanDto> getMealPlansByPatient(Long patientId);
    List<MealPlanDto> getMealPlansByObjective(String objective);
    List<MealPlanDto> getMealPlansByCalorieRange(Integer minCalories, Integer maxCalories);
    MealPlanDto getActiveMealPlanByPatient(Long patientId);
}
