import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { VisitService } from '../../../core/services/visit.service';
import { ToastService } from '../../../core/services/toast.service';
import { Visit, VisitStatus } from '../../../core/models/visit.model';

@Component({
  selector: 'app-visit-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="visit-details-container">
      <div class="page-header">
        <a routerLink="/visits" class="back-link">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
          </svg>
          Back to Visits
        </a>
        <h1>Visit Details</h1>
        <p class="subtitle">View and manage visit information</p>
      </div>

      <div *ngIf="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading visit details...</p>
      </div>

      <div *ngIf="!loading && visit" class="visit-content">
        <!-- Visit Info Card -->
        <div class="info-card">
          <div class="card-header">
            <div>
              <h2>{{ visit.visitNumber }}</h2>
              <span class="status-badge" [class]="getStatusClass(visit.status)">
                {{ formatStatus(visit.status) }}
              </span>
            </div>
            <div class="token-info">
              <span class="token-label">Token #</span>
              <span class="token-number">{{ visit.tokenNumber }}</span>
            </div>
          </div>

          <div class="info-grid">
            <div class="info-item">
              <label>Patient</label>
              <span>{{ visit.patientName }}</span>
            </div>
            <div class="info-item">
              <label>UHID</label>
              <span>{{ visit.patientUhid }}</span>
            </div>
            <div class="info-item">
              <label>Visit Type</label>
              <span>{{ visit.visitType }}</span>
            </div>
            <div class="info-item">
              <label>Department</label>
              <span>{{ visit.departmentName }}</span>
            </div>
            <div class="info-item">
              <label>Doctor</label>
              <span>{{ visit.doctorName || 'Not assigned' }}</span>
            </div>
            <div class="info-item">
              <label>Visit Date</label>
              <span>{{ visit.visitDate | date:'medium' }}</span>
            </div>
            <div class="info-item">
              <label>Chief Complaint</label>
              <span>{{ visit.chiefComplaint || 'Not specified' }}</span>
            </div>
          </div>
        </div>

        <!-- Consultation Notes Section (for doctors) -->
        <div class="card" *ngIf="canEditConsultation()">
          <div class="card-header">
            <h3>Consultation Notes</h3>
          </div>
          <form [formGroup]="consultationForm" (ngSubmit)="saveConsultation()">
            <div class="form-group">
              <label for="diagnosis">Diagnosis *</label>
              <textarea
                id="diagnosis"
                formControlName="diagnosis"
                rows="3"
                placeholder="Enter diagnosis..."
              ></textarea>
            </div>
            <div class="form-group">
              <label for="treatmentPlan">Treatment Plan</label>
              <textarea
                id="treatmentPlan"
                formControlName="treatmentPlan"
                rows="3"
                placeholder="Enter treatment plan..."
              ></textarea>
            </div>
            <div class="form-group">
              <label for="prescription">Prescription</label>
              <textarea
                id="prescription"
                formControlName="prescription"
                rows="3"
                placeholder="Enter prescription..."
              ></textarea>
            </div>
            <div class="form-actions">
              <button type="submit" class="btn-primary" [disabled]="consultationForm.invalid || saving">
                {{ saving ? 'Saving...' : 'Save Consultation Notes' }}
              </button>
            </div>
          </form>
        </div>

        <!-- View Consultation Notes -->
        <div class="card" *ngIf="visit.diagnosis || visit.treatmentPlan || visit.prescription">
          <div class="card-header">
            <h3>Consultation Notes</h3>
          </div>
          <div class="consultation-view">
            <div class="info-item" *ngIf="visit.diagnosis">
              <label>Diagnosis</label>
              <span>{{ visit.diagnosis }}</span>
            </div>
            <div class="info-item" *ngIf="visit.treatmentPlan">
              <label>Treatment Plan</label>
              <span>{{ visit.treatmentPlan }}</span>
            </div>
            <div class="info-item" *ngIf="visit.prescription">
              <label>Prescription</label>
              <span>{{ visit.prescription }}</span>
            </div>
          </div>
        </div>

        <!-- Referral Section -->
        <div class="card" *ngIf="visit.status === 'REFERRED' || canRefer()">
          <div class="card-header">
            <h3>Referral Information</h3>
          </div>
          
          <!-- View Referral -->
          <div *ngIf="visit.referredDepartmentName" class="referral-view">
            <div class="info-item">
              <label>Referred To</label>
              <span>{{ visit.referredDepartmentName }}</span>
            </div>
            <div class="info-item" *ngIf="visit.referralReason">
              <label>Reason</label>
              <span>{{ visit.referralReason }}</span>
            </div>
          </div>

          <!-- Make Referral -->
          <form *ngIf="!visit.referredDepartmentName" [formGroup]="referralForm" (ngSubmit)="submitReferral()">
            <div class="form-group">
              <label for="referredDepartmentName">Referred Department *</label>
              <input
                type="text"
                id="referredDepartmentName"
                formControlName="referredDepartmentName"
                placeholder="Enter department name..."
              />
            </div>
            <div class="form-group">
              <label for="referralReason">Reason for Referral</label>
              <textarea
                id="referralReason"
                formControlName="referralReason"
                rows="3"
                placeholder="Enter referral reason..."
              ></textarea>
            </div>
            <div class="form-actions">
              <button type="submit" class="btn-warning" [disabled]="referralForm.invalid || saving">
                {{ saving ? 'Referring...' : 'Refer Patient' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Actions -->
        <div class="actions-card">
          <h3>Actions</h3>
          <div class="action-buttons">
            <button 
              *ngIf="visit.status === 'REGISTERED'" 
              class="btn-primary"
              (click)="checkInPatient()">
              Check In
            </button>
            <button 
              *ngIf="visit.status === 'IN_QUEUE'" 
              class="btn-primary"
              (click)="startConsultation()">
              Start Consultation
            </button>
            <button 
              *ngIf="visit.status === 'IN_CONSULTATION'" 
              class="btn-success"
              (click)="completeVisit()">
              Complete Visit
            </button>
            <button 
              *ngIf="canRefer()" 
              class="btn-warning"
              (click)="showReferralForm = true">
              Refer Patient
            </button>
            <button 
              *ngIf="visit.status !== 'COMPLETED' && visit.status !== 'CANCELLED'" 
              class="btn-danger"
              (click)="cancelVisit()">
              Cancel Visit
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .visit-details-container {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .page-header {
      margin-bottom: 24px;
    }

    .back-link {
      display: inline-flex;
      align-items: center;
      color: #6b7280;
      text-decoration: none;
      margin-bottom: 8px;
      transition: color 0.2s;
    }

    .back-link:hover {
      color: #374151;
    }

    .page-header h1 {
      font-size: 24px;
      font-weight: 600;
      color: #111827;
      margin: 0 0 4px 0;
    }

    .subtitle {
      color: #6b7280;
      margin: 0;
    }

    .loading-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 48px;
      color: #6b7280;
    }

    .spinner {
      width: 40px;
      height: 40px;
      border: 3px solid #e5e7eb;
      border-top-color: #3b82f6;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin-bottom: 16px;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    .info-card, .card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      padding: 24px;
      margin-bottom: 24px;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 16px;
      border-bottom: 1px solid #e5e7eb;
    }

    .card-header h2 {
      font-size: 20px;
      font-weight: 600;
      color: #111827;
      margin: 0 0 8px 0;
    }

    .card-header h3 {
      font-size: 18px;
      font-weight: 600;
      color: #111827;
      margin: 0;
    }

    .status-badge {
      display: inline-block;
      padding: 4px 12px;
      border-radius: 9999px;
      font-size: 12px;
      font-weight: 500;
    }

    .status-REGISTERED { background: #dbeafe; color: #1e40af; }
    .status-IN_QUEUE { background: #fef3c7; color: #92400e; }
    .status-IN_CONSULTATION { background: #d1fae5; color: #065f46; }
    .status-COMPLETED { background: #e5e7eb; color: #374151; }
    .status-CANCELLED { background: #fee2e2; color: #991b1b; }
    .status-NO_SHOW { background: #f3f4f6; color: #6b7280; }
    .status-REFERRED { background: #fce7f3; color: #9d174d; }

    .token-info {
      text-align: right;
    }

    .token-label {
      display: block;
      font-size: 12px;
      color: #6b7280;
      margin-bottom: 4px;
    }

    .token-number {
      font-size: 28px;
      font-weight: 700;
      color: #3b82f6;
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
    }

    .info-item {
      display: flex;
      flex-direction: column;
    }

    .info-item label {
      font-size: 12px;
      color: #6b7280;
      margin-bottom: 4px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .info-item span {
      font-size: 14px;
      color: #111827;
      font-weight: 500;
    }

    .form-group {
      margin-bottom: 16px;
    }

    .form-group label {
      display: block;
      font-size: 14px;
      font-weight: 500;
      color: #374151;
      margin-bottom: 6px;
    }

    .form-group input,
    .form-group textarea {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #d1d5db;
      border-radius: 8px;
      font-size: 14px;
      transition: border-color 0.2s, box-shadow 0.2s;
    }

    .form-group input:focus,
    .form-group textarea:focus {
      outline: none;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    .form-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 20px;
    }

    .btn-primary {
      background: #3b82f6;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.2s;
    }

    .btn-primary:hover:not(:disabled) {
      background: #2563eb;
    }

    .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .btn-success {
      background: #10b981;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.2s;
    }

    .btn-success:hover:not(:disabled) {
      background: #059669;
    }

    .btn-warning {
      background: #f59e0b;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.2s;
    }

    .btn-warning:hover:not(:disabled) {
      background: #d97706;
    }

    .btn-danger {
      background: #ef4444;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.2s;
    }

    .btn-danger:hover:not(:disabled) {
      background: #dc2626;
    }

    .actions-card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      padding: 24px;
    }

    .actions-card h3 {
      font-size: 16px;
      font-weight: 600;
      color: #111827;
      margin: 0 0 16px 0;
    }

    .action-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
    }

    .consultation-view, .referral-view {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
  `]
})
export class VisitDetailsComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private visitService = inject(VisitService);
  private toast = inject(ToastService);
  private destroy$ = new Subject<void>();

  visit: Visit | null = null;
  loading = true;
  saving = false;
  showReferralForm = false;

  consultationForm: FormGroup = this.fb.group({
    diagnosis: ['', Validators.required],
    treatmentPlan: [''],
    prescription: ['']
  });

  referralForm: FormGroup = this.fb.group({
    referredDepartmentName: ['', Validators.required],
    referralReason: ['']
  });

  ngOnInit(): void {
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      if (params['uuid']) {
        this.loadVisit(params['uuid']);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadVisit(uuid: string): void {
    this.loading = true;
    this.visitService.getVisit(uuid).pipe(takeUntil(this.destroy$)).subscribe({
      next: (visit) => {
        this.visit = visit;
        this.loading = false;
        // Pre-fill consultation form if notes exist
        if (visit.diagnosis || visit.treatmentPlan || visit.prescription) {
          this.consultationForm.patchValue({
            diagnosis: visit.diagnosis || '',
            treatmentPlan: visit.treatmentPlan || '',
            prescription: visit.prescription || ''
          });
        }
      },
      error: (error) => {
        this.toast.error('Failed to load visit details');
        this.loading = false;
      }
    });
  }

  canEditConsultation(): boolean {
    return this.visit?.status === 'IN_CONSULTATION';
  }

  canRefer(): boolean {
    return this.visit?.status === 'IN_QUEUE' || this.visit?.status === 'IN_CONSULTATION';
  }

  getStatusClass(status: VisitStatus): string {
    return `status-${status}`;
  }

  formatStatus(status: VisitStatus): string {
    return status.replace(/_/g, ' ');
  }

  checkInPatient(): void {
    if (!this.visit) return;
    this.saving = true;
    this.visitService.checkInPatient(this.visit.uuid).pipe(takeUntil(this.destroy$)).subscribe({
      next: (visit) => {
        this.visit = visit;
        this.toast.success('Patient checked in successfully');
        this.saving = false;
      },
      error: () => {
        this.toast.error('Failed to check in patient');
        this.saving = false;
      }
    });
  }

  startConsultation(): void {
    if (!this.visit) return;
    this.saving = true;
    this.visitService.updateVisitStatus(this.visit.uuid, VisitStatus.IN_CONSULTATION).pipe(takeUntil(this.destroy$)).subscribe({
      next: (visit) => {
        this.visit = visit;
        this.toast.success('Consultation started');
        this.saving = false;
      },
      error: () => {
        this.toast.error('Failed to start consultation');
        this.saving = false;
      }
    });
  }

  completeVisit(): void {
    if (!this.visit) return;
    this.saving = true;
    this.visitService.updateVisitStatus(this.visit.uuid, VisitStatus.COMPLETED).pipe(takeUntil(this.destroy$)).subscribe({
      next: (visit) => {
        this.visit = visit;
        this.toast.success('Visit completed successfully');
        this.saving = false;
      },
      error: () => {
        this.toast.error('Failed to complete visit');
        this.saving = false;
      }
    });
  }

  cancelVisit(): void {
    if (!this.visit) return;
    if (confirm('Are you sure you want to cancel this visit?')) {
      this.saving = true;
      this.visitService.updateVisitStatus(this.visit.uuid, VisitStatus.CANCELLED).pipe(takeUntil(this.destroy$)).subscribe({
        next: (visit) => {
          this.visit = visit;
          this.toast.success('Visit cancelled');
          this.saving = false;
        },
        error: () => {
          this.toast.error('Failed to cancel visit');
          this.saving = false;
        }
      });
    }
  }

  saveConsultation(): void {
    if (!this.visit || this.consultationForm.invalid) return;
    this.saving = true;
    const { diagnosis, treatmentPlan, prescription } = this.consultationForm.value;
    this.visitService.saveConsultationNotes(this.visit.uuid, diagnosis, treatmentPlan, prescription)
      .pipe(takeUntil(this.destroy$)).subscribe({
        next: (visit) => {
          this.visit = visit;
          this.toast.success('Consultation notes saved');
          this.saving = false;
        },
        error: () => {
          this.toast.error('Failed to save consultation notes');
          this.saving = false;
        }
      });
  }

  submitReferral(): void {
    if (!this.visit || this.referralForm.invalid) return;
    this.saving = true;
    const { referredDepartmentName, referralReason } = this.referralForm.value;
    this.visitService.referPatient(this.visit.uuid, 0, referredDepartmentName, referralReason)
      .pipe(takeUntil(this.destroy$)).subscribe({
        next: (visit) => {
          this.visit = visit;
          this.showReferralForm = false;
          this.toast.success('Patient referred successfully');
          this.saving = false;
        },
        error: () => {
          this.toast.error('Failed to refer patient');
          this.saving = false;
        }
      });
  }
}
