import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '@core/services/auth.service';
import { ToastService } from '@core/services/toast.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-4xl mx-auto space-y-6">
      <h1 class="text-2xl font-bold text-secondary-900">Settings</h1>

      <!-- Notification Settings -->
      <div class="card">
        <div class="card-header">
          <h2 class="text-lg font-semibold text-secondary-900">Notifications</h2>
        </div>
        <div class="card-body space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Email Notifications</p>
              <p class="text-sm text-secondary-500">Receive email updates about your account</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" [(ngModel)]="emailNotifications" class="sr-only peer" />
              <div class="w-11 h-6 bg-secondary-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-secondary-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
            </label>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Push Notifications</p>
              <p class="text-sm text-secondary-500">Receive push notifications on your device</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" [(ngModel)]="pushNotifications" class="sr-only peer" />
              <div class="w-11 h-6 bg-secondary-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-secondary-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
            </label>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">SMS Notifications</p>
              <p class="text-sm text-secondary-500">Receive text messages for important updates</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" [(ngModel)]="smsNotifications" class="sr-only peer" />
              <div class="w-11 h-6 bg-secondary-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-secondary-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
            </label>
          </div>
        </div>
      </div>

      <!-- Security Settings -->
      <div class="card">
        <div class="card-header">
          <h2 class="text-lg font-semibold text-secondary-900">Security</h2>
        </div>
        <div class="card-body space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Two-Factor Authentication</p>
              <p class="text-sm text-secondary-500">Add an extra layer of security to your account</p>
            </div>
            <button class="btn-secondary btn-sm">Enable</button>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Active Sessions</p>
              <p class="text-sm text-secondary-500">Manage your active sessions across devices</p>
            </div>
            <button class="btn-secondary btn-sm">View All</button>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Password</p>
              <p class="text-sm text-secondary-500">Last changed 30 days ago</p>
            </div>
            <button (click)="changePassword()" class="btn-secondary btn-sm">Change</button>
          </div>
        </div>
      </div>

      <!-- Privacy Settings -->
      <div class="card">
        <div class="card-header">
          <h2 class="text-lg font-semibold text-secondary-900">Privacy</h2>
        </div>
        <div class="card-body space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Profile Visibility</p>
              <p class="text-sm text-secondary-500">Control who can see your profile information</p>
            </div>
            <select class="input w-40">
              <option>Public</option>
              <option>Private</option>
              <option>Contacts Only</option>
            </select>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Show Activity Status</p>
              <p class="text-sm text-secondary-500">Let others see when you're active</p>
            </div>
            <label class="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" [(ngModel)]="showActivityStatus" class="sr-only peer" />
              <div class="w-11 h-6 bg-secondary-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-secondary-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
            </label>
          </div>
        </div>
      </div>

      <!-- Danger Zone -->
      <div class="card border-danger-200">
        <div class="card-header bg-danger-50">
          <h2 class="text-lg font-semibold text-danger-800">Danger Zone</h2>
        </div>
        <div class="card-body space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Export Your Data</p>
              <p class="text-sm text-secondary-500">Download a copy of all your data</p>
            </div>
            <button class="btn-outline btn-sm border-danger-500 text-danger-500 hover:bg-danger-50">
              Export Data
            </button>
          </div>
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-secondary-900">Delete Account</p>
              <p class="text-sm text-secondary-500">Permanently delete your account and all data</p>
            </div>
            <button class="btn-danger btn-sm">
              Delete Account
            </button>
          </div>
        </div>
      </div>

      <!-- Save Changes -->
      <div class="flex justify-end">
        <button (click)="saveSettings()" class="btn-primary">
          Save Changes
        </button>
      </div>
    </div>
  `,
})
export class SettingsComponent {
  authService = inject(AuthService);
  private toastService = inject(ToastService);

  emailNotifications = signal(true);
  pushNotifications = signal(false);
  smsNotifications = signal(false);
  showActivityStatus = signal(true);

  changePassword(): void {
    this.toastService.show({
      type: 'info',
      title: 'Coming Soon',
      message: 'Password change functionality will be available soon.'
    });
  }

  saveSettings(): void {
    this.toastService.show({
      type: 'success',
      title: 'Settings Saved',
      message: 'Your preferences have been updated.'
    });
  }
}
