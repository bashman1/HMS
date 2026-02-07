import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Welcome Section -->
      <div class="bg-white rounded-xl shadow-card p-6">
        <h1 class="text-2xl font-bold text-secondary-900">
          Welcome back, {{ authService.currentUser()?.firstName }}!
        </h1>
        <p class="mt-2 text-secondary-600">
          Here's what's happening with your account today.
        </p>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div *ngFor="let stat of stats; track stat.title" class="card p-6 hover:shadow-soft transition-shadow">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-secondary-500">{{ stat.title }}</p>
              <p class="mt-1 text-2xl font-bold text-secondary-900">{{ stat.value }}</p>
            </div>
            <div class="w-12 h-12 rounded-lg flex items-center justify-center" [ngClass]="stat.iconBg">
              <svg class="w-6 h-6" [ngClass]="stat.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" [attr.d]="stat.icon"/>
              </svg>
            </div>
          </div>
          <div class="mt-4 flex items-center text-sm">
            <span [ngClass]="stat.changeType === 'up' ? 'text-green-600' : 'text-red-600'">
              <svg *ngIf="stat.changeType === 'up'" class="w-4 h-4 inline mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd"/>
              </svg>
              <svg *ngIf="stat.changeType !== 'up'" class="w-4 h-4 inline mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M14.707 10.293a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 111.414-1.414L9 12.586V5a1 1 0 012 0v7.586l2.293-2.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
              </svg>
              {{ stat.change }}
            </span>
            <span class="ml-2 text-secondary-500">vs last month</span>
          </div>
        </div>
      </div>

      <!-- Recent Activity & Quick Actions -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Recent Activity -->
        <div class="lg:col-span-2 card">
          <div class="card-header">
            <h2 class="text-lg font-semibold text-secondary-900">Recent Activity</h2>
          </div>
          <div class="card-body">
            <div class="space-y-4">
              <div *ngFor="let activity of recentActivity; track activity.id" class="flex items-center space-x-4">
                <div class="w-10 h-10 rounded-full flex items-center justify-center" [ngClass]="activity.iconBg">
                  <svg class="w-5 h-5" [ngClass]="activity.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" [attr.d]="activity.icon"/>
                  </svg>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-secondary-900">{{ activity.title }}</p>
                  <p class="text-sm text-secondary-500">{{ activity.description }}</p>
                </div>
                <span class="text-xs text-secondary-400">{{ activity.time }}</span>
              </div>
            </div>
            <button class="mt-4 w-full py-2 text-sm text-primary-600 hover:text-primary-700 font-medium">
              View all activity
            </button>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="card">
          <div class="card-header">
            <h2 class="text-lg font-semibold text-secondary-900">Quick Actions</h2>
          </div>
          <div class="card-body space-y-3">
            <button *ngFor="let action of quickActions; track action.title"
              class="w-full flex items-center px-4 py-3 text-left rounded-lg border border-secondary-200 hover:border-primary-300 hover:bg-primary-50 transition-colors group"
            >
              <div class="w-10 h-10 rounded-lg flex items-center justify-center mr-4" [ngClass]="action.iconBg">
                <svg class="w-5 h-5" [ngClass]="action.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" [attr.d]="action.icon"/>
                </svg>
              </div>
              <div>
                <p class="text-sm font-medium text-secondary-900 group-hover:text-primary-600">{{ action.title }}</p>
                <p class="text-xs text-secondary-500">{{ action.description }}</p>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent {
  authService = inject(AuthService);

  stats = [
    {
      title: 'Total Patients',
      value: '2,543',
      change: '+12.5%',
      changeType: 'up' as const,
      icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z',
      iconBg: 'bg-primary-100',
      iconColor: 'text-primary-600',
    },
    {
      title: 'Appointments',
      value: '156',
      change: '+8.2%',
      changeType: 'up' as const,
      icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
    },
    {
      title: 'Pending Tasks',
      value: '23',
      change: '-3.1%',
      changeType: 'down' as const,
      icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4',
      iconBg: 'bg-yellow-100',
      iconColor: 'text-yellow-600',
    },
    {
      title: 'Revenue',
      value: '$45,231',
      change: '+15.3%',
      changeType: 'up' as const,
      icon: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
    },
  ];

  recentActivity = [
    {
      id: 1,
      title: 'New patient registered',
      description: 'John Doe has registered as a new patient',
      time: '2 hours ago',
      icon: 'M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z',
      iconBg: 'bg-primary-100',
      iconColor: 'text-primary-600',
    },
    {
      id: 2,
      title: 'Appointment completed',
      description: 'Dr. Smith completed consultation with Jane Wilson',
      time: '4 hours ago',
      icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
    },
    {
      id: 3,
      title: 'Lab results ready',
      description: 'Blood test results for Robert Brown are ready',
      time: '6 hours ago',
      icon: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
    },
  ];

  quickActions = [
    {
      title: 'Register Patient',
      description: 'Add new patient to system',
      icon: 'M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z',
      iconBg: 'bg-primary-100',
      iconColor: 'text-primary-600',
    },
    {
      title: 'Schedule Appointment',
      description: 'Book a new appointment',
      icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
    },
    {
      title: 'View Reports',
      description: 'Access analytics reports',
      icon: 'M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
      iconBg: 'bg-purple-100',
      iconColor: 'text-purple-600',
    },
  ];
}
