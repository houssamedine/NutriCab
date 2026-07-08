import { Component } from '@angular/core';
import {PatientsService} from '../../core/services/patients.service';
import {Patients} from '../../core/interfaces/interface';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patients.component.html',
  styleUrl: './patients.component.css'
})
export class PatientsComponent {

  patients:Patients[]=[];
  loading = false;
  errorMessage = '';

  constructor(private patientService: PatientsService,private router:Router) {
    this.loadPatients();
  }

  loadPatients():void{
    this.loading=true;
    this.errorMessage='';

    this.patientService.getAllPatients().subscribe({
      next:(data)=>{
        this.patients=data;
        this.loading=false;
      },
      error:(err)=>{
        this.errorMessage='Erreur lors du chargement des patients';
        this.loading=false;
    }
    })
  }

  deletePatient(id:number):void{
    const confirmed = confirm('Voulez-vous vraiment supprimer ce patient ?');
    if (!confirmed){
      return;
    }

    this.patientService.deletePatient(id).subscribe({
      next:()=>{
        this.patients=this.patients.filter(item=>item.id !== id);
      },
      error:()=>{
        this.errorMessage = 'Erreur lors de la suppression';
      }
    })
  }

  goToNewPatient(): void {
    this.router.navigate(['/patients/new']);
  }


}
