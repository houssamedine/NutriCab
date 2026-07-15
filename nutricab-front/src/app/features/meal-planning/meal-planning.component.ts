import { Component, inject } from '@angular/core';
import { Patient } from '../../core/models/patient.model';
import { PatientsService } from '../../core/services/patients.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { MealplanningService } from '../../core/services/mealplanning.service';
import { Consultation } from '../../core/models/consultation.model';
import { MealPlan } from '../../core/models/meal-plan.model';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-meal-planning',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './meal-planning.component.html',
  styleUrl: './meal-planning.component.css'
})
export class MealPlanningComponent {

  patients: Patient[] = [];
  searchTerm = '';
  loading = false;
  errorMessage = '';
  mainPlans: MealPlan[] = [];
  selectedPatientId = '';
  selectedActiveStatus = '';
  minCalories: number | null = null;
  maxCalories: number | null = null;
  filterModalOpen = false;

  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);
  private mealPlanningService = inject(MealplanningService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadPatients();
    this.loadMealPlans();
  }

  /** Charge la liste des rendez-vous */
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

  loadMealPlans(): void {
    this.loading = true;
    this.errorMessage = '';
    this.mealPlanningService.getAllMealPlans().subscribe({
      next: (data) => {
        this.mainPlans = data.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des plans de repas';
        this.loading = false;
      }
    });
  }

  editConsultation(consultationId: number): void {
    this.router.navigate(['/meal-plans/edit', consultationId]);
  }

  deleteConsultation(consultationId: number): void {
    this.alertService.confirm('Confirmation', 'Êtes-vous sûr de vouloir supprimer ce plan de repas ?', () => {
      this.mealPlanningService.deleteMealPlan(consultationId).subscribe({
        next: () => {
          this.alertService.success('Le plan de repas a été supprimé avec succès.');
          this.loadMealPlans();
        },
        error: () => {
          this.alertService.error('Une erreur est survenue lors de la suppression du plan de repas.');
        }
      });
    })
  }

  goToNewMealPlan(): void {
    this.router.navigate(['/meal-plans/new']);
  }

  openFilterModal(): void {
    this.filterModalOpen = true;
  }

  closeFilterModal(): void {
    this.filterModalOpen = false;
  }

  /**Recherche de rendez-vous par mot-clé */
  onSearchInput(event: Event): void { 
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadMealPlans();
    }
  }
  
  /** Recherche de rendez-vous par mot-clé */
  onSearch(): void { 
    this.loading = true;
    this.errorMessage = '';
    const keyword = this.searchTerm.trim();

    if (!keyword) {
      this.loadMealPlans();
      return;
    }
     this.mealPlanningService.searchMealPlans(keyword).subscribe({
      next: (data) => {
        this.mainPlans = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche';
        this.loading = false;
      }
    });
  }

  filtrerByPatient(patientId: string): void {
    this.errorMessage = '';

    if (!patientId) {
      this.loadMealPlans();
      return;
    }

    this.loading = true;

    this.mealPlanningService.getMealPlansByPatientId(Number(patientId)).subscribe({
      next: (data) => {
        this.mainPlans = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du filtrage par patient';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.errorMessage = '';

    if (this.selectedActiveStatus === 'active' && this.selectedPatientId) {
      this.loading = true;
      this.mealPlanningService.getActiveMealPlanByPatientId(Number(this.selectedPatientId)).subscribe({
        next: (data) => {
          this.mainPlans = [data];
          this.loading = false;
          this.closeFilterModal();
        },
        error: () => {
          this.errorMessage = 'Aucun plan actif trouve pour ce patient';
          this.mainPlans = [];
          this.loading = false;
        }
      });
      return;
    }

    if (this.selectedPatientId) {
      this.filtrerByPatient(this.selectedPatientId);
      this.closeFilterModal();
      return;
    }

    if (this.minCalories !== null && this.maxCalories !== null) {
      this.loading = true;
      this.mealPlanningService.getMealPlansByCaloriesRange(this.minCalories, this.maxCalories).subscribe({
        next: (data) => {
          this.mainPlans = data;
          this.loading = false;
          this.closeFilterModal();
        },
        error: () => {
          this.errorMessage = 'Erreur lors du filtrage par calories';
          this.loading = false;
        }
      });
      return;
    }

    this.loadMealPlans();
    this.closeFilterModal();
  }

  resetFilters(): void {
    this.selectedPatientId = '';
    this.selectedActiveStatus = '';
    this.minCalories = null;
    this.maxCalories = null;
    this.loadMealPlans();
    this.closeFilterModal();
  }
}
