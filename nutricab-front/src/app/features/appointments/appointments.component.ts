import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Appointment, AppointmentStatus } from '../../core/models/appointment.model';
import { AppointmentsService } from '../../core/services/appointments.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Patient } from '../../core/models/patient.model';
import { PatientsService } from '../../core/services/patients.service';
import { Page } from '../../core/models/user.model';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableLoadingComponent } from '../../shared/table-loading/table-loading.component';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule, PaginationComponent, TableLoadingComponent],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.css'
})
export class AppointmentsComponent implements OnInit {
  appointments: Appointment[] = [];
  loading = true;
  searchTerm = '';
  errorMessage = '';
  patients: Patient[] = [];
  selectedPatientId = '';
  page: Page<Appointment> | null = null;
  pageSize = 10;

  private appointmentsService = inject(AppointmentsService);
  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadAppointments();
      this.loadPatients();
    }
  }

  /** Charge la liste des rendez-vous */
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

  /**Liste Globale des rendez-vous */
  loadAppointments(pageNumber: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.appointmentsService.getAllAppointments(pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.appointments = page.content;
        this.loading = false;

      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des rendez-vous';
        this.loading = false;
      }
    });
  }

  /**Navigation vers le formulaire de création d'un rendez-vous */
  goToNewAppointment(): void {
    this.router.navigate(['/appointments/new']);
  }

  /**Navigation vers le formulaire d'édition d'un rendez-vous */
  editAppointment(id: number): void {
    this.router.navigate(['/appointments/edit', id]);
  }

  /**Supprimer un rendez-vous */
  deleteAppointment(id: number): void {
    this.alertService.confirm(
      'Confirmer la suppression',
      'Etes-vous sur de vouloir supprimer ce rendez-vous ? Cette action est irreversible.',
      () => {
        this.appointmentsService.deleteAppointment(id).subscribe({
          next: () => {
            this.appointments = this.appointments.filter(item => item.id !== id);
            this.alertService.success('Rendez-vous supprime avec succes');
            this.loadCurrentPage();
          },
          error: () => {
            this.errorMessage = 'Erreur lors de la suppression';
          }
        });
      }
    );
  }

  /**Recherche de rendez-vous par mot-clé */
  onSearchInput(event: Event): void {
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadAppointments();
    }
  }

  /** Recherche de rendez-vous par mot-clé */
  onSearch(): void {
    this.loading = true;
    this.errorMessage = '';
    const keyword = this.searchTerm.trim();

    if (!keyword) {
      this.loadAppointments();
      return;
    }
    this.appointmentsService.searchAppointments(keyword, 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.appointments = page.content;
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
      this.loadAppointments();
      return;
    }

    this.loading = true;

    this.appointmentsService.getAppointmentsByPatientId(Number(patientId), 0, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.appointments = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du filtrage par patient';
        this.loading = false;
      }
    });
  }

  onPageChange(pageNumber: number): void {
    const keyword = this.searchTerm.trim();

    if (this.selectedPatientId) {
      this.loading = true;
      this.errorMessage = '';
      this.appointmentsService.getAppointmentsByPatientId(Number(this.selectedPatientId), pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.appointments = page.content;
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
      this.appointmentsService.searchAppointments(keyword, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.appointments = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la recherche';
          this.loading = false;
        }
      });
      return;
    }

    this.loadAppointments(pageNumber);
  }

  private loadCurrentPage(): void {
    const currentPage = this.page?.number ?? 0;
    const pageAfterDelete = currentPage > 0 && this.appointments.length === 0
      ? currentPage - 1
      : currentPage;

    this.onPageChange(pageAfterDelete);
  }

  getStatusLabel(status: AppointmentStatus): string {
    const labels: Record<AppointmentStatus, string> = {
      PLANNED: 'Planifie',
      DONE: 'Termine',
      CANCELLED: 'Annule'
    };

    return labels[status];
  }

  getStatusClass(status: AppointmentStatus): string {
    const classes: Record<AppointmentStatus, string> = {
      PLANNED: 'status-planned',
      DONE: 'status-done',
      CANCELLED: 'status-cancelled'
    };

    return classes[status];
  }

}
