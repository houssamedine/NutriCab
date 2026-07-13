package com.bd.patientsmd.security;

import com.bd.patientsmd.models.entites.Appointments;
import com.bd.patientsmd.models.entites.Consultations;
import com.bd.patientsmd.models.entites.MealPlan;
import com.bd.patientsmd.models.entites.Patients;
import com.bd.patientsmd.repository.AppointmentRepository;
import com.bd.patientsmd.repository.ConsultationRepository;
import com.bd.patientsmd.repository.MealPlanRepository;
import com.bd.patientsmd.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    private final CurrentUserService currentUserService;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final MealPlanRepository mealPlanRepository;

    public AuthorizationService(
            CurrentUserService currentUserService,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            ConsultationRepository consultationRepository,
            MealPlanRepository mealPlanRepository
    ) {
        this.currentUserService = currentUserService;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    public boolean canAccessPatient(Long patientId) {
        if (patientId == null) {
            return false;
        }

        if (currentUserService.isAdmin()) {
            return true;
        }

        return patientRepository.findById(patientId)
                .map(this::isCurrentUserPatientOwner)
                .orElse(false);
    }

    public boolean canManagePatient(Long patientId) {
        return currentUserService.isAdmin()
                || (currentUserService.isNutritionist() && canAccessPatient(patientId));
    }

    public boolean canCreatePatient(Long assignedUserId) {
        if (assignedUserId == null) {
            return currentUserService.isAdmin();
        }

        if (currentUserService.isAdmin()) {
            return true;
        }

        return currentUserService.isNutritionist()
                && currentUserService.getCurrentUserId()
                .map(userId -> userId.equals(assignedUserId))
                .orElse(false);
    }

    public boolean canAccessAppointment(Long appointmentId) {
        if (appointmentId == null) {
            return false;
        }

        if (currentUserService.isAdmin() || currentUserService.isSecretary()) {
            return true;
        }

        return appointmentRepository.findById(appointmentId)
                .map(Appointments::getPatient)
                .map(this::isCurrentUserPatientOwner)
                .orElse(false);
    }

    public boolean canManageAppointment(Long appointmentId) {
        return canAccessAppointment(appointmentId)
                && !currentUserService.isPatient();
    }

    public boolean canCreateAppointment(Long patientId) {
        if (patientId == null) {
            return false;
        }

        return currentUserService.isAdmin()
                || currentUserService.isSecretary()
                || (currentUserService.isNutritionist() && canAccessPatient(patientId));
    }

    public boolean canAccessConsultation(Long consultationId) {
        if (consultationId == null) {
            return false;
        }

        if (currentUserService.isAdmin()) {
            return true;
        }

        return consultationRepository.findById(consultationId)
                .map(Consultations::getPatient)
                .map(this::isCurrentUserPatientOwner)
                .orElse(false);
    }

    public boolean canManageConsultation(Long consultationId) {
        return currentUserService.isAdmin()
                || (currentUserService.isNutritionist() && canAccessConsultation(consultationId));
    }

    public boolean canCreateConsultation(Long patientId) {
        if (patientId == null) {
            return false;
        }

        return currentUserService.isAdmin()
                || (currentUserService.isNutritionist() && canAccessPatient(patientId));
    }

    public boolean canAccessMealPlan(Long mealPlanId) {
        if (mealPlanId == null) {
            return false;
        }

        if (currentUserService.isAdmin()) {
            return true;
        }

        return mealPlanRepository.findById(mealPlanId)
                .map(MealPlan::getPatient)
                .map(this::isCurrentUserPatientOwner)
                .orElse(false);
    }

    public boolean canManageMealPlan(Long mealPlanId) {
        return currentUserService.isAdmin()
                || (currentUserService.isNutritionist() && canAccessMealPlan(mealPlanId));
    }

    public boolean canCreateMealPlan(Long patientId) {
        if (patientId == null) {
            return false;
        }

        return currentUserService.isAdmin()
                || (currentUserService.isNutritionist() && canAccessPatient(patientId));
    }

    private boolean isCurrentUserPatientOwner(Patients patient) {
        if (patient.getUser() == null) {
            return false;
        }

        return currentUserService.getCurrentUserId()
                .map(userId -> userId.equals(patient.getUser().getId()))
                .orElse(false);
    }
}
