import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '@core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed z-50 flex flex-col gap-2 p-4 pointer-events-none top-4 right-4">
      <div *ngFor="let toast of toastService.toasts; track: toast.id"
        class="pointer-events-auto flex items-start gap-3 p-4 rounded-lg shadow-lg animate-slide-up max-w-sm"
        [ngClass]="{
          'bg-green-50 border border-green-200': toast.type === 'success',
          'bg-red-50 border border-red-200': toast.type === 'error',
          'bg-yellow-50 border border-yellow-200': toast.type === 'warning',
          'bg-blue-50 border border-blue-200': toast.type === 'info'
        }"
      >
        <div class="flex-shrink-0" [ngSwitch]="toast.type">
          <svg *ngSwitchCase="'success'" class="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
          </svg>
          <svg *ngSwitchCase="'error'" class="w-5 h-5 text-red-500" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
          </svg>
          <svg *ngSwitchCase="'warning'" class="w-5 h-5 text-yellow-500" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
          </svg>
          <svg *ngSwitchCase="'info'" class="w-5 h-5 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
          </svg>
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium" [ngClass]="{
            'text-green-800': toast.type === 'success',
            'text-red-800': toast.type === 'error',
            'text-yellow-800': toast.type === 'warning',
            'text-blue-800': toast.type === 'info'
          }">{{ toast.title }}</p>
          <p *ngIf="toast.message" class="mt-1 text-sm" [ngClass]="{
            'text-green-700': toast.type === 'success',
            'text-red-700': toast.type === 'error',
            'text-yellow-700': toast.type === 'warning',
            'text-blue-700': toast.type === 'info'
          }">{{ toast.message }}</p>
        </div>
        <button *ngIf="toast.dismissible"
          (click)="toastService.dismiss(toast.id)"
          class="flex-shrink-0 ml-2 text-secondary-400 hover:text-secondary-600 transition-colors"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
    </div>
  `,
})
export class ToastComponent {
  toastService = inject(ToastService);
}
