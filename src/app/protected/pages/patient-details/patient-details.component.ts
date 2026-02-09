import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../../core/services/patient.service';
import { VisitService } from '../../../core/services/visit.service';
import { ToastService } from '../../../core/services/toast.service';
import { Patient } from '../../../core/models/patient.model';
import { Visit } from '../../../core/models/visit.model';

@Component({
  selector: 'app-patient-details',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="patient-details-container" *ngIf="patient">
      <div class="page-header">
        <button class="btn-back" (click)="goBack()">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          Back
        </button>
        <div class="header-info">
          <h1>{{ patient.fullName }}</h1>
          <span class="uhid">{{ patient.uhid }}</span>
        </div>
        <button class="btn-primary" (click)="createVisit()">Create Visit</button>
      </div>

      <div class="patient-grid">
        <!-- Patient Information Card -->
        <div class="info-card">
          <h2>Patient Information</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>Full Name</label>
              <span>{{ patient.fullName }}</span>
            </div>
            <div class="info-item">
              <label>Date of Birth</label>
              <span>{{ patient.dateOfBirth | date:'mediumDate' }} ({{ patient.age }} years)</span>
            </div>
            <div class="info-item">
              <label>Gender</label>
              <span>{{ patient.gender | titlecase }}</span>
            </div>
            <div class="info-item">
              <label>Blood Group</label>
              <span>{{ patient.bloodGroup || 'Not specified' }}</span>
            </div>
            <div class="info-item">
              <label>Phone</label>
              <span>{{ patient.phonePrimary }}</span>
            </div>
            <div class="info-item">
              <label>Email</label>
              <span>{{ patient.email || 'Not provided' }}</span>
            </div>
            <div class="info-item full-width">
              <label>Address</label>
              <span>{{ getFullAddress() }}</span>
            </div>
          </div>
        </div>

        <!-- Medical Information Card -->
        <div class="info-card">
          <h2>Medical Information</h2>
          <div class="info-grid">
            <div class="info-item full-width">
              <label>Allergies</label>
              <span class="medical-info">{{ patient.allergies || 'None reported' }}</span>
            </div>
            <div class="info-item full-width">
              <label>Chronic Conditions</label>
              <span class="medical-info">{{ patient.chronicConditions || 'None reported' }}</span>
            </div>
          </div>
        </div>

        <!-- Emergency Contact Card -->
        <div class="info-card">
          <h2>Emergency Contact</h2>
          <div class="info-grid">
            <div class="info-item">
              <label>Name</label>
              <span>{{ patient.emergencyContactName || 'Not provided' }}</span>
            </div>
            <div class="info-item">
              <label>Phone</label>
              <span>{{ patient.emergencyContactPhone || 'Not provided' }}</span>
            </div>
            <div class="info-item">
              <label>Relationship</label>
              <span>{{ patient.emergencyContactRelation || 'Not provided' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Visit History -->
      <div class="visit-history">
        <h2>Visit History</h2>
        <div *ngIf="visits.length === 0" class="empty-state">
          <p>No visits yet</p>
        </div>
        <div *ngIf="visits.length > 0" class="visits-list">
          <div *ngFor="let visit of visits" class="visit-item">
            <div class="visit-date">
              {{ visit.visitDate | date:'mediumDate' }}
            </div>
            <div class="visit-details">
              <span class="visit-type">{{ visit.visitType }}</span>
              <span class="department">{{ visit.departmentName }}</span>
            </div>
            <div class="visit-status">
              <span class="status-badge" [class]="getStatusClass(visit.status)">
                {{ formatStatus(visit.status) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div *ngIf="!patient && !isLoading" class="error-state">
      <p>Patient not found</p>
      <button class="btn-primary" (click)="goBack()">Go Back</button>
    </div>
  `,
  styles: [`
    .patient-details-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    .page-header {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      margin-bottom: 2rem;
    }

    .btn-back {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      background: white;
      border: 1px solid #d1d5db;
      border-radius: 6px;
      font-size: 0.875rem;
      color: #374151;
      cursor: pointer;
    }

    .header-info {
      flex: 1;
    }

    .header-info h1 {
      font-size: 1.75rem;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0;
    }

    .uhid {
      font-size: 0.875rem;
      color: #666;
    }

    .btn-primary {
      padding: 0.625rem 1.25rem;
      background: #3b82f6;
      color: white;
      border: none;
      border-radius: 6px;
      font-size: 0.875rem;
      font-weight: 500;
      cursor: pointer;
    }

    .patient-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1.5rem;
      margin-bottom: 2rem;
    }

    .info-card {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .info-card h2 {
      font-size: 1.125rem;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 1.25rem 0;
      padding-bottom: 0.75rem;
      border-bottom: 1px solid #e5e7eb;
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
    }

    .info-item {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .info-item.full-width {
      grid-column: 1 / -1;
    }

    .info-item label {
      font-size: 0.75rem;
      font-weight: 500;
      color: #9ca3af;
      text-transform: uppercase;
      letter-spacing: 0.025em;
    }

    .info-item span {
      font-size: 0.9375rem;
      color: #1a1a1a;
    }

    .medical-info {
      background: #fef3c7;
      padding: 0.75rem;
      border-radius: 6px;
      font-size: 0.875rem;
    }

    .visit-history {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .visit-history h2 {
      font-size: 1.125rem;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 1.25rem 0;
    }

    .empty-state {
      padding: 2rem;
      text-align: center;
      color: #9ca3af;
    }

    .visits-list {
      display: flex;
      flex-direction: column;
    }

    .visit-item {
      display: grid;
      grid-template-columns: 120px 1fr 120px;
      align-items: center;
      padding: 1rem 0;
      border-bottom: 1px solid #f3f4f6;
    }

    .visit-date {
      font-size: 0.875rem;
      color: #374151;
    }

    .visit-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .visit-type {
      font-weight: 500;
      color: #1a1a1a;
    }

    .department {
      font-size: 0.75rem;
      color: #9ca3af;
    }

    .status-badge {
      padding: 0.25rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.75rem;
      font-weight: 500;
    }

    .status-badge.registered {
      background: #e5e7eb;
      color: #374151;
    }

    .status-badge.in-queue {
      background: #fef3c7;
      color: #92400e;
    }

    .status-badge.in-consultation {
      background: #dbeafe;
      color: #1e40af;
    }

    .status-badge.completed {
      background: #d1fae5;
      color: #065f46;
    }

    .error-state {
      padding: 3rem;
      text-align: center;
    }

    @media (max-width: 768px) {
      .patient-grid {
        grid-template-columns: 1fr;
      }

      .visit-item {
        grid-template-columns: 1fr;
        gap: 0.5rem;
      }
    }
  `]
})
export class PatientDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private patientService = inject(PatientService);
  private visitService = inject(VisitService);
  private toastService = inject(ToastService);

  patient: Patient | null = null;
  visits: Visit[] = [];
  isLoading = true;

  ngOnInit(): void {
    const uuid = this.route.snapshot.paramMap.get('uuid');
    if (uuid) {
      this.loadPatient(uuid);
      this.loadVisits(uuid);
    }
  }

  loadPatient(uuid: string): void {
    this.patientService.getPatient(uuid).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.toastService.error('Failed to load patient', 'Error');
      }
    });
  }

  loadVisits(patientUuid: string): void {
    this.visitService.getPatientVisits(patientUuid).subscribe({
      next: (visits) => {
        this.visits = visits;
      },
      error: () => {
        this.toastService.error('Failed to load visit history', 'Error');
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  createVisit(): void {
    if (this.patient) {
      this.router.navigate(['/opd/queue'], { queryParams: { patientId: this.patient.uuid } });
    }
  }

  getFullAddress(): string {
    if (!this.patient) return '';
    const parts = [
      this.patient.addressLine1,
      this.patient.addressLine2,
      this.patient.city,
      this.patient.state,
      this.patient.postalCode,
      this.patient.country
    ].filter(Boolean);
    return parts.length > 0 ? parts.join(', ') : 'Not provided';
  }

  getStatusClass(status: string): string {
    return status.toLowerCase().replace(/_/g, '-');
  }

  formatStatus(status: string): string {
    return status.replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, c => c.toUpperCase());
  }
}
