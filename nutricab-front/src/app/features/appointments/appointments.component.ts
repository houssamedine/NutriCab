import { Component, inject } from '@angular/core';
import { Appointment } from '../../core/models/appointment.model';
import { AppointmentsService } from '../../core/services/appointments.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Patient } from '../../core/models/patient.model';
import { PatientsService } from '../../core/services/patients.service';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.css'
})
export class AppointmentsComponent {
  appointments: Appointment[] = [];
  loading = false;
  searchTerm = '';
  errorMessage = '';
  patients: Patient[] = [];
  selectedPatientId = '';

  private appointmentsService = inject(AppointmentsService);
  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadAppointments();
    this.loadPatients();
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

  /**Liste Globale des rendez-vous */
  loadAppointments(): void {
    this.loading = true;
    this.errorMessage = '';
    this.appointmentsService.getAllAppointments().subscribe({
      next: (data) => {
        this.appointments = data;
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
    this.appointmentsService.searchAppointments(keyword).subscribe({
      next: (data) => {
        this.appointments = data;
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

    this.appointmentsService.getAppointmentsByPatientId(Number(patientId)).subscribe({
      next: (data) => {
        this.appointments = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du filtrage par patient';
        this.loading = false;
      }
    });
  }

}
