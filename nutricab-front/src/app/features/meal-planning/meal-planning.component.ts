import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MealPlan } from '../../core/models/meal-plan.model';
import { Patient } from '../../core/models/patient.model';
import { Page } from '../../core/models/user.model';
import { MealplanningService } from '../../core/services/mealplanning.service';
import { PatientsService } from '../../core/services/patients.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableLoadingComponent } from '../../shared/table-loading/table-loading.component';

@Component({
  selector: 'app-meal-planning',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule, PaginationComponent, TableLoadingComponent],
  templateUrl: './meal-planning.component.html',
  styleUrl: './meal-planning.component.css'
})
export class MealPlanningComponent implements OnInit {

  patients: Patient[] = [];
  searchTerm = '';
  loading = true;
  errorMessage = '';
  mainPlans: MealPlan[] = [];
  selectedPatientId = '';
  selectedActiveStatus = '';
  minCalories: number | null = null;
  maxCalories: number | null = null;
  filterModalOpen = false;
  page: Page<MealPlan> | null = null;
  pageSize = 10;

  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);
  private mealPlanningService = inject(MealplanningService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadPatients();
      this.loadMealPlans();
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

  loadMealPlans(pageNumber: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.mealPlanningService.getAllMealPlans(pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.mainPlans = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des plans alimentaires';
        this.loading = false;
      }
    });
  }

  editConsultation(mealPlanId: number): void {
    this.router.navigate(['/meal-plans/edit', mealPlanId]);
  }

  deleteConsultation(mealPlanId: number): void {
    this.alertService.confirm('Confirmation', 'Etes-vous sur de vouloir supprimer ce plan alimentaire ?', () => {
      this.mealPlanningService.deleteMealPlan(mealPlanId).subscribe({
        next: () => {
          this.alertService.success('Plan alimentaire supprime avec succes');
          this.loadCurrentPage();
        },
        error: () => {
          this.alertService.error('Erreur lors de la suppression du plan alimentaire');
        }
      });
    });
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

  onSearchInput(event: Event): void {
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadMealPlans();
    }
  }

  onSearch(): void {
    this.loading = true;
    this.errorMessage = '';
    const keyword = this.searchTerm.trim();

    if (!keyword) {
      this.loadMealPlans();
      return;
    }

    this.mealPlanningService.searchMealPlans(keyword, 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.mainPlans = page.content;
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

    this.mealPlanningService.getMealPlansByPatientId(Number(patientId), 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.mainPlans = page.content;
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
          this.page = null;
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
      this.mealPlanningService.getMealPlansByCaloriesRange(this.minCalories, this.maxCalories, 0, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.mainPlans = page.content;
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

  onPageChange(pageNumber: number): void {
    const keyword = this.searchTerm.trim();

    if (this.selectedPatientId && this.selectedActiveStatus !== 'active') {
      this.loading = true;
      this.errorMessage = '';
      this.mealPlanningService.getMealPlansByPatientId(Number(this.selectedPatientId), pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.mainPlans = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du filtrage par patient';
          this.loading = false;
        }
      });
      return;
    }

    if (this.minCalories !== null && this.maxCalories !== null) {
      this.loading = true;
      this.errorMessage = '';
      this.mealPlanningService.getMealPlansByCaloriesRange(this.minCalories, this.maxCalories, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.mainPlans = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du filtrage par calories';
          this.loading = false;
        }
      });
      return;
    }

    if (keyword) {
      this.loading = true;
      this.errorMessage = '';
      this.mealPlanningService.searchMealPlans(keyword, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.mainPlans = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la recherche';
          this.loading = false;
        }
      });
      return;
    }

    this.loadMealPlans(pageNumber);
  }

  private loadCurrentPage(): void {
    const currentPage = this.page?.number ?? 0;
    const pageAfterDelete = currentPage > 0 && this.mainPlans.length <= 1
      ? currentPage - 1
      : currentPage;

    this.onPageChange(pageAfterDelete);
  }
}
