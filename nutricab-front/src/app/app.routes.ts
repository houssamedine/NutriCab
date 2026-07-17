import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    canActivateChild: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'patients',
        loadComponent: () => import('./features/patients/patients.component').then(m => m.PatientsComponent)
      },
      {
        path: 'patients/new',
        loadComponent: () => import('./features/patients/patient-form/patient-form.component').then(m => m.PatientFormComponent)
      },
      {
        path: 'patients/edit/:id',
        loadComponent: () => import('./features/patients/patient-form/patient-form.component').then(m => m.PatientFormComponent)
      },
      {
        path: 'appointments',
        loadComponent: () => import('./features/appointments/appointments.component').then(m => m.AppointmentsComponent)
      },
      {
        path: 'appointments/new',
        loadComponent: () => import('./features/appointments/appointment-form/appointment-form.component').then(m => m.AppointmentFormComponent)
      },
      {
        path: 'appointments/edit/:id',
        loadComponent: () => import('./features/appointments/appointment-form/appointment-form.component').then(m => m.AppointmentFormComponent)
      },
      {
        path: 'consultations',
        loadComponent: () => import('./features/consultations/consultations.component').then(m => m.ConsultationsComponent)
      },
      {
        path: 'consultations/new',
        loadComponent: () => import('./features/consultations/consultation-form/consultation-form.component').then(m => m.ConsultationFormComponent)
      },
      {
        path: 'consultations/edit/:id',
        loadComponent: () => import('./features/consultations/consultation-form/consultation-form.component').then(m => m.ConsultationFormComponent)
      },
      {
        path: 'meal-plans',
        loadComponent: () => import('./features/meal-planning/meal-planning.component').then(m => m.MealPlanningComponent)
      },
      {
        path: 'meal-plans/new',
        loadComponent: () => import('./features/meal-planning/meal-plan-form/meal-plan-form.component').then(m => m.MealPlanFormComponent)
      },
      {
        path: 'meal-plans/edit/:id',
        loadComponent: () => import('./features/meal-planning/meal-plan-form/meal-plan-form.component').then(m => m.MealPlanFormComponent)
      },
      {
        path: 'users',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/users/users.component').then(m => m.UsersComponent)
      },
      {
        path: 'users/new',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/users/users-form/users-form.component').then(m => m.UsersFormComponent)
      },
      {
        path: 'users/edit/:id',
        canActivate: [adminGuard],
        loadComponent: () => import('./features/users/users-form/users-form.component').then(m => m.UsersFormComponent)
      },
      { path: '', redirectTo: 'patients', pathMatch: 'full' },
    ]
  },
  { path: '**', redirectTo: 'patients' },
];
