import { Routes } from '@angular/router';
import {PatientsComponent} from './features/patients/patients.component';
import {PatientFormComponent} from './features/patients/patient-form/patient-form.component';
import {LoginComponent} from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { AppointmentsComponent } from './features/appointments/appointments.component';
import { ConsultationsComponent } from './features/consultations/consultations.component';
import { MealPlanningComponent } from './features/meal-planning/meal-planning.component';
import { UsersComponent } from './features/users/users.component';

export const routes: Routes = [
  { path:'login', component: LoginComponent },
  { path:'dashboard', component: DashboardComponent },
  { path:'patients',component:PatientsComponent},
  { path:'patients/new', component:PatientFormComponent},
  { path:'patients/edit/:id', component:PatientFormComponent},
  { path:'appointments',component:AppointmentsComponent},
  { path:'appointments/new', component:AppointmentsComponent},
  { path:'consultations',component:ConsultationsComponent},
  { path:'consultations/new', component:ConsultationsComponent},
  { path:'meal-plans',component:MealPlanningComponent},
  { path:'meal-plans/new', component:MealPlanningComponent},
  { path:'users',component:UsersComponent},
  { path:'users/new', component:UsersComponent},
  { path: '', redirectTo: 'patients', pathMatch: 'full' }
];
