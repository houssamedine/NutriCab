package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.ConsultationsDto;
import com.bd.patientsmd.models.requests.CreateConsultationRequest;
import com.bd.patientsmd.services.ConsultationsService;
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
    public ConsultationsDto createConsultation(@RequestBody CreateConsultationRequest request){
        return consultationsService.createConsultation(request);
    }

    @GetMapping
    public List<ConsultationsDto> getAllConsultations(){
        return consultationsService.getAllConsultations();
    }

    @GetMapping("/{id}")
    public ConsultationsDto getConsultationById(@PathVariable Long id){
        return consultationsService.getConsultationById(id);
    }

    @PutMapping("/{id}")
    public ConsultationsDto updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest request){
        return consultationsService.updateConsultation(id,request);
    }

    @DeleteMapping("/{id}")
    public void deleteConsultation(@PathVariable Long id){
        consultationsService.deleteConsultation(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<ConsultationsDto> getConsultationsByPatient(@PathVariable Long patientId){
        return consultationsService.getConsultationsByPatient(patientId);
    }

    @GetMapping("/patient/{patientId}/weight-history")
    public List<ConsultationsDto> getWeightHistoryByPatient(@PathVariable Long patientId){
        return consultationsService.getWeightHistoryByPatient(patientId);
    }

    @GetMapping("/recent")
    public List<ConsultationsDto> getRecentConsultations(){
        return consultationsService.getRecentConsultations();
    }


}
