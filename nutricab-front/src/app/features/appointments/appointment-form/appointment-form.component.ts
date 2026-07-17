import { Component, inject } from '@angular/core';
import { AppointmentsService } from '../../../core/services/appointments.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertService } from '../../../shared/Alertify/alert-service.service';
import { CreateAppointmentRequest } from '../../../core/models/appointment.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Patient } from '../../../core/models/patient.model';
import { PatientsService } from '../../../core/services/patients.service';

@Component({
  selector: 'app-appointment-form',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule,FormsModule],
  templateUrl: './appointment-form.component.html',
  styleUrl: './appointment-form.component.css'
})
export class AppointmentFormComponent {
  errorMessage = '';
  loading = false;
  submitted = false;
  appointmentForm!: FormGroup;
  appointmentId?: number;
  isEditMode = false;
  patients: Patient[] = [];

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private appointmentService = inject(AppointmentsService);
  private alertService = inject(AlertService);
  private patientsService = inject(PatientsService);

  ngOnInit(): void {
    this.appointmentForm = this.fb.group({
      patientId: ['', Validators.required],
      appointmentDate: ['', Validators.required],
      status: ['PLANNED', Validators.required],
      notes: ['']
    });

    this.loadPatients();

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.appointmentId = Number(id);
      this.isEditMode = true;
      this.loadAppointmentById(this.appointmentId);
    }
  }

  loadPatients(): void {
    this.patientsService.getAllPatients(0, 100).subscribe({
      next: (page) => {
        this.patients = page.content;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des patients';
      }
    });
  }

  loadAppointmentById(id: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.appointmentService.getAppointmentById(id).subscribe({
      next: (data) => {
        this.appointmentForm.patchValue({
          patientId: data.patient?.id,
          appointmentDate: data.appointmentDate?.slice(0, 16),
          status: data.status,
          notes: data.notes
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement du rendez-vous';
        this.loading = false;
      }
    });
  }

  /** Enregistre un rendez-vous (création ou modification) */
  saveAppointment(): void {
    this.submitted = true;
    this.errorMessage = '';
    
    if (this.appointmentForm.invalid) {
      this.appointmentForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.appointmentForm.value as CreateAppointmentRequest;
    const request: CreateAppointmentRequest = {
      ...formValue,
      appointmentDate: formValue.appointmentDate.length === 16
        ? `${formValue.appointmentDate}:00`
        : formValue.appointmentDate
    };

    const saveRequest = this.isEditMode && this.appointmentId
      ? this.appointmentService.updateAppointment(this.appointmentId, request)
      : this.appointmentService.createAppointment(request);
      
    saveRequest.subscribe({
      next: () => {
        this.loading = false;
        this.alertService.success(
          this.isEditMode ? 'Rendez-vous modifie avec succes' : 'Rendez-vous ajoute avec succes'
        );
        this.router.navigate(['/appointments']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = this.isEditMode
          ? 'Erreur lors de la modification du rendez-vous'
          : 'Erreur lors de l\'ajout du rendez-vous';
      }
    });
  }
  
  /** Vérifie si un champ du formulaire est invalide */
  fieldInvalid(fieldName: string): boolean {
    const field = this.appointmentForm.get(fieldName);
    return !!(field && field.invalid && (field.touched || this.submitted));
  } 

  cancel(): void {
    this.router.navigate(['/appointments']);
  }
}
