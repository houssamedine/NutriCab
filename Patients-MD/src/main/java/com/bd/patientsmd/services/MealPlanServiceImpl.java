package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.entites.MealPlan;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.mappers.MealPlanMapper;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;
import com.bd.patientsmd.repository.MealPlanRepository;
import com.bd.patientsmd.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class MealPlanServiceImpl implements MealPlanService{
    private final MealPlanRepository mealPlanRepository;
    private final PatientRepository patientRepository;

    @Override
    public MealPlanDto createMealPlan(CreateMealPlanRequest request) {
        Patients patient=patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));

        MealPlan mealPlan= MealPlanMapper.toEntity(request, patient);
        MealPlan mealPlanSaved=mealPlanRepository.save(mealPlan);
        return MealPlanMapper.toDto(mealPlanSaved);
    }

    @Override
    public List<MealPlanDto> getAllMealPlans() {
        return mealPlanRepository.findAll()
                .stream()
                .map(MealPlanMapper::toDto)
                .toList();
    }

    @Override
    public MealPlanDto getMealPlanById(Long id) {
        MealPlan mealPlan=mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal Plan introuvable"));
        return MealPlanMapper.toDto(mealPlan);
    }

    @Override
    public MealPlanDto updateMealPlan(Long id, CreateMealPlanRequest request) {
        MealPlan mealPlan=mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal Plan introuvable"));
        Patients patient=patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));
        MealPlanMapper.updateEntity(mealPlan,request,patient);
        return MealPlanMapper.toDto(mealPlan);
    }

    @Override
    public void deleteMealPlan(Long id) {
        if (!mealPlanRepository.existsById(id)){
            throw new ResourceNotFoundException("Meal Plan introuvable");
        }
        mealPlanRepository.deleteById(id);
    }

    @Override
    public List<MealPlanDto> getMealPlansByPatient(Long patientId) {
        return mealPlanRepository.findByPatientId(patientId)
                .stream()
                .map(MealPlanMapper::toDto)
                .toList();
    }

    @Override
    public List<MealPlanDto> getMealPlansByObjective(String objective) {
        return mealPlanRepository.findByObjectiveContainingIgnoreCase(objective)
                .stream()
                .map(MealPlanMapper::toDto)
                .toList();
    }

    @Override
    public List<MealPlanDto> getMealPlansByCalorieRange(Integer minCalories, Integer maxCalories) {
        return mealPlanRepository.findByCaloriesBetween(minCalories, maxCalories)
                .stream()
                .map(MealPlanMapper::toDto)
                .toList();
    }

    @Override
    public MealPlanDto getActiveMealPlanByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient introuvable");
        }

        MealPlan mealPlan = mealPlanRepository.findFirstByPatientIdOrderByIdDesc(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal Plan introuvable"));
        return MealPlanMapper.toDto(mealPlan);
    }
}
