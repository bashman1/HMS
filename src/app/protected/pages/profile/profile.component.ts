import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-4xl mx-auto space-y-6">
      <!-- Profile Header -->
      <div class="card">
        <div class="relative h-32 bg-gradient-to-r from-primary-500 to-primary-600">
          <div class="absolute -bottom-16 left-6">
            <div class="w-32 h-32 rounded-full bg-white p-1 shadow-lg">
              <div class="w-full h-full rounded-full bg-primary-100 flex items-center justify-center">
                <span class="text-4xl font-bold text-primary-600">
                  {{ getUserInitials() }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div class="pt-20 pb-6 px-6">
          <div class="flex justify-between items-start">
            <div>
              <h1 class="text-2xl font-bold text-secondary-900">
                {{ getUserName() }}
              </h1>
              <p class="text-secondary-500">{{ authService.currentUser()?.email }}</p>
              <div class="mt-2 flex items-center gap-2">
                <span *ngFor="let role of authService.currentUser()?.roles" class="badge-primary">{{ role.name }}</span>
                <span *ngIf="authService.currentUser()?.isEmailVerified" class="badge-success">
                  <svg class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                  Verified
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Profile Information -->
      <div class="card">
        <div class="card-header">
          <h2 class="text-lg font-semibold text-secondary-900">Profile Information</h2>
        </div>
        <div class="card-body">
          <form class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label class="label">First Name</label>
                <input
                  type="text"
                  [value]="authService.currentUser()?.firstName"
                  class="input"
                  readonly
                />
              </div>
              <div>
                <label class="label">Last Name</label>
                <input
                  type="text"
                  [value]="authService.currentUser()?.lastName"
                  class="input"
                  readonly
                />
              </div>
              <div>
                <label class="label">Email Address</label>
                <input
                  type="email"
                  [value]="authService.currentUser()?.email"
                  class="input"
                  readonly
                />
              </div>
              <div>
                <label class="label">Phone Number</label>
                <input
                  type="tel"
                  [value]="authService.currentUser()?.phoneNumber || 'Not provided'"
                  class="input"
                  readonly
                />
              </div>
            </div>
          </form>
        </div>
      </div>

      <!-- Account Stats -->
      <div class="card">
        <div class="card-header">
          <h2 class="text-lg font-semibold text-secondary-900">Account Statistics</h2>
        </div>
        <div class="card-body">
          <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
            <div class="text-center">
              <p class="text-3xl font-bold text-primary-600">1,234</p>
              <p class="text-sm text-secondary-500">Total Visits</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-green-600">56</p>
              <p class="text-sm text-secondary-500">Appointments</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-blue-600">12</p>
              <p class="text-sm text-secondary-500">Prescriptions</p>
            </div>
            <div class="text-center">
              <p class="text-3xl font-bold text-purple-600">3</p>
              <p class="text-sm text-secondary-500">Active Plans</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class ProfileComponent {
  authService = inject(AuthService);

  getUserName(): string {
    const user = this.authService.currentUser();
    return user ? user.firstName + ' ' + user.lastName : 'User';
  }

  getUserInitials(): string {
    const user = this.authService.currentUser();
    if (!user) return 'U';
    return (user.firstName.charAt(0) + user.lastName.charAt(0)).toUpperCase();
  }
}
