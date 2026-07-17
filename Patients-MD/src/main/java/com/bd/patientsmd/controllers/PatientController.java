package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.PatientDto;
import com.bd.patientsmd.models.requests.CreatePatientRequest;
import com.bd.patientsmd.services.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public Page<PatientDto> getAllPatients(Pageable pageable){
        return patientService.getAllPatients(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.canCreatePatient(#patientRequest.userId())")
    public PatientDto createPatient(@P("patientRequest") @Valid @RequestBody CreatePatientRequest patientRequest){
        return patientService.createPatient(patientRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessPatient(#id)")
    public PatientDto getPatientById(@P("id") @PathVariable Long id){
        return patientService.getPatientById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.canManagePatient(#id) and (hasRole('ADMIN') or @authorizationService.canCreatePatient(#patientRequest.userId()))")
    public PatientDto updatePatient(
            @P("id") @PathVariable Long id,
            @P("patientRequest") @Valid @RequestBody CreatePatientRequest patientRequest
    ){
        return patientService.updatePatient(id, patientRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePatient(@P("id") @PathVariable Long id){
        patientService.deletePatient(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public Page<PatientDto> searchPatients(@RequestParam String keyword, Pageable pageable){
        return patientService.searhPatient(keyword, pageable);
    }


}
