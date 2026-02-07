import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { publicGuard } from './core/guards/public.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'public',
    canActivate: [publicGuard],
    loadChildren: () => import('./public/public.routes').then(m => m.PUBLIC_ROUTES),
  },
  {
    path: 'auth',
    canActivate: [publicGuard],
    loadChildren: () => import('./public/auth/auth.routes').then(m => m.AUTH_ROUTES),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./protected/layout/layout.component').then(m => m.LayoutComponent),
    loadChildren: () => import('./protected/protected.routes').then(m => m.PROTECTED_ROUTES),
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
