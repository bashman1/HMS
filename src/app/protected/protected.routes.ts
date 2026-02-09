import { Routes } from '@angular/router';

export const PROTECTED_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent),
  },
  {
    path: 'settings',
    loadComponent: () => import('./pages/settings/settings.component').then(m => m.SettingsComponent),
  },
  // Patient Management Routes
  {
    path: 'patients',
    loadComponent: () => import('./pages/patient-list/patient-list.component').then(m => m.PatientListComponent),
  },
  {
    path: 'patients/register',
    loadComponent: () => import('./pages/patient-registration/patient-registration.component').then(m => m.PatientRegistrationComponent),
  },
  {
    path: 'patients/:uuid',
    loadComponent: () => import('./pages/patient-details/patient-details.component').then(m => m.PatientDetailsComponent),
  },
  // OPD Routes
  {
    path: 'opd/queue',
    loadComponent: () => import('./pages/opd-queue/opd-queue.component').then(m => m.OpdQueueComponent),
  },
  {
    path: 'visits',
    loadComponent: () => import('./pages/visit-list/visit-list.component').then(m => m.VisitListComponent),
  },
  {
    path: 'visits/register',
    loadComponent: () => import('./pages/visit-registration/visit-registration.component').then(m => m.VisitRegistrationComponent),
  },
];
