import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '@core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex">
      <!-- Left Side - Image/Pattern -->
      <div class="hidden lg:flex lg:flex-1 bg-primary-600 items-center justify-center p-12">
        <div class="max-w-lg text-center">
          <svg class="w-64 h-64 mx-auto text-white/20" fill="currentColor" viewBox="0 0 24 24">
            <path d="M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
          </svg>
          <h2 class="mt-8 text-3xl font-bold text-white">Join HMS Today</h2>
          <p class="mt-4 text-primary-100 text-lg">
            Start managing your healthcare facility with our powerful tools.
          </p>
          <div class="mt-8 grid grid-cols-2 gap-4 text-left max-w-md mx-auto">
            <div class="flex items-center text-primary-100">
              <svg class="w-5 h-5 mr-2 text-primary-300" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
              </svg>
              Free 14-day trial
            </div>
            <div class="flex items-center text-primary-100">
              <svg class="w-5 h-5 mr-2 text-primary-300" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
              </svg>
              No credit card required
            </div>
            <div class="flex items-center text-primary-100">
              <svg class="w-5 h-5 mr-2 text-primary-300" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
              </svg>
              Cancel anytime
            </div>
            <div class="flex items-center text-primary-100">
              <svg class="w-5 h-5 mr-2 text-primary-300" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
              </svg>
              24/7 support
            </div>
          </div>
        </div>
      </div>

      <!-- Right Side - Form -->
      <div class="flex-1 flex items-center justify-center p-8">
        <div class="w-full max-w-md">
          <div class="text-center mb-8">
            <a routerLink="/" class="inline-flex items-center">
              <span class="text-3xl font-bold text-primary-600">HMS</span>
            </a>
            <h1 class="mt-6 text-3xl font-bold text-secondary-900">Create an account</h1>
            <p class="mt-2 text-secondary-600">Start your free 14-day trial today</p>
          </div>

          <div *ngIf="error()" class="alert alert-error mb-6">
            <p>{{ error() }}</p>
          </div>

          <div *ngIf="success()" class="alert alert-success mb-6">
            <p>{{ success() }}</p>
            <a routerLink="/auth/login" class="link mt-2 inline-block">Go to login</a>
          </div>

          <form *ngIf="!success()" [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="space-y-5">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label for="firstName" class="label">First name</label>
                <input
                  type="text"
                  id="firstName"
                  formControlName="firstName"
                  class="input"
                  [class.input-error]="registerForm.get('firstName')?.invalid && registerForm.get('firstName')?.touched"
                  placeholder="John"
                />
                <p *ngIf="registerForm.get('firstName')?.invalid && registerForm.get('firstName')?.touched" class="mt-1 text-sm text-danger-500">First name is required</p>
              </div>
              <div>
                <label for="lastName" class="label">Last name</label>
                <input
                  type="text"
                  id="lastName"
                  formControlName="lastName"
                  class="input"
                  [class.input-error]="registerForm.get('lastName')?.invalid && registerForm.get('lastName')?.touched"
                  placeholder="Doe"
                />
                <p *ngIf="registerForm.get('lastName')?.invalid && registerForm.get('lastName')?.touched" class="mt-1 text-sm text-danger-500">Last name is required</p>
              </div>
            </div>

            <div>
              <label for="email" class="label">Email address</label>
              <input
                type="email"
                id="email"
                formControlName="email"
                class="input"
                [class.input-error]="registerForm.get('email')?.invalid && registerForm.get('email')?.touched"
                placeholder="john@example.com"
              />
              <p *ngIf="registerForm.get('email')?.invalid && registerForm.get('email')?.touched" class="mt-1 text-sm text-danger-500">
                <span *ngIf="registerForm.get('email')?.hasError('required')">Email is required</span>
                <span *ngIf="registerForm.get('email')?.hasError('email')">Please enter a valid email</span>
              </p>
            </div>

            <div>
              <label for="phoneNumber" class="label">Phone number (optional)</label>
              <input
                type="tel"
                id="phoneNumber"
                formControlName="phoneNumber"
                class="input"
                placeholder="+1 (555) 000-0000"
              />
            </div>

            <div>
              <label for="password" class="label">Password</label>
              <div class="relative">
                <input
                  [type]="showPassword() ? 'text' : 'password'"
                  id="password"
                  formControlName="password"
                  class="input pr-10"
                  [class.input-error]="registerForm.get('password')?.invalid && registerForm.get('password')?.touched"
                  placeholder="Create a password"
                />
                <button
                  type="button"
                  (click)="showPassword.set(!showPassword())"
                  class="absolute inset-y-0 right-0 px-3 flex items-center text-secondary-400 hover:text-secondary-600"
                >
                  <svg *ngIf="showPassword()" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
                  </svg>
                  <svg *ngIf="!showPassword()" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                  </svg>
                </button>
              </div>
              <p *ngIf="registerForm.get('password')?.invalid && registerForm.get('password')?.touched" class="mt-1 text-sm text-danger-500">Password is required</p>
            </div>

            <div>
              <label for="confirmPassword" class="label">Confirm Password</label>
              <input
                [type]="showPassword() ? 'text' : 'password'"
                id="confirmPassword"
                formControlName="confirmPassword"
                class="input"
                [class.input-error]="registerForm.get('confirmPassword')?.invalid && registerForm.get('confirmPassword')?.touched"
                placeholder="Confirm your password"
              />
              <p *ngIf="registerForm.get('confirmPassword')?.invalid && registerForm.get('confirmPassword')?.touched" class="mt-1 text-sm text-danger-500">Please confirm your password</p>
            </div>

            <div class="flex items-start">
              <input
                type="checkbox"
                formControlName="acceptTerms"
                id="acceptTerms"
                class="w-4 h-4 mt-1 text-primary-600 border-secondary-300 rounded focus:ring-primary-500"
              />
              <label for="acceptTerms" class="ml-2 text-sm text-secondary-600">
                I agree to the <a href="#" class="link">Terms of Service</a> and <a href="#" class="link">Privacy Policy</a>
              </label>
            </div>

            <button
              type="submit"
              [disabled]="registerForm.invalid || isLoading()"
              class="btn-primary w-full py-3"
            >
              <svg *ngIf="isLoading()" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isLoading() ? 'Creating account...' : 'Create account' }}
            </button>
          </form>

          <p class="mt-6 text-center text-secondary-600">
            Already have an account?
            <a routerLink="/auth/login" class="link">Sign in</a>
          </p>
        </div>
      </div>
    </div>
  `,
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  showPassword = signal(false);
  isLoading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  registerForm = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: [''],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
    acceptTerms: [false, [Validators.requiredTrue]],
  }, { validators: this.passwordMatchValidator });

  passwordMatchValidator(form: any) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ mismatch: true });
      return { mismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.isLoading.set(true);
    this.error.set(null);

    const { firstName, lastName, email, phoneNumber, password } = this.registerForm.value;

    this.authService.register({
      firstName: firstName!,
      lastName: lastName!,
      email: email!,
      phoneNumber: phoneNumber || undefined,
      password: password!
    }).subscribe({
      next: (response) => {
        this.isLoading.set(false);
        this.success.set(response.message || 'Registration successful! Please check your email to verify your account.');
      },
      error: (err: any) => {
        this.isLoading.set(false);
        this.error.set(err.error?.message || 'Registration failed. Please try again.');
      },
    });
  }
}
