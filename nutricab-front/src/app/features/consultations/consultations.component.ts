import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Consultation } from '../../core/models/consultation.model';
import { Patient } from '../../core/models/patient.model';
import { Page } from '../../core/models/user.model';
import { ConsultationsService } from '../../core/services/consultations.service';
import { PatientsService } from '../../core/services/patients.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableLoadingComponent } from '../../shared/table-loading/table-loading.component';

@Component({
  selector: 'app-consultations',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule, PaginationComponent, TableLoadingComponent],
  templateUrl: './consultations.component.html',
  styleUrl: './consultations.component.css'
})
export class ConsultationsComponent implements OnInit {

  loading = true;
  searchTerm = '';
  errorMessage = '';
  consultations: Consultation[] = [];
  patients: Patient[] = [];
  selectedPatientId = '';
  page: Page<Consultation> | null = null;
  pageSize = 10;

  private consultationsService = inject(ConsultationsService);
  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadConsultations();
      this.loadPatients();
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

  loadConsultations(pageNumber: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.consultationsService.getAllConsultations(pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.consultations = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des consultations';
        this.loading = false;
      }
    });
  }

  editConsultation(id: number): void {
    this.router.navigate(['/consultations/edit', id]);
  }

  deleteConsultation(id: number): void {
    this.alertService.confirm('Confirmation', 'Etes-vous sur de vouloir supprimer cette consultation ?', () => {
      this.consultationsService.deleteConsultation(id).subscribe({
        next: () => {
          this.alertService.success('Consultation supprimee avec succes');
          this.loadCurrentPage();
        },
        error: () => {
          this.alertService.error('Erreur lors de la suppression de la consultation');
        }
      });
    });
  }

  goToNewConsultation(): void {
    this.router.navigate(['/consultations/new']);
  }

  filtrerByPatient(patientId: string): void {
    this.errorMessage = '';

    if (!patientId) {
      this.loadConsultations();
      return;
    }

    this.loading = true;

    this.consultationsService.getConsultationsByPatient(Number(patientId), 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.consultations = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du filtrage par patient';
        this.loading = false;
      }
    });
  }

  onSearchInput(event: Event): void {
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadConsultations();
    }
  }

  onSearch(): void {
    this.loading = true;
    this.errorMessage = '';
    const keyword = this.searchTerm.trim();

    if (!keyword) {
      this.loadConsultations();
      return;
    }

    this.consultationsService.searchConsultations(keyword, 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.consultations = page.content;
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

    if (this.selectedPatientId) {
      this.loading = true;
      this.errorMessage = '';
      this.consultationsService.getConsultationsByPatient(Number(this.selectedPatientId), pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.consultations = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du filtrage par patient';
          this.loading = false;
        }
      });
      return;
    }

    if (keyword) {
      this.loading = true;
      this.errorMessage = '';
      this.consultationsService.searchConsultations(keyword, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.consultations = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la recherche';
          this.loading = false;
        }
      });
      return;
    }

    this.loadConsultations(pageNumber);
  }

  private loadCurrentPage(): void {
    const currentPage = this.page?.number ?? 0;
    const pageAfterDelete = currentPage > 0 && this.consultations.length <= 1
      ? currentPage - 1
      : currentPage;

    this.onPageChange(pageAfterDelete);
  }
}
