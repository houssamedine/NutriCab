package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.MealPlanDto;
import com.bd.patientsmd.models.requests.CreateMealPlanRequest;
import com.bd.patientsmd.services.MealPlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mealPlan")
public class MealPlanController {

    private final MealPlanService planService;

    public MealPlanController(MealPlanService planService) {
        this.planService = planService;
    }

    @PostMapping()
    public MealPlanDto createMealPlan(@RequestBody CreateMealPlanRequest request){
        return planService.createMealPlan(request);
    }

    @GetMapping()
    public List<MealPlanDto> getAllMealPlans(){
        return planService.getAllMealPlans();
    }

    @GetMapping("/{id}")
    public MealPlanDto getMealPlanById(@PathVariable Long id){
        return planService.getMealPlanById(id);
    }

    @PutMapping("/{id}")
    public MealPlanDto updateMealPlan(@PathVariable Long id, @RequestBody CreateMealPlanRequest request){
        return planService.updateMealPlan(id,request);
    }

    @DeleteMapping("/{id}")
    public void deleteMealPlan(@PathVariable Long id){
        planService.deleteMealPlan(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<MealPlanDto> getMealPlansByPatient(@PathVariable Long patientId){
        return planService.getMealPlansByPatient(patientId);
    }

    @GetMapping("/search")
    public List<MealPlanDto> getMealPlansByObjective(@RequestParam String objective){
        return planService.getMealPlansByObjective(objective);
    }

    @GetMapping("/calories")
    public List<MealPlanDto> getMealPlansByCalorieRange(@RequestParam Integer minCalories, @RequestParam Integer maxCalories){
        return planService.getMealPlansByCalorieRange(minCalories,maxCalories);
    }

    @GetMapping("/patient/{patientId}/active")
    public MealPlanDto getActiveMealPlanByPatient(@PathVariable Long patientId){
        return planService.getActiveMealPlanByPatient(patientId);
    }

}
