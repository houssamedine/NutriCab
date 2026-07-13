package com.bd.patientsmd.models.mappers;

import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.entites.MealPlan;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;
import com.bd.patientsmd.models.responses.MealPlanResponse;

public class MealPlanMapper {

    public static MealPlanDto toDto(MealPlan mealPlan) {
        if (mealPlan == null) return null;

        return new MealPlanDto(
                mealPlan.getId(),
                PatientMapper.toDto(mealPlan.getPatient()),
                mealPlan.getTitle(),
                mealPlan.getObjective(),
                mealPlan.getCalories(),
                mealPlan.getContent(),
                mealPlan.getCreatedAt(),
                mealPlan.getUpdatedAt(),
                mealPlan.getCreatedBy(),
                mealPlan.getUpdatedBy()
        );
    }

    public static MealPlanResponse toResponse(MealPlan mealPlan) {
        if (mealPlan == null) return null;

        return new MealPlanResponse(
                mealPlan.getId(),
                PatientMapper.toSummaryResponse(mealPlan.getPatient()),
                mealPlan.getTitle(),
                mealPlan.getObjective(),
                mealPlan.getCalories(),
                mealPlan.getContent()
        );
    }

    public static MealPlan toEntity(CreateMealPlanRequest request, Patients patient) {
        if (request == null) return null;

        return MealPlan.builder()
                .patient(patient)
                .title(request.title())
                .objective(request.objective())
                .calories(request.calories())
                .content(request.content())
                .build();
    }

    public static void updateEntity(MealPlan mealPlan, CreateMealPlanRequest request, Patients patient) {
        if (mealPlan == null || request == null) return;

        mealPlan.setPatient(patient);
        mealPlan.setTitle(request.title());
        mealPlan.setObjective(request.objective());
        mealPlan.setCalories(request.calories());
        mealPlan.setContent(request.content());
    }
}
