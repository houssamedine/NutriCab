package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.mappers.CreatePatientRequest;
import com.bd.patientsmd.services.PatientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<PatientDto> getAllPatients(){
        return patientService.getAllPatients();
    }

    @PostMapping
    public PatientDto createPatient(@Valid @RequestBody CreatePatientRequest patientRequest){
        return patientService.createPatient(patientRequest);
    }

    @GetMapping("/{id}")
    public PatientDto getPatientById(@PathVariable Long id){
        return patientService.getPatientById(id);
    }

    @PutMapping("/{id}")
    public PatientDto updatePatient(@PathVariable Long id,@Valid @RequestBody CreatePatientRequest patientRequest){
        return patientService.updatePatient(id, patientRequest);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id){
        patientService.deletePatient(id);
    }

    @GetMapping("/search")
    public List<PatientDto> searchPatients(@RequestParam String keyword){
        return patientService.searhPatient(keyword);
    }


}
