import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {PatientsComponent} from './features/patients/patients.component';
import {PatientFormComponent} from './features/patients/patient-form/patient-form.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,PatientsComponent,PatientFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'nutricab-front';
}
