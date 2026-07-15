import { Component, inject } from '@angular/core';
import { Consultation } from '../../core/models/consultation.model';
import { ConsultationsService } from '../../core/services/consultations.service';
import { Router } from '@angular/router';
import { PatientsService } from '../../core/services/patients.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Patient } from '../../core/models/patient.model';

@Component({
  selector: 'app-consultations',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './consultations.component.html',
  styleUrl: './consultations.component.css'
})
export class ConsultationsComponent {

  loading = false;
  searchTerm = '';
  errorMessage = '';
  consultations: Consultation[] = []
  patients: Patient[] = [];

  selectedPatientId = '';

  private consultationsService = inject(ConsultationsService);
  private patientsService = inject(PatientsService);
  private alertService = inject(AlertService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadConsultations();
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

  /** Charge la liste des consultations */
  loadConsultations(): void {
    this.loading = true;
    this.errorMessage = '';
    this.consultationsService.getAllConsultations().subscribe({
      next: (data) => {
        this.consultations = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des consultations';
        this.loading = false;
      }
    });
  }

  /** Redirige vers la page de modification d'une consultation */
  editConsultation(id: number): void {
    this.router.navigate(['/consultations/edit', id]);
  }

  /** Supprime une consultation après confirmation */
  deleteConsultation(id: number): void {
    this.alertService.confirm('Confirmation', 'Êtes-vous sûr de vouloir supprimer cette consultation ?', () => {
      this.consultationsService.deleteConsultation(id).subscribe({
        next: () => {
          this.alertService.success('Consultation supprimée avec succès');
          this.loadConsultations();
        },
        error: () => {
          this.alertService.error('Erreur lors de la suppression de la consultation');
        }
      });
    });
  }

  /** Redirige vers la page de création d'une nouvelle consultation */
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

  this.consultationsService.getConsultationsByPatient(Number(patientId)).subscribe({
    next: (data) => {
      this.consultations = data;
      this.loading = false;
    },
    error: () => {
      this.errorMessage = 'Erreur lors du filtrage par patient';
      this.loading = false;
    }
  });
}

/**Recherche de rendez-vous par mot-clé */
  onSearchInput(event: Event): void { 
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadConsultations();
    }
  }
  
  /** Recherche de rendez-vous par mot-clé */
  onSearch(): void { 
    this.loading = true;
    this.errorMessage = '';
    const keyword = this.searchTerm.trim();

    if (!keyword) {
      this.loadConsultations();
      return;
    }
     this.consultationsService.searchConsultations(keyword).subscribe({
      next: (data) => {
        this.consultations = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche';
        this.loading = false;
      }
    });
  }
}
