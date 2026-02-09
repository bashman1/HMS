import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientService } from '../../../core/services/patient.service';
import { ToastService } from '../../../core/services/toast.service';
import { Gender, BloodGroup, MaritalStatus, PatientRegistrationRequest } from '../../../core/models/patient.model';

@Component({
  selector: 'app-patient-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="patient-registration-container">
      <div class="page-header">
        <h1>Patient Registration</h1>
        <p class="subtitle">Register a new patient in the system</p>
      </div>

      <form [formGroup]="registrationForm" (ngSubmit)="onSubmit()" class="registration-form">
        <!-- Personal Information Section -->
        <div class="form-section">
          <h2>Personal Information</h2>
          <div class="form-grid">
            <div class="form-group">
              <label for="firstName">First Name *</label>
              <input id="firstName" type="text" formControlName="firstName" 
                     [class.error]="isFieldInvalid('firstName')">
              <span class="error-message" *ngIf="isFieldInvalid('firstName')">
                First name is required (min 2 characters)
              </span>
            </div>

            <div class="form-group">
              <label for="middleName">Middle Name</label>
              <input id="middleName" type="text" formControlName="middleName">
            </div>

            <div class="form-group">
              <label for="lastName">Last Name *</label>
              <input id="lastName" type="text" formControlName="lastName"
                     [class.error]="isFieldInvalid('lastName')">
              <span class="error-message" *ngIf="isFieldInvalid('lastName')">
                Last name is required (min 2 characters)
              </span>
            </div>

            <div class="form-group">
              <label for="dateOfBirth">Date of Birth *</label>
              <input id="dateOfBirth" type="date" formControlName="dateOfBirth"
                     [class.error]="isFieldInvalid('dateOfBirth')">
              <span class="error-message" *ngIf="isFieldInvalid('dateOfBirth')">
                Date of birth is required
              </span>
            </div>

            <div class="form-group">
              <label for="gender">Gender *</label>
              <select id="gender" formControlName="gender" [class.error]="isFieldInvalid('gender')">
                <option value="">Select Gender</option>
                <option *ngFor="let g of genders" [value]="g">{{ g | titlecase }}</option>
              </select>
              <span class="error-message" *ngIf="isFieldInvalid('gender')">
                Gender is required
              </span>
            </div>

            <div class="form-group">
              <label for="bloodGroup">Blood Group</label>
              <select id="bloodGroup" formControlName="bloodGroup">
                <option value="">Select Blood Group</option>
                <option *ngFor="let bg of bloodGroups" [value]="bg">{{ bg | titlecase }}</option>
              </select>
            </div>

            <div class="form-group">
              <label for="maritalStatus">Marital Status</label>
              <select id="maritalStatus" formControlName="maritalStatus">
                <option value="">Select Marital Status</option>
                <option *ngFor="let ms of maritalStatuses" [value]="ms">{{ ms | titlecase }}</option>
              </select>
            </div>

            <div class="form-group">
              <label for="nationality">Nationality</label>
              <input id="nationality" type="text" formControlName="nationality">
            </div>

            <div class="form-group">
              <label for="religion">Religion</label>
              <input id="religion" type="text" formControlName="religion">
            </div>

            <div class="form-group">
              <label for="occupation">Occupation</label>
              <input id="occupation" type="text" formControlName="occupation">
            </div>
          </div>
        </div>

        <!-- Contact Information Section -->
        <div class="form-section">
          <h2>Contact Information</h2>
          <div class="form-grid">
            <div class="form-group">
              <label for="phonePrimary">Primary Phone *</label>
              <input id="phonePrimary" type="tel" formControlName="phonePrimary"
                     [class.error]="isFieldInvalid('phonePrimary')"
                     placeholder="+1234567890">
              <span class="error-message" *ngIf="isFieldInvalid('phonePrimary')">
                Valid phone number is required (10-15 digits)
              </span>
            </div>

            <div class="form-group">
              <label for="phoneSecondary">Secondary Phone</label>
              <input id="phoneSecondary" type="tel" formControlName="phoneSecondary"
                     placeholder="+1234567890">
            </div>

            <div class="form-group">
              <label for="email">Email</label>
              <input id="email" type="email" formControlName="email"
                     [class.error]="isFieldInvalid('email')">
              <span class="error-message" *ngIf="isFieldInvalid('email')">
                Valid email is required
              </span>
            </div>

            <div class="form-group full-width">
              <label for="addressLine1">Address Line 1</label>
              <input id="addressLine1" type="text" formControlName="addressLine1">
            </div>

            <div class="form-group full-width">
              <label for="addressLine2">Address Line 2</label>
              <input id="addressLine2" type="text" formControlName="addressLine2">
            </div>

            <div class="form-group">
              <label for="city">City</label>
              <input id="city" type="text" formControlName="city">
            </div>

            <div class="form-group">
              <label for="state">State</label>
              <input id="state" type="text" formControlName="state">
            </div>

            <div class="form-group">
              <label for="postalCode">Postal Code</label>
              <input id="postalCode" type="text" formControlName="postalCode">
            </div>

            <div class="form-group">
              <label for="country">Country</label>
              <input id="country" type="text" formControlName="country">
            </div>
          </div>
        </div>

        <!-- Emergency Contact Section -->
        <div class="form-section">
          <h2>Emergency Contact</h2>
          <div class="form-grid">
            <div class="form-group">
              <label for="emergencyContactName">Contact Name</label>
              <input id="emergencyContactName" type="text" formControlName="emergencyContactName">
            </div>

            <div class="form-group">
              <label for="emergencyContactPhone">Contact Phone</label>
              <input id="emergencyContactPhone" type="tel" formControlName="emergencyContactPhone"
                     placeholder="+1234567890">
            </div>

            <div class="form-group">
              <label for="emergencyContactRelation">Relationship</label>
              <input id="emergencyContactRelation" type="text" formControlName="emergencyContactRelation">
            </div>
          </div>
        </div>

        <!-- Medical Information Section -->
        <div class="form-section">
          <h2>Medical Information</h2>
          <div class="form-grid">
            <div class="form-group full-width">
              <label for="allergies">Allergies</label>
              <textarea id="allergies" formControlName="allergies" rows="3"
                        placeholder="List any known allergies..."></textarea>
            </div>

            <div class="form-group full-width">
              <label for="chronicConditions">Chronic Conditions</label>
              <textarea id="chronicConditions" formControlName="chronicConditions" rows="3"
                        placeholder="List any chronic conditions..."></textarea>
            </div>
          </div>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
          <button type="button" class="btn-secondary" (click)="resetForm()">Reset</button>
          <button type="submit" class="btn-primary" [disabled]="registrationForm.invalid || isSubmitting">
            {{ isSubmitting ? 'Registering...' : 'Register Patient' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .patient-registration-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    .page-header {
      margin-bottom: 2rem;
    }

    .page-header h1 {
      font-size: 2rem;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0;
    }

    .subtitle {
      color: #666;
      margin-top: 0.5rem;
    }

    .registration-form {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }

    .form-section {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .form-section h2 {
      font-size: 1.25rem;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 1.5rem 0;
      padding-bottom: 0.75rem;
      border-bottom: 1px solid #e5e7eb;
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 1.25rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .form-group.full-width {
      grid-column: 1 / -1;
    }

    .form-group label {
      font-size: 0.875rem;
      font-weight: 500;
      color: #374151;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
      padding: 0.625rem 0.875rem;
      border: 1px solid #d1d5db;
      border-radius: 6px;
      font-size: 0.9375rem;
      transition: border-color 0.2s, box-shadow 0.2s;
    }

    .form-group input:focus,
    .form-group select:focus,
    .form-group textarea:focus {
      outline: none;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    .form-group input.error,
    .form-group select.error,
    .form-group textarea.error {
      border-color: #ef4444;
    }

    .error-message {
      font-size: 0.75rem;
      color: #ef4444;
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      padding-top: 1rem;
    }

    .btn-primary,
    .btn-secondary {
      padding: 0.75rem 1.5rem;
      border-radius: 8px;
      font-size: 0.9375rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;
    }

    .btn-primary {
      background: #3b82f6;
      color: white;
      border: none;
    }

    .btn-primary:hover:not(:disabled) {
      background: #2563eb;
    }

    .btn-primary:disabled {
      background: #9ca3af;
      cursor: not-allowed;
    }

    .btn-secondary {
      background: white;
      color: #374151;
      border: 1px solid #d1d5db;
    }

    .btn-secondary:hover {
      background: #f9fafb;
      border-color: #9ca3af;
    }

    @media (max-width: 768px) {
      .form-grid {
        grid-template-columns: 1fr;
      }

      .form-group.full-width {
        grid-column: 1;
      }
    }
  `]
})
export class PatientRegistrationComponent {
  private fb = inject(FormBuilder);
  private patientService = inject(PatientService);
  private router = inject(Router);
  private toastService = inject(ToastService);

  registrationForm!: FormGroup;
  isSubmitting = false;

  genders = Object.values(Gender);
  bloodGroups = Object.values(BloodGroup);
  maritalStatuses = Object.values(MaritalStatus);

  constructor() {
    this.initForm();
  }

  private initForm(): void {
    this.registrationForm = this.fb.group({
      // Personal Information
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      middleName: [''],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      bloodGroup: [''],
      maritalStatus: [''],
      nationality: [''],
      religion: [''],
      occupation: [''],

      // Contact Information
      phonePrimary: ['', [Validators.required, Validators.pattern(/^\+?[0-9]{10,15}$/)]],
      phoneSecondary: [''],
      email: ['', Validators.email],
      addressLine1: [''],
      addressLine2: [''],
      city: [''],
      state: [''],
      postalCode: [''],
      country: [''],

      // Emergency Contact
      emergencyContactName: [''],
      emergencyContactPhone: [''],
      emergencyContactRelation: [''],

      // Medical Information
      allergies: [''],
      chronicConditions: ['']
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registrationForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  resetForm(): void {
    this.registrationForm.reset();
  }

  onSubmit(): void {
    if (this.registrationForm.invalid) {
      Object.keys(this.registrationForm.controls).forEach(key => {
        this.registrationForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isSubmitting = true;
    const request: PatientRegistrationRequest = this.registrationForm.value;

    this.patientService.registerPatient(request).subscribe({
      next: (patient) => {
        this.isSubmitting = false;
        this.toastService.success('Patient registered successfully', 'Success');
        this.router.navigate(['/patients', patient.uuid]);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.toastService.error('Failed to register patient', error.error?.message || 'Error');
      }
    });
  }
}
