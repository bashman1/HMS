import { Injectable, signal } from '@angular/core';
import { Toast, ToastConfig } from '../models/toast.model';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private toastsSignal = signal<Toast[]>([]);
  
  get toasts() {
    return this.toastsSignal();
  }
  
  private config: ToastConfig = {
    duration: 5000,
    dismissible: true,
    position: 'top-right'
  };

  show(toast: Omit<Toast, 'id'>): void {
    const newToast: Toast = {
      ...toast,
      id: this.generateId(),
      duration: toast.duration ?? this.config.duration,
      dismissible: toast.dismissible ?? this.config.dismissible
    };
    
    this.toastsSignal.update(toasts => [...toasts, newToast]);
    
    if (newToast.duration && newToast.duration > 0) {
      setTimeout(() => this.dismiss(newToast.id), newToast.duration);
    }
  }

  success(title: string, message?: string): void {
    this.show({ type: 'success', title, message });
  }

  error(title: string, message?: string): void {
    this.show({ type: 'error', title, message });
  }

  warning(title: string, message?: string): void {
    this.show({ type: 'warning', title, message });
  }

  info(title: string, message?: string): void {
    this.show({ type: 'info', title, message });
  }

  dismiss(id: string): void {
    this.toastsSignal.update(toasts => toasts.filter(t => t.id !== id));
  }

  dismissAll(): void {
    this.toastsSignal.set([]);
  }

  private generateId(): string {
    return `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }
}
