import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Patient } from '../../../core/models/patient.model';
import { ActivatedRoute, Router } from '@angular/router';
import { MealplanningService } from '../../../core/services/mealplanning.service';
import { PatientsService } from '../../../core/services/patients.service';
import { AlertService } from '../../../shared/Alertify/alert-service.service';
import { CreateMealPlanRequest } from '../../../core/models/meal-plan.model';

@Component({
  selector: 'app-meal-plan-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './meal-plan-form.component.html',
  styleUrl: './meal-plan-form.component.css'
})
export class MealPlanFormComponent {
  errorMessage = '';
  loading = false;
  submitted = false;
  mealPlanForm!: FormGroup;
  mealPlanId?: number;
  isEditMode = false;
  patients: Patient[] = [];

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute)
  private router = inject(Router);
  private mealPlanService = inject(MealplanningService);
  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);

  ngOnInit(): void {
    this.mealPlanForm = this.fb.group({
      patientId: [null, Validators.required],
      title: ['', Validators.required],
      objective: ['', Validators.required],
      calories: [null, Validators.required],
      content: ['', Validators.required],
    });

    const mealPlanId=this.route.snapshot.paramMap.get('id');

    if (mealPlanId) {
      this.isEditMode = true;
      this.mealPlanId = +mealPlanId;
      this.loadMealPlan(this.mealPlanId);
    }

    /** Charge la liste des patients */
    this.loadPatients();
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

  saveMealPlan(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.mealPlanForm.invalid) {
      this.mealPlanForm.markAllAsTouched();
      return;
    }

    this.loading = true;

    const formData = this.mealPlanForm.value as CreateMealPlanRequest;
    const request: CreateMealPlanRequest = {
          ...formData,
     }
    
    const requestSaved = this.isEditMode && this.mealPlanId
      ? this.mealPlanService.updateMealPlan(this.mealPlanId, request)
      : this.mealPlanService.createMealPlan(request);
     requestSaved.subscribe({
      next: () => {
        this.alertService.success(`Plan de repas ${this.isEditMode ? 'mis à jour' : 'créé'} avec succès`);
        this.router.navigate(['/meal-plans']);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = this.isEditMode
          ? 'Erreur lors de la mise à jour du plan de repas'
          : 'Erreur lors de la création du plan de repas';
      }
    });
  }

  loadMealPlan(mealPlanId: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.mealPlanService.getMealPlanById(mealPlanId).subscribe({
      next: (mealPlan) => {
        this.mealPlanForm.patchValue({  
          patientId: mealPlan.patient.id,
          title: mealPlan.title,
          objective: mealPlan.objective,
          calories: mealPlan.calories,
          content: mealPlan.content
        });
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement du plan de repas';
      }
    });
  }

  /** Vérifie si un champ du formulaire est invalide */
  fieldInvalid(fieldName: string): boolean {
    const field = this.mealPlanForm.get(fieldName);
    return !!(field && field.invalid && (field.touched || this.submitted));
  }

  cancel(): void {
    this.router.navigate(['/meal-plans']);
  }

}
