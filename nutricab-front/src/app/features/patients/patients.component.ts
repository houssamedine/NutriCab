import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Patient } from '../../core/models/patient.model';
import { PatientsService } from '../../core/services/patients.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { Page } from '../../core/models/user.model';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableLoadingComponent } from '../../shared/table-loading/table-loading.component';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationComponent, TableLoadingComponent],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.css'
})
export class PatientsComponent implements OnInit {

  patients: Patient[] = [];
  searchTerm = '';
  loading = true;
  errorMessage = '';
  page: Page<Patient> | null = null;
  pageSize = 10;
  private readonly platformId = inject(PLATFORM_ID);

  constructor(
    private patientService: PatientsService,
    private alertService: AlertService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadPatients();
    }
  }

  /**Liste Globale des patients */
  loadPatients(pageNumber: number = 0): void {
    this.loading = true;
    this.errorMessage = '';

    this.patientService.getAllPatients(pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.patients = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des patients';
        this.loading = false;
      }
    });
  }

  /**Supprimer un patient */
  deletePatient(id: number): void {
    this.alertService.confirm(
      'Confirmer la suppression',
      'Etes-vous sur de vouloir supprimer ce patient ? Cette action est irreversible.',
      () => {
        this.patientService.deletePatient(id).subscribe({
          next: () => {
            this.patients = this.patients.filter(item => item.id !== id);
            this.alertService.success('Patient supprime avec succes');
            this.loadCurrentPage();
          },
          error: () => {
            this.errorMessage = 'Erreur lors de la suppression';
            this.alertService.error(this.errorMessage);
          }
        });
      },
      () => this.alertService.warning('Suppression annulee')
    );
  }

  /**Rediriger vers le formulaire de modification d'un patient */
  editPatient(id: number): void {
    this.router.navigate(['/patients/edit', id]);
  }

  /**Rediriger vers le formulaire d'ajout d'un nouveau patient */
  goToNewPatient(): void {
    this.router.navigate(['/patients/new']);
  }

  /**Rechercher un patient par mot clé */
  onSearchInput(event: Event): void {
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadPatients();
    }
  }

  /**Rechercher un patient par mot clé */
  onSearch(): void {
    this.loading = true;
    this.errorMessage = '';

    const keyword = this.searchTerm.trim();

    if (!keyword) {
      this.loadPatients();
      return;
    }

    this.patientService.searchPatients(keyword, 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.patients = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche';
        this.loading = false;
      }
    });
  }

  onPageChange(pageNumber: number): void {
    const keyword = this.searchTerm.trim();

    if (keyword) {
      this.loading = true;
      this.errorMessage = '';
      this.patientService.searchPatients(keyword, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.patients = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la recherche';
          this.loading = false;
        }
      });
      return;
    }

    this.loadPatients(pageNumber);
  }

  private loadCurrentPage(): void {
    const currentPage = this.page?.number ?? 0;
    const pageAfterDelete = currentPage > 0 && this.patients.length === 0
      ? currentPage - 1
      : currentPage;

    this.onPageChange(pageAfterDelete);
  }
}
