import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsultationsService } from '../../../core/services/consultations.service';
import { AlertService } from '../../../shared/Alertify/alert-service.service';
import { CreateConsultationRequest } from '../../../core/models/consultation.model';
import { Patient } from '../../../core/models/patient.model';
import { CommonModule } from '@angular/common';
import { PatientsService } from '../../../core/services/patients.service';

@Component({
  selector: 'app-consultation-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './consultation-form.component.html',
  styleUrl: './consultation-form.component.css'
})
export class ConsultationFormComponent {
  errorMessage = '';
  loading = false;
  submitted = false;
  consultationForm!: FormGroup;
  consultationId?: number;
  isEditMode = false;
  patients: Patient[] = [];

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute)
  private router = inject(Router);
  private consultationService = inject(ConsultationsService);
  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);

  ngOnInit(): void {
    this.consultationForm = this.fb.group({
      patientId: ['', Validators.required],
      consultationDate: ['', Validators.required],
      weightKg: ['', Validators.required],
      waistCm: ['', Validators.required],
      notes: [''],
      recommendations: ['']
    });

    /** Charge la liste des patients */
    this.loadPatients();

    /** Vérifie si nous sommes en mode édition ou création */
    const id = this.route.snapshot.paramMap.get('id');

    /** Vérifie si nous sommes en mode édition ou création */
    if (id) {
      this.consultationId = Number(id);
      this.isEditMode = true;
      this.loadConsultationById(this.consultationId);
    }
  }

  /** Charge la liste des patients */
  loadPatients(): void {
    this.patientsService.getAllPatients().subscribe({
      next: (data) => {
        this.patients = data;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des patients';
      }
    });
  }

  /** Charge les détails d'une consultation pour l'édition */
  loadConsultationById(id: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.consultationService.getConsultationById(id).subscribe({
      next: (data) => {
        this.consultationForm.patchValue({
          patientId: data.patient.id,
          consultationDate: data.consultationDate,
          weightKg: data.weightKg,
          waistCm: data.waistCm,
          notes: data.notes,
          recommendations: data.recommendations
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement de la consultation';
        this.loading = false;
      }
    });
  }

  /** Sauvegarde une consultation (création ou mise à jour) */
  saveConsultation(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.consultationForm.invalid) {
      this.consultationForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.consultationForm.value as CreateConsultationRequest;
    const request: CreateConsultationRequest = {
      ...formValue,
      consultationDate: formValue.consultationDate.length === 16
        ? `${formValue.consultationDate}:00`
        : formValue.consultationDate
    };

    const saveRequest = this.isEditMode && this.consultationId
      ? this.consultationService.updateConsultation(this.consultationId, request)
      : this.consultationService.createConsultation(request);

    saveRequest.subscribe({
      next: () => {
        this.loading = false;
        this.alertService.success(`Consultation ${this.isEditMode ? 'mise à jour' : 'créée'} avec succès`);
        this.router.navigate(['/consultations']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = this.isEditMode
          ? 'Erreur lors de la mise à jour de la consultation'
          : 'Erreur lors de la création de la consultation';
      }
    });
  }

  /** Vérifie si un champ du formulaire est invalide */
  fieldInvalid(fieldName: string): boolean {
    const field = this.consultationForm.get(fieldName);
    return !!(field && field.invalid && (field.touched || this.submitted));
  }

  cancel(): void {
    this.router.navigate(['/consultations']);
  }
}
