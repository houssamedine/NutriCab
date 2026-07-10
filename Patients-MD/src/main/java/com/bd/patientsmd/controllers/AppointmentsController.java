package com.bd.patientsmd.controllers;

import com.bd.patientsmd.models.dtos.AppointmentDto;
import com.bd.patientsmd.models.requests.CreateAppointmentRequest;
import com.bd.patientsmd.services.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentsController {

    private final AppointmentService appointmentService;

    public AppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentDto createAppointment(@RequestBody CreateAppointmentRequest appointmentRequest){
        return appointmentService.createAppointment(appointmentRequest);
    }

    @PutMapping("/{id}")
    public AppointmentDto updateAppointment(@PathVariable Long id, @RequestBody CreateAppointmentRequest appointmentRequest){
        return appointmentService.updateAppointment(id,appointmentRequest);
    }

    @GetMapping
    public List<AppointmentDto> getAllAppointment(){
        return appointmentService.getAllAppointment();
    }

    @GetMapping("/{id}")
    public AppointmentDto getAppointmentById(@PathVariable Long id){
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/patient/{id}")
    public List<AppointmentDto> getPatientById(@PathVariable Long id){
        return appointmentService.getPatientById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id){
        appointmentService.deleteAppointment(id);
    }

    @GetMapping("/search")
    public List<AppointmentDto> findByStatus(@RequestParam String keyword){
        return appointmentService.findByStatus(keyword);
    }
}
