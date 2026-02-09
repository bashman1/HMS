import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { VisitService } from '../../../core/services/visit.service';
import { ToastService } from '../../../core/services/toast.service';
import { Visit, VisitStatus } from '../../../core/models/visit.model';

@Component({
  selector: 'app-visit-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="visit-list-container">
      <div class="page-header">
        <div>
          <h1>Visits</h1>
          <p class="subtitle">View and manage patient visits</p>
        </div>
        <a routerLink="/visits/register" class="btn-primary">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
          </svg>
          New Visit
        </a>
      </div>

      <!-- Status Filters -->
      <div class="status-filters">
        <button 
          (click)="filterByStatus(null)"
          [class.active]="selectedStatus === null"
          class="filter-btn">
          All
        </button>
        <button 
          *ngFor="let status of activeStatuses"
          (click)="filterByStatus(status)"
          [class.active]="selectedStatus === status"
          class="filter-btn">
          {{ formatStatus(status) }}
        </button>
      </div>

      <!-- Visits Table -->
      <div class="table-container">
        <table class="visit-table">
          <thead>
            <tr>
              <th>Token</th>
              <th>Visit #</th>
              <th>Patient</th>
              <th>Type</th>
              <th>Department</th>
              <th>Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let visit of visits">
              <td>
                <span class="token-badge">{{ visit.tokenNumber }}</span>
              </td>
              <td>
                <span class="visit-number">{{ visit.visitNumber }}</span>
              </td>
              <td>
                <div class="patient-info">
                  <span class="patient-name">{{ visit.patientName }}</span>
                  <span class="patient-uhid">{{ visit.patientUhid }}</span>
                </div>
              </td>
              <td>{{ visit.visitType }}</td>
              <td>{{ visit.departmentName || 'General' }}</td>
              <td>{{ visit.visitDate | date:'medium' }}</td>
              <td>
                <span class="status-badge" [class]="getStatusClass(visit.status)">
                  {{ formatStatus(visit.status) }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <a [routerLink]="['/patients', visit.patientUuid]" class="btn-view">View Patient</a>
                  
                  <!-- Status Change Dropdown -->
                  <select 
                    class="status-select" 
                    [value]="visit.status"
                    (change)="changeStatus(visit, $event)"
                    [disabled]="visit.status === 'COMPLETED' || visit.status === 'CANCELLED'">
                    <option value="" disabled>Change Status</option>
                    <option *ngFor="let s of getAvailableStatuses(visit.status)" [value]="s">
                      {{ formatStatus(s) }}
                    </option>
                  </select>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div *ngIf="visits.length === 0 && !isLoading" class="empty-state">
          <svg class="w-16 h-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
          <p>No visits found</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .visit-list-container {
      padding: 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
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

    .btn-primary {
      display: flex;
      align-items: center;
      padding: 0.75rem 1.5rem;
      background: #3b82f6;
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 0.9375rem;
      font-weight: 500;
      cursor: pointer;
      text-decoration: none;
      transition: background 0.2s;
    }

    .btn-primary:hover {
      background: #2563eb;
    }

    .status-filters {
      display: flex;
      gap: 0.5rem;
      margin-bottom: 1.5rem;
      flex-wrap: wrap;
    }

    .filter-btn {
      padding: 0.5rem 1rem;
      background: white;
      border: 1px solid #e5e7eb;
      border-radius: 6px;
      font-size: 0.875rem;
      color: #374151;
      cursor: pointer;
      transition: all 0.2s;
    }

    .filter-btn:hover {
      background: #f9fafb;
    }

    .filter-btn.active {
      background: #3b82f6;
      color: white;
      border-color: #3b82f6;
    }

    .table-container {
      background: white;
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      overflow: hidden;
    }

    .visit-table {
      width: 100%;
      border-collapse: collapse;
    }

    .visit-table th {
      text-align: left;
      padding: 1rem;
      background: #f9fafb;
      font-size: 0.75rem;
      font-weight: 600;
      color: #6b7280;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      border-bottom: 1px solid #e5e7eb;
    }

    .visit-table td {
      padding: 1rem;
      border-bottom: 1px solid #f3f4f6;
    }

    .visit-table tr:hover {
      background: #f9fafb;
    }

    .token-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      background: #3b82f6;
      color: white;
      border-radius: 50%;
      font-weight: 700;
      font-size: 0.875rem;
    }

    .visit-number {
      font-family: monospace;
      font-size: 0.875rem;
      background: #f3f4f6;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
    }

    .patient-info {
      display: flex;
      flex-direction: column;
    }

    .patient-name {
      font-weight: 500;
      color: #1a1a1a;
    }

    .patient-uhid {
      font-size: 0.75rem;
      color: #9ca3af;
    }

    .status-badge {
      display: inline-block;
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

    .status-badge.cancelled {
      background: #fee2e2;
      color: #991b1b;
    }

    .status-badge.no-show {
      background: #f3f4f6;
      color: #6b7280;
    }

    .status-badge.referred {
      background: #fce7f3;
      color: #9d174d;
    }

    .action-buttons {
      display: flex;
      gap: 0.5rem;
      align-items: center;
    }

    .btn-view {
      padding: 0.375rem 0.75rem;
      background: #f3f4f6;
      color: #374151;
      border-radius: 6px;
      font-size: 0.875rem;
      text-decoration: none;
      transition: background 0.2s;
    }

    .btn-view:hover {
      background: #e5e7eb;
    }

    .status-select {
      padding: 0.375rem 0.75rem;
      border: 1px solid #d1d5db;
      border-radius: 6px;
      font-size: 0.75rem;
      background: white;
      cursor: pointer;
    }

    .status-select:disabled {
      background: #f3f4f6;
      cursor: not-allowed;
    }

    .empty-state {
      padding: 4rem;
      text-align: center;
      color: #9ca3af;
    }

    .empty-state p {
      margin: 1rem 0 0.5rem;
      font-size: 1.125rem;
      color: #6b7280;
    }
  `]
})
export class VisitListComponent implements OnInit {
  private visitService = inject(VisitService);
  private toastService = inject(ToastService);

  visits: Visit[] = [];
  isLoading = false;
  selectedStatus: VisitStatus | null = null;
  allStatuses = Object.values(VisitStatus);
  activeStatuses = [VisitStatus.REGISTERED, VisitStatus.IN_QUEUE, VisitStatus.IN_CONSULTATION];

  ngOnInit(): void {
    this.loadVisits();
  }

  loadVisits(): void {
    this.isLoading = true;
    
    if (this.selectedStatus) {
      this.visitService.getVisitsByStatus(this.selectedStatus).subscribe({
        next: (visits) => {
          this.visits = visits;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.toastService.error('Failed to load visits', 'Error');
        }
      });
    } else {
      this.visitService.getAllVisits().subscribe({
        next: (visits) => {
          this.visits = visits;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.toastService.error('Failed to load visits', 'Error');
        }
      });
    }
  }

  filterByStatus(status: VisitStatus | null): void {
    this.selectedStatus = this.selectedStatus === status ? null : status;
    this.loadVisits();
  }

  changeStatus(visit: Visit, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newStatus = select.value as VisitStatus;
    
    if (newStatus && newStatus !== visit.status) {
      this.visitService.updateVisitStatus(visit.uuid, newStatus).subscribe({
        next: (updatedVisit) => {
          const index = this.visits.findIndex(v => v.uuid === visit.uuid);
          if (index !== -1) {
            this.visits[index] = updatedVisit;
          }
          this.toastService.success(`Visit status changed to ${this.formatStatus(newStatus)}`, 'Success');
        },
        error: (error) => {
          this.toastService.error('Failed to update visit status', error.error?.message || 'Error');
        }
      });
    }
    // Reset select to current status
    select.value = visit.status;
  }

  getAvailableStatuses(currentStatus: VisitStatus): VisitStatus[] {
    const all = [VisitStatus.REGISTERED, VisitStatus.IN_QUEUE, VisitStatus.IN_CONSULTATION, VisitStatus.COMPLETED, VisitStatus.CANCELLED, VisitStatus.NO_SHOW, VisitStatus.REFERRED];
    return all.filter(s => s !== currentStatus);
  }

  getStatusClass(status: VisitStatus): string {
    return status.toLowerCase().replace(/_/g, '-');
  }

  formatStatus(status: VisitStatus | null): string {
    if (!status) return 'All';
    return status.replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, c => c.toUpperCase());
  }
}
