import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CreatePatientRequest } from '../../../core/models/patient.model';
import { PatientsService } from '../../../core/services/patients.service';
import { AlertService } from '../../../shared/Alertify/alert-service.service';

@Component({
  selector: 'app-patient-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './patient-form.component.html',
  styleUrl: './patient-form.component.css'
})
export class PatientFormComponent {
  errorMessage = '';
  loading = false;
  submitted = false;
  patientForm: FormGroup;
  patientId?: number;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientsService,
    private alertService: AlertService
  ) {
    this.patientForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      birthDate: ['', Validators.required],
      phone: ['', [Validators.required, Validators.minLength(8)]],
      heightCm: [null, [Validators.required, Validators.min(1)]],
      initialWeightKg: [null, [Validators.required, Validators.min(1)]],
      objective: ['']
    });

    /** Récupérer l'ID du patient depuis les paramètres de l'URL */
    const id = this.route.snapshot.paramMap.get('id');

    /** Vérifier si nous sommes en mode édition ou création */
    if (id) {
      this.patientId = Number(id);
      this.isEditMode = true;
      this.loadPatientById(this.patientId);
    }
  }

  /**Enregistrer un patient (création ou modification) */
  savePatient(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.patientForm.invalid) {
      this.patientForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const request = this.patientForm.value as CreatePatientRequest;

    const saveRequest = this.isEditMode && this.patientId
      ? this.patientService.updatePatient(this.patientId, request)
      : this.patientService.createPatient(request);

    saveRequest.subscribe({
      next: () => {
        this.loading = false;
        this.alertService.success(
          this.isEditMode ? 'Patient modifie avec succes' : 'Patient ajoute avec succes'
        );
        this.router.navigate(['/patients']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = this.isEditMode
          ? 'Erreur lors de la modification du patient'
          : 'Erreur lors de la creation du patient';
        this.alertService.error(this.errorMessage);
      }
    });
  }

  /**Charger les informations d'un patient par son ID */
  loadPatientById(id: number): void {
    this.loading = true;

    this.patientService.getById(id).subscribe({
      next: (patient) => {
        this.patientForm.patchValue({
          fullName: patient.fullName,
          birthDate: patient.birthDate,
          phone: patient.phone,
          heightCm: patient.heightCm,
          initialWeightKg: patient.initialWeightKg,
          objective: patient.objective
        });

        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement du patient';
        this.alertService.error(this.errorMessage);
      }
    });
  }

  /**Vérifier si un champ du formulaire est invalide */
  fieldInvalid(fieldName: string): boolean {
    const field = this.patientForm.get(fieldName);
    return !!field && field.invalid && (field.touched || this.submitted);
  }
}
