import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, interval } from 'rxjs';
import { VisitService } from '../../../core/services/visit.service';
import { ToastService } from '../../../core/services/toast.service';
import { Visit, VisitStatus } from '../../../core/models/visit.model';

@Component({
  selector: 'app-opd-queue',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="opd-queue-container">
      <div class="page-header">
        <h1>OPD Queue</h1>
        <p class="subtitle">Manage today's patient queue</p>
      </div>

      <div class="queue-stats">
        <div class="stat-card">
          <span class="stat-value">{{ queue.length }}</span>
          <span class="stat-label">In Queue</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ inConsultationCount }}</span>
          <span class="stat-label">In Consultation</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ completedCount }}</span>
          <span class="stat-label">Completed</span>
        </div>
      </div>

      <div class="queue-list">
        <div class="queue-header">
          <h2>Current Queue</h2>
          <button class="btn-refresh" (click)="refreshQueue()">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2"/>
            </svg>
            Refresh
          </button>
        </div>

        <div *ngIf="queue.length === 0" class="empty-state">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4"/>
            <circle 4v2 cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <p>No patients in queue</p>
        </div>

        <div *ngIf="queue.length > 0" class="queue-items">
          <div *ngFor="let visit of queue; let i = index" 
               class="queue-item" 
               [class.current]="visit.status === 'IN_CONSULTATION'">
            <div class="token-number">{{ visit.tokenNumber }}</div>
            <div class="patient-info">
              <span class="patient-name">{{ visit.patientName }}</span>
              <span class="visit-number">{{ visit.visitNumber }}</span>
            </div>
            <div class="visit-details">
              <span class="department">{{ visit.departmentName }}</span>
              <span class="chief-complaint">{{ visit.chiefComplaint || 'No complaint' }}</span>
            </div>
            <div class="visit-status">
              <span class="status-badge" [class]="getStatusClass(visit.status)">
                {{ formatStatus(visit.status) }}
              </span>
            </div>
            <div class="visit-actions">
              <button *ngIf="visit.status === 'IN_QUEUE'" 
                      class="btn-action" 
                      (click)="callPatient(visit)">
                Call
              </button>
              <button *ngIf="visit.status === 'IN_CONSULTATION'" 
                      class="btn-action btn-complete" 
                      (click)="completeVisit(visit)">
                Complete
              </button>
              <button class="btn-action btn-view" 
                      (click)="viewPatient(visit)">
                View
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .opd-queue-container {
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

    .queue-stats {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 1rem;
      margin-bottom: 2rem;
    }

    .stat-card {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      display: flex;
      flex-direction: column;
      align-items: center;
    }

    .stat-value {
      font-size: 2.5rem;
      font-weight: 700;
      color: #3b82f6;
    }

    .stat-label {
      font-size: 0.875rem;
      color: #666;
      margin-top: 0.25rem;
    }

    .queue-list {
      background: white;
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      overflow: hidden;
    }

    .queue-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.25rem 1.5rem;
      border-bottom: 1px solid #e5e7eb;
    }

    .queue-header h2 {
      font-size: 1.25rem;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0;
    }

    .btn-refresh {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      background: #f3f4f6;
      border: none;
      border-radius: 6px;
      font-size: 0.875rem;
      color: #374151;
      cursor: pointer;
      transition: background 0.2s;
    }

    .btn-refresh:hover {
      background: #e5e7eb;
    }

    .empty-state {
      padding: 3rem;
      text-align: center;
      color: #9ca3af;
    }

    .empty-state svg {
      margin-bottom: 1rem;
    }

    .queue-items {
      display: flex;
      flex-direction: column;
    }

    .queue-item {
      display: grid;
      grid-template-columns: 60px 1fr 1fr 120px 140px;
      align-items: center;
      padding: 1rem 1.5rem;
      border-bottom: 1px solid #f3f4f6;
      transition: background 0.2s;
    }

    .queue-item:hover {
      background: #f9fafb;
    }

    .queue-item.current {
      background: #eff6ff;
      border-left: 4px solid #3b82f6;
    }

    .token-number {
      width: 48px;
      height: 48px;
      background: #3b82f6;
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.25rem;
      font-weight: 700;
    }

    .patient-info {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .patient-name {
      font-weight: 600;
      color: #1a1a1a;
    }

    .visit-number {
      font-size: 0.75rem;
      color: #9ca3af;
    }

    .visit-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .department {
      font-size: 0.875rem;
      color: #374151;
    }

    .chief-complaint {
      font-size: 0.75rem;
      color: #9ca3af;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      max-width: 200px;
    }

    .status-badge {
      padding: 0.25rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.75rem;
      font-weight: 500;
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

    .visit-actions {
      display: flex;
      gap: 0.5rem;
    }

    .btn-action {
      padding: 0.5rem 1rem;
      border: none;
      border-radius: 6px;
      font-size: 0.875rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;
    }

    .btn-action:first-child {
      background: #3b82f6;
      color: white;
    }

    .btn-action:first-child:hover {
      background: #2563eb;
    }

    .btn-action.btn-complete {
      background: #10b981;
      color: white;
    }

    .btn-action.btn-complete:hover {
      background: #059669;
    }

    .btn-action.btn-view {
      background: #f3f4f6;
      color: #374151;
    }

    .btn-action.btn-view:hover {
      background: #e5e7eb;
    }

    @media (max-width: 768px) {
      .queue-stats {
        grid-template-columns: 1fr;
      }

      .queue-item {
        grid-template-columns: 48px 1fr;
        gap: 1rem;
      }

      .visit-details,
      .visit-status,
      .visit-actions {
        grid-column: 2;
      }
    }
  `]
})
export class OpdQueueComponent implements OnInit, OnDestroy {
  private visitService = inject(VisitService);
  private router = inject(Router);
  private toastService = inject(ToastService);
  private destroy$ = new Subject<void>();

  queue: Visit[] = [];
  departmentId = 1; // Default department - would come from user context

  get inConsultationCount(): number {
    return this.queue.filter(v => v.status === VisitStatus.IN_CONSULTATION).length;
  }

  get completedCount(): number {
    return this.queue.filter(v => v.status === VisitStatus.COMPLETED).length;
  }

  ngOnInit(): void {
    this.loadQueue();
    
    // Auto-refresh every 30 seconds
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.loadQueue());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadQueue(): void {
    this.visitService.getDepartmentQueue(this.departmentId)
      .subscribe({
        next: (visits) => {
          this.queue = visits.sort((a, b) => {
            // Sort by priority (descending) then by token number
            if (b.priority !== a.priority) {
              return b.priority - a.priority;
            }
            return (a.tokenNumber || 0) - (b.tokenNumber || 0);
          });
        },
        error: () => {
          this.toastService.error('Failed to load queue', 'Error');
        }
      });
  }

  refreshQueue(): void {
    this.loadQueue();
    this.toastService.success('Queue refreshed', 'Success');
  }

  callPatient(visit: Visit): void {
    this.visitService.updateVisitStatus(visit.uuid, VisitStatus.IN_CONSULTATION)
      .subscribe({
        next: () => {
          this.toastService.success(`Calling patient: ${visit.patientName}`, 'Success');
          this.loadQueue();
        },
        error: (error) => {
          this.toastService.error('Failed to call patient', 'Error');
        }
      });
  }

  completeVisit(visit: Visit): void {
    this.visitService.updateVisitStatus(visit.uuid, VisitStatus.COMPLETED)
      .subscribe({
        next: () => {
          this.toastService.success('Visit completed', 'Success');
          this.loadQueue();
        },
        error: (error) => {
          this.toastService.error('Failed to complete visit', 'Error');
        }
      });
  }

  viewPatient(visit: Visit): void {
    this.router.navigate(['/patients', visit.patientUuid]);
  }

  getStatusClass(status: VisitStatus): string {
    switch (status) {
      case VisitStatus.IN_QUEUE:
        return 'in-queue';
      case VisitStatus.IN_CONSULTATION:
        return 'in-consultation';
      case VisitStatus.COMPLETED:
        return 'completed';
      default:
        return '';
    }
  }

  formatStatus(status: VisitStatus): string {
    return status.replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, c => c.toUpperCase());
  }
}
