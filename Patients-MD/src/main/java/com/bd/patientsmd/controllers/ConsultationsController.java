package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;
import com.bd.patientsmd.services.ConsultationsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationsController {

    private final ConsultationsService consultationsService;

    public ConsultationsController(ConsultationsService consultationsService) {
        this.consultationsService = consultationsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authorizationService.canCreateConsultation(#request.patientId())")
    public ConsultationsDto createConsultation(@P("request") @Valid @RequestBody CreateConsultationRequest request){
        return consultationsService.createConsultation(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public Page<ConsultationsDto> getAllConsultations(Pageable pageable){
        return consultationsService.getAllConsultations(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessConsultation(#id)")
    public ConsultationsDto getConsultationById(@P("id") @PathVariable Long id){
        return consultationsService.getConsultationById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.canManageConsultation(#id) and @authorizationService.canCreateConsultation(#request.patientId())")
    public ConsultationsDto updateConsultation(
            @P("id") @PathVariable Long id,
            @P("request") @Valid @RequestBody CreateConsultationRequest request
    ){
        return consultationsService.updateConsultation(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.canManageConsultation(#id)")
    public void deleteConsultation(@P("id") @PathVariable Long id){
        consultationsService.deleteConsultation(id);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("@authorizationService.canAccessPatient(#patientId)")
    public Page<ConsultationsDto> getConsultationsByPatient(@P("patientId") @PathVariable Long patientId, Pageable pageable){
        return consultationsService.getConsultationsByPatient(patientId, pageable);
    }

    @GetMapping("/patient/{patientId}/weight-history")
    @PreAuthorize("@authorizationService.canAccessPatient(#patientId)")
    public List<ConsultationsDto> getWeightHistoryByPatient(@P("patientId") @PathVariable Long patientId){
        return consultationsService.getWeightHistoryByPatient(patientId);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'PATIENT')")
    public List<ConsultationsDto> getRecentConsultations(){
        return consultationsService.getRecentConsultations();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'SECRETARY', 'PATIENT')")
    public Page<ConsultationsDto> searchConsultations(@RequestParam String keyword, Pageable pageable){
        return consultationsService.searchConsultations(keyword, pageable);
    }


}
