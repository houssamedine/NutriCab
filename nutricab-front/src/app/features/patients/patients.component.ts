import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Patient } from '../../core/models/patient.model';
import { PatientsService } from '../../core/services/patients.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.css'
})
export class PatientsComponent {

  patients: Patient[] = [];
  searchTerm = '';
  loading = false;
  errorMessage = '';

  constructor(
    private patientService: PatientsService,
    private alertService: AlertService,
    private router: Router
  ) {
    this.loadPatients();
  }

  /**Liste Globale des patients */
  loadPatients(): void {
    this.loading = true;
    this.errorMessage = '';

    this.patientService.getAllPatients().subscribe({
      next: (data) => {
        this.patients = data;
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

    this.patientService.searchPatients(keyword).subscribe({
      next: (data) => {
        this.patients = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche';
        this.loading = false;
      }
    });
  }
}
