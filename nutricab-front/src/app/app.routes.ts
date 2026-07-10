import { Routes } from '@angular/router';
import {PatientsComponent} from './features/patients/patients.component';
import {PatientFormComponent} from './features/patients/patient-form/patient-form.component';

export const routes: Routes = [
  // { path: 'login', component: LoginComponent },
  // { path: 'dashboard', component: DashboardComponent },
  {path:'patients',component:PatientsComponent},
  {path:'patients/new', component:PatientFormComponent},
  { path: '', redirectTo: 'patients', pathMatch: 'full' }
];
