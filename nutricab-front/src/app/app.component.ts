import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {PatientsComponent} from './features/patients/patients.component';
import {PatientFormComponent} from './features/patients/patient-form/patient-form.component';
import { NavbarComponent } from './shared/Menu/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,PatientsComponent,PatientFormComponent,NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'nutricab-front';
}
