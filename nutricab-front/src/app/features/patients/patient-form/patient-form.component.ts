import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {PatientsService} from '../../../core/services/patients.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-patient-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './patient-form.component.html',
  styleUrl: './patient-form.component.css'
})
export class PatientFormComponent {
  errorMessage = '';
  patientForm: FormGroup;

  constructor(
    private fb: FormBuilder,private router:Router,
    private patientService:PatientsService)
  {
    this.patientForm=this.fb.group({
      fullName: ['',Validators.required],
      birthDate: ['',Validators.required],
      phone: ['',Validators.required],
      heightCm: [null,[Validators.required,Validators.min(50)]],
      initialWeightKg: ['',[Validators.required,Validators.min(50)]],
      objective: [''],
    });
  }

  savePatient():void{
    if(this.patientForm.invalid){
      this.patientForm.markAllAsTouched();
      return;
    }
    this.patientService.createPatient(this.patientForm.value).subscribe(
      {
        next:()=>{
          alert('Patient ajouté avec succès');
          this.patientForm.reset();
        },
        error:()=>{
          this.errorMessage='Erreur lors de la création'
        }
      }
    )
  }

  goToListPatient(): void {
    this.router.navigate(['/patients']);
  }

}
