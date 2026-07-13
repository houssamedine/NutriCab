package com.bd.patientsmd.services;

import com.bd.patientsmd.exceptions.ResourceNotFoundException;
import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.entites.MealPlan;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.models.mappers.MealPlanMapper;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;
import com.bd.patientsmd.repository.MealPlanRepository;
import com.bd.patientsmd.repository.PatientRepository;
import com.bd.patientsmd.security.CurrentUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class MealPlanServiceImpl implements MealPlanService{
    private final MealPlanRepository mealPlanRepository;
    private final PatientRepository patientRepository;
    private final CurrentUserService currentUserService;

    @Override
    public MealPlanDto createMealPlan(CreateMealPlanRequest request) {
        Patients patient = getPatientOrThrow(request.patientId());

        MealPlan mealPlan= MealPlanMapper.toEntity(request, patient);
        mealPlan.stampCreated();
        MealPlan mealPlanSaved=mealPlanRepository.saveAndFlush(mealPlan);
        return MealPlanMapper.toDto(mealPlanSaved);
    }

    @Override
    public Page<MealPlanDto> getAllMealPlans(Pageable pageable) {
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> mealPlanRepository.findByPatientUserId(userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(MealPlanMapper::toDto);
        }

        return mealPlanRepository.findAll(pageable)
                .map(MealPlanMapper::toDto);
    }

    @Override
    public MealPlanDto getMealPlanById(Long id) {
        MealPlan mealPlan=mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal Plan introuvable"));
        return MealPlanMapper.toDto(mealPlan);
    }

    @Override
    public MealPlanDto updateMealPlan(Long id, CreateMealPlanRequest request) {
        MealPlan mealPlan=mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meal Plan introuvable"));
        Patients patient = getPatientOrThrow(request.patientId());
        MealPlanMapper.updateEntity(mealPlan,request,patient);
        mealPlan.stampUpdated();
        return MealPlanMapper.toDto(mealPlanRepository.saveAndFlush(mealPlan));
    }

    @Override
    public void deleteMealPlan(Long id) {
        if (!mealPlanRepository.existsById(id)){
            throw new ResourceNotFoundException("Meal Plan introuvable");
        }
        mealPlanRepository.deleteById(id);
    }

    @Override
    public Page<MealPlanDto> getMealPlansByPatient(Long patientId, Pageable pageable) {
        return mealPlanRepository.findByPatientId(patientId, pageable)
                .map(MealPlanMapper::toDto);
    }

    @Override
    public Page<MealPlanDto> getMealPlansByObjective(String objective, Pageable pageable) {
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> mealPlanRepository.findByObjectiveContainingIgnoreCaseAndPatientUserId(objective, userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(MealPlanMapper::toDto);
        }

        return mealPlanRepository.findByObjectiveContainingIgnoreCase(objective, pageable)
                .map(MealPlanMapper::toDto);
    }

    @Override
    public Page<MealPlanDto> getMealPlansByCalorieRange(Integer minCalories, Integer maxCalories, Pageable pageable) {
        if (!currentUserService.isAdmin()) {
            return currentUserService.getCurrentUserId()
                    .map(userId -> mealPlanRepository.findByCaloriesBetweenAndPatientUserId(minCalories, maxCalories, userId, pageable))
                    .orElseGet(() -> Page.empty(pageable))
                    .map(MealPlanMapper::toDto);
        }

        return mealPlanRepository.findByCaloriesBetween(minCalories, maxCalories, pageable)
                .map(MealPlanMapper::toDto);
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

    private Patients getPatientOrThrow(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("L'ID du patient est obligatoire");
        }

        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable"));
    }
}
