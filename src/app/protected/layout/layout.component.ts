import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen bg-secondary-50">
      <!-- Sidebar -->
      <aside
        class="fixed inset-y-0 left-0 z-40 w-64 bg-white border-r border-secondary-200 transform transition-transform duration-300 ease-out lg:translate-x-0"
        [class.-translate-x-full]="!sidebarOpen()"
        [class.translate-x-0]="sidebarOpen()"
      >
        <div class="flex flex-col h-full">
          <!-- Logo -->
          <div class="flex items-center h-16 px-6 border-b border-secondary-100">
            <a routerLink="/dashboard" class="flex items-center">
              <span class="text-2xl font-bold text-primary-600">HMS</span>
            </a>
          </div>

          <!-- Navigation -->
          <nav class="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
            <a
              routerLink="/dashboard"
              routerLinkActive="bg-primary-50 text-primary-600 border-primary-200"
              [routerLinkActiveOptions]="{ exact: true }"
              class="flex items-center px-4 py-3 text-secondary-600 rounded-lg hover:bg-secondary-50 hover:text-secondary-900 transition-colors group"
            >
              <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
              </svg>
              Dashboard
            </a>
            <a
              routerLink="/profile"
              routerLinkActive="bg-primary-50 text-primary-600 border-primary-200"
              class="flex items-center px-4 py-3 text-secondary-600 rounded-lg hover:bg-secondary-50 hover:text-secondary-900 transition-colors"
            >
              <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
              Profile
            </a>
            <a
              routerLink="/settings"
              routerLinkActive="bg-primary-50 text-primary-600 border-primary-200"
              class="flex items-center px-4 py-3 text-secondary-600 rounded-lg hover:bg-secondary-50 hover:text-secondary-900 transition-colors"
            >
              <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              </svg>
              Settings
            </a>
          </nav>

          <!-- User Section -->
          <div class="p-4 border-t border-secondary-100">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
                  <span class="text-primary-600 font-semibold">
                    {{ getUserInitials() }}
                  </span>
                </div>
              </div>
              <div class="ml-3 min-w-0 flex-1">
                <p class="text-sm font-medium text-secondary-900 truncate">
                  {{ getUserName() }}
                </p>
                <p class="text-xs text-secondary-500 truncate">
                  {{ authService.currentUser()?.email }}
                </p>
              </div>
            </div>
            <button
              (click)="logout()"
              class="mt-4 w-full flex items-center px-4 py-2 text-sm text-secondary-600 hover:text-danger-500 hover:bg-danger-50 rounded-lg transition-colors"
            >
              <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
              </svg>
              Sign out
            </button>
          </div>
        </div>
      </aside>

      <!-- Mobile Sidebar Overlay -->
      <div
        *ngIf="sidebarOpen()"
        (click)="sidebarOpen.set(false)"
        class="fixed inset-0 z-30 bg-secondary-900/50 lg:hidden"
      ></div>

      <!-- Main Content -->
      <div class="lg:pl-64">
        <!-- Top Header -->
        <header class="sticky top-0 z-20 bg-white border-b border-secondary-100">
          <div class="flex items-center justify-between h-16 px-4 sm:px-6 lg:px-8">
            <!-- Mobile menu button -->
            <button
              (click)="sidebarOpen.set(!sidebarOpen())"
              class="lg:hidden p-2 text-secondary-400 hover:text-secondary-600 hover:bg-secondary-100 rounded-lg"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
              </svg>
            </button>

            <!-- Page Title (Mobile) -->
            <div class="lg:hidden">
              <span class="text-lg font-semibold text-secondary-900">HMS</span>
            </div>

            <!-- Right side -->
            <div class="flex items-center space-x-4">
              <!-- Notifications -->
              <button class="p-2 text-secondary-400 hover:text-secondary-600 hover:bg-secondary-100 rounded-lg relative">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                </svg>
                <span class="absolute top-1 right-1 w-2 h-2 bg-danger-500 rounded-full"></span>
              </button>

              <!-- User Menu (Desktop) -->
              <div class="hidden lg:flex items-center">
                <div class="flex items-center">
                  <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
                    <span class="text-primary-600 font-semibold text-sm">
                      {{ getUserInitials() }}
                    </span>
                  </div>
                  <span class="ml-2 text-sm font-medium text-secondary-700">
                    {{ authService.currentUser()?.firstName }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </header>

        <!-- Page Content -->
        <main class="p-4 sm:p-6 lg:p-8">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
})
export class LayoutComponent {
  authService = inject(AuthService);
  
  sidebarOpen = signal(false);

  getUserName(): string {
    const user = this.authService.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : 'User';
  }

  getUserInitials(): string {
    const user = this.authService.currentUser();
    if (!user) return 'U';
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  logout(): void {
    this.authService.logout();
  }
}
