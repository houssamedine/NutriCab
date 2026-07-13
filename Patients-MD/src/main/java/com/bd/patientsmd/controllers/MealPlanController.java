package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;
import com.bd.patientsmd.services.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meal-plan")
public class MealPlanController {

    private final MealPlanService planService;

    public MealPlanController(MealPlanService planService) {
        this.planService = planService;
    }

    @PostMapping()
    @PreAuthorize("@authorizationService.canCreateMealPlan(#request.patientId())")
    public MealPlanDto createMealPlan(@P("request") @Valid @RequestBody CreateMealPlanRequest request){
        return planService.createMealPlan(request);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public Page<MealPlanDto> getAllMealPlans(Pageable pageable){
        return planService.getAllMealPlans(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessMealPlan(#id)")
    public MealPlanDto getMealPlanById(@P("id") @PathVariable Long id){
        return planService.getMealPlanById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.canManageMealPlan(#id) and @authorizationService.canCreateMealPlan(#request.patientId())")
    public MealPlanDto updateMealPlan(
            @P("id") @PathVariable Long id,
            @P("request") @Valid @RequestBody CreateMealPlanRequest request
    ){
        return planService.updateMealPlan(id,request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authorizationService.canManageMealPlan(#id)")
    public void deleteMealPlan(@P("id") @PathVariable Long id){
        planService.deleteMealPlan(id);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("@authorizationService.canAccessPatient(#patientId)")
    public Page<MealPlanDto> getMealPlansByPatient(@P("patientId") @PathVariable Long patientId, Pageable pageable){
        return planService.getMealPlansByPatient(patientId, pageable);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public Page<MealPlanDto> getMealPlansByObjective(@RequestParam String objective, Pageable pageable){
        return planService.getMealPlansByObjective(objective, pageable);
    }

    @GetMapping("/calories")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public Page<MealPlanDto> getMealPlansByCalorieRange(
            @RequestParam Integer minCalories,
            @RequestParam Integer maxCalories,
            Pageable pageable
    ){
        return planService.getMealPlansByCalorieRange(minCalories, maxCalories, pageable);
    }

    @GetMapping("/patient/{patientId}/active")
    @PreAuthorize("@authorizationService.canAccessPatient(#patientId)")
    public MealPlanDto getActiveMealPlanByPatient(@P("patientId") @PathVariable Long patientId){
        return planService.getActiveMealPlanByPatient(patientId);
    }

}
