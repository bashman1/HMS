import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { VisitService } from '../../../core/services/visit.service';
import { PatientService } from '../../../core/services/patient.service';
import { ToastService } from '../../../core/services/toast.service';
import { VisitType, CreateVisitRequest } from '../../../core/models/visit.model';
import { Patient } from '../../../core/models/patient.model';

@Component({
  selector: 'app-visit-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="visit-registration-container">
      <div class="page-header">
        <a routerLink="/visits" class="back-link">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
          </svg>
          Back to Visits
        </a>
        <h1>New Visit Registration</h1>
        <p class="subtitle">Register a new patient visit (OPD)</p>
      </div>

      <form [formGroup]="visitForm" (ngSubmit)="onSubmit()" class="registration-form">
        <!-- Patient Selection Section -->
        <div class="form-section">
          <h2>Patient Information</h2>
          <div class="patient-search-container">
            <div class="form-group">
              <label for="patientSearch">Search Patient *</label>
              <div class="search-input-wrapper">
                <input 
                  id="patientSearch" 
                  type="text" 
                  formControlName="patientSearch"
                  placeholder="Search by name, UHID, or phone..."
                  (input)="onPatientSearch()"
                  (focus)="showPatientDropdown = true"
                >
                <span class="search-icon">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                  </svg>
                </span>
              </div>
              
              <!-- Patient Dropdown -->
              <div class="patient-dropdown" *ngIf="showPatientDropdown && (searchResults.length > 0 || isSearching)">
                <div *ngIf="isSearching" class="searching">Searching...</div>
                <button 
                  *ngFor="let patient of searchResults" 
                  type="button"
                  class="patient-option"
                  (click)="selectPatient(patient)"
                >
                  <div class="patient-option-main">
                    <span class="patient-name">{{ patient.firstName }} {{ patient.lastName }}</span>
                    <span class="patient-uhid">{{ patient.uhid }}</span>
                  </div>
                  <div class="patient-option-sub">
                    <span>{{ patient.dateOfBirth | date:'mediumDate' }}</span>
                    <span>{{ patient.phonePrimary }}</span>
                  </div>
                </button>
                <div *ngIf="searchResults.length === 0 && !isSearching && patientSearchTerm.length >= 2" class="no-results">
                  No patients found. <a routerLink="/patients/register">Register new patient</a>
                </div>
              </div>

              <!-- Selected Patient Card -->
              <div class="selected-patient-card" *ngIf="selectedPatient">
                <div class="selected-patient-info">
                  <div class="patient-avatar">
                    {{ getPatientInitials() }}
                  </div>
                  <div class="patient-details">
                    <span class="patient-name">{{ selectedPatient.firstName }} {{ selectedPatient.lastName }}</span>
                    <span class="patient-meta">
                      {{ selectedPatient.uhid }} | {{ selectedPatient.gender | titlecase }} | {{ calculateAge(selectedPatient.dateOfBirth) }} years
                    </span>
                  </div>
                </div>
                <button type="button" class="remove-btn" (click)="clearPatient()">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Visit Details Section -->
        <div class="form-section">
          <h2>Visit Details</h2>
          <div class="form-grid">
            <div class="form-group">
              <label for="visitType">Visit Type *</label>
              <select id="visitType" formControlName="visitType" [class.error]="isFieldInvalid('visitType')">
                <option value="">Select Visit Type</option>
                <option *ngFor="let vt of visitTypes" [value]="vt">{{ formatVisitType(vt) }}</option>
              </select>
              <span class="error-message" *ngIf="isFieldInvalid('visitType')">
                Visit type is required
              </span>
            </div>

            <div class="form-group">
              <label for="departmentId">Department *</label>
              <select id="departmentId" formControlName="departmentId" [class.error]="isFieldInvalid('departmentId')">
                <option value="">Select Department</option>
                <option *ngFor="let dept of departments" [value]="dept.id">{{ dept.name }}</option>
              </select>
              <span class="error-message" *ngIf="isFieldInvalid('departmentId')">
                Department is required
              </span>
            </div>

            <div class="form-group">
              <label for="chiefComplaint">Chief Complaint *</label>
              <input id="chiefComplaint" type="text" formControlName="chiefComplaint"
                     [class.error]="isFieldInvalid('chiefComplaint')"
                     placeholder="Main reason for visit...">
              <span class="error-message" *ngIf="isFieldInvalid('chiefComplaint')">
                Chief complaint is required
              </span>
            </div>

            <div class="form-group">
              <label for="priority">Priority</label>
              <select id="priority" formControlName="priority">
                <option [value]="0">Normal</option>
                <option [value]="1">Urgent</option>
                <option [value]="2">Emergency</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Visit Notes Section -->
        <div class="form-section">
          <h2>Additional Information</h2>
          <div class="form-grid">
            <div class="form-group full-width">
              <label for="notes">Notes</label>
              <textarea id="notes" formControlName="notes" rows="3"
                        placeholder="Any additional notes for this visit..."></textarea>
            </div>

            <div class="form-group">
              <label for="isFollowUp">Follow-up Visit</label>
              <div class="checkbox-wrapper">
                <input type="checkbox" id="isFollowUp" formControlName="isFollowUp">
                <label for="isFollowUp">This is a follow-up visit</label>
              </div>
            </div>
          </div>
        </div>

        <!-- Form Actions -->
        <div class="form-actions">
          <button type="button" class="btn-secondary" (click)="resetForm()">Reset</button>
          <button type="submit" class="btn-primary" [disabled]="visitForm.invalid || isSubmitting || !selectedPatient">
            {{ isSubmitting ? 'Creating Visit...' : 'Create Visit' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .visit-registration-container {
      padding: 2rem;
      max-width: 1000px;
      margin: 0 auto;
    }

    .back-link {
      display: inline-flex;
      align-items: center;
      color: #6b7280;
      text-decoration: none;
      font-size: 0.875rem;
      margin-bottom: 1rem;
      transition: color 0.2s;
    }

    .back-link:hover {
      color: #3b82f6;
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
      grid-template-columns: repeat(2, 1fr);
      gap: 1.25rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      position: relative;
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
    .form-group select.error {
      border-color: #ef4444;
    }

    .error-message {
      font-size: 0.75rem;
      color: #ef4444;
    }

    /* Patient Search Styles */
    .search-input-wrapper {
      position: relative;
    }

    .search-input-wrapper input {
      width: 100%;
      padding-right: 2.5rem;
    }

    .search-icon {
      position: absolute;
      right: 0.875rem;
      top: 50%;
      transform: translateY(-50%);
      color: #9ca3af;
    }

    .patient-dropdown {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: white;
      border: 1px solid #e5e7eb;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      max-height: 300px;
      overflow-y: auto;
      z-index: 50;
      margin-top: 0.25rem;
    }

    .patient-option {
      display: flex;
      flex-direction: column;
      padding: 0.75rem 1rem;
      border: none;
      background: none;
      width: 100%;
      text-align: left;
      cursor: pointer;
      border-bottom: 1px solid #f3f4f6;
      transition: background 0.2s;
    }

    .patient-option:last-child {
      border-bottom: none;
    }

    .patient-option:hover {
      background: #f9fafb;
    }

    .patient-option-main {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .patient-name {
      font-weight: 500;
      color: #1a1a1a;
    }

    .patient-uhid {
      font-size: 0.75rem;
      color: #6b7280;
      background: #f3f4f6;
      padding: 0.125rem 0.5rem;
      border-radius: 4px;
    }

    .patient-option-sub {
      display: flex;
      gap: 1rem;
      margin-top: 0.25rem;
      font-size: 0.75rem;
      color: #6b7280;
    }

    .searching, .no-results {
      padding: 1rem;
      text-align: center;
      color: #6b7280;
    }

    .no-results a {
      color: #3b82f6;
      text-decoration: none;
    }

    .no-results a:hover {
      text-decoration: underline;
    }

    /* Selected Patient Card */
    .selected-patient-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
      background: #f0f9ff;
      border: 1px solid #bae6fd;
      border-radius: 8px;
      margin-top: 0.5rem;
    }

    .selected-patient-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .patient-avatar {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      background: #3b82f6;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: 1rem;
    }

    .patient-details {
      display: flex;
      flex-direction: column;
    }

    .patient-details .patient-name {
      font-weight: 600;
      color: #1a1a1a;
    }

    .patient-meta {
      font-size: 0.875rem;
      color: #6b7280;
    }

    .remove-btn {
      padding: 0.5rem;
      background: none;
      border: none;
      color: #ef4444;
      cursor: pointer;
      border-radius: 4px;
      transition: background 0.2s;
    }

    .remove-btn:hover {
      background: #fee2e2;
    }

    /* Checkbox */
    .checkbox-wrapper {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.625rem 0;
    }

    .checkbox-wrapper input[type="checkbox"] {
      width: 18px;
      height: 18px;
      cursor: pointer;
    }

    .checkbox-wrapper label {
      font-size: 0.9375rem;
      color: #374151;
      cursor: pointer;
    }

    /* Form Actions */
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
export class VisitRegistrationComponent implements OnInit {
  private fb = inject(FormBuilder);
  private visitService = inject(VisitService);
  private patientService = inject(PatientService);
  private router = inject(Router);
  private toastService = inject(ToastService);

  visitForm!: FormGroup;
  isSubmitting = false;
  selectedPatient: Patient | null = null;
  searchResults: Patient[] = [];
  isSearching = false;
  showPatientDropdown = false;
  patientSearchTerm = '';

  visitTypes = Object.values(VisitType);

  // Mock departments - in a real app, these would come from an API
  departments = [
    { id: 1, name: 'General Medicine' },
    { id: 2, name: 'General Surgery' },
    { id: 3, name: 'Pediatrics' },
    { id: 4, name: 'Orthopedics' },
    { id: 5, name: 'Gynecology' },
    { id: 6, name: 'ENT' },
    { id: 7, name: 'Ophthalmology' },
    { id: 8, name: 'Dermatology' },
    { id: 9, name: 'Psychiatry' },
    { id: 10, name: 'Emergency' }
  ];

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.visitForm = this.fb.group({
      patientSearch: [''],
      patientId: [null, Validators.required],
      visitType: ['', Validators.required],
      departmentId: ['', Validators.required],
      chiefComplaint: ['', [Validators.required, Validators.minLength(3)]],
      priority: [0],
      notes: [''],
      isFollowUp: [false]
    });
  }

  onPatientSearch(): void {
    this.patientSearchTerm = this.visitForm.get('patientSearch')?.value || '';
    
    if (this.patientSearchTerm.length >= 2) {
      this.isSearching = true;
      this.showPatientDropdown = true;
      
      this.patientService.searchPatients(this.patientSearchTerm, 0, 10).subscribe({
        next: (response) => {
          this.searchResults = response.content;
          this.isSearching = false;
        },
        error: () => {
          this.searchResults = [];
          this.isSearching = false;
        }
      });
    } else {
      this.searchResults = [];
      this.showPatientDropdown = false;
    }
  }

  selectPatient(patient: Patient): void {
    this.selectedPatient = patient;
    this.visitForm.patchValue({ patientId: patient.id });
    this.visitForm.patchValue({ patientSearch: `${patient.firstName} ${patient.lastName}` });
    this.showPatientDropdown = false;
  }

  clearPatient(): void {
    this.selectedPatient = null;
    this.visitForm.patchValue({ patientId: null, patientSearch: '' });
    this.searchResults = [];
  }

  getPatientInitials(): string {
    if (!this.selectedPatient) return '';
    return `${this.selectedPatient.firstName.charAt(0)}${this.selectedPatient.lastName.charAt(0)}`.toUpperCase();
  }

  calculateAge(dateOfBirth: string): number {
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    return age;
  }

  formatVisitType(vt: VisitType): string {
    return vt.replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, c => c.toUpperCase());
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.visitForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  resetForm(): void {
    this.visitForm.reset();
    this.selectedPatient = null;
    this.searchResults = [];
  }

  onSubmit(): void {
    if (this.visitForm.invalid || !this.selectedPatient) {
      Object.keys(this.visitForm.controls).forEach(key => {
        this.visitForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isSubmitting = true;
    const formValue = this.visitForm.value;
    
    const request: CreateVisitRequest = {
      patientId: formValue.patientId,
      visitType: formValue.visitType,
      departmentId: formValue.departmentId,
      departmentName: this.departments.find(d => d.id === formValue.departmentId)?.name,
      chiefComplaint: formValue.chiefComplaint,
      priority: formValue.priority,
      notes: formValue.notes,
      isFollowUp: formValue.isFollowUp
    };

    this.visitService.createVisit(request).subscribe({
      next: (visit) => {
        this.isSubmitting = false;
        this.toastService.success('Visit created successfully', 'Success');
        this.router.navigate(['/visits']);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.toastService.error('Failed to create visit', error.error?.message || 'Error');
      }
    });
  }
}
