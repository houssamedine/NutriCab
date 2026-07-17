package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.requests.CreateAppointmentRequest;
import com.bd.patientsmd.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentsController {

    private final AppointmentService appointmentService;

    public AppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authorizationService.canCreateAppointment(#appointmentRequest.patientId())")
    public AppointmentDto createAppointment(@P("appointmentRequest") @Valid @RequestBody CreateAppointmentRequest appointmentRequest){
        return appointmentService.createAppointment(appointmentRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authorizationService.canManageAppointment(#id) and @authorizationService.canCreateAppointment(#appointmentRequest.patientId())")
    public AppointmentDto updateAppointment(
            @P("id") @PathVariable Long id,
            @P("appointmentRequest") @Valid @RequestBody CreateAppointmentRequest appointmentRequest
    ){
        return appointmentService.updateAppointment(id,appointmentRequest);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'SECRETARY', 'PATIENT')")
    public Page<AppointmentDto> getAllAppointment(Pageable pageable){
        return appointmentService.getAllAppointment(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationService.canAccessAppointment(#id)")
    public AppointmentDto getAppointmentById(@P("id") @PathVariable Long id){
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/patient/{id}")
    @PreAuthorize("hasRole('SECRETARY') or @authorizationService.canAccessPatient(#id)")
    public Page<AppointmentDto> getPatientById(@P("id") @PathVariable Long id, Pageable pageable){
        return appointmentService.getPatientById(id, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.canManageAppointment(#id)")
    public void deleteAppointment(@P("id") @PathVariable Long id){
        appointmentService.deleteAppointment(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'NUTRITIONIST', 'SECRETARY', 'PATIENT')")
    public Page<AppointmentDto> searchAppointments(@RequestParam String keyword, Pageable pageable){
        return appointmentService.searchAppointments(keyword, pageable);
    }
}
