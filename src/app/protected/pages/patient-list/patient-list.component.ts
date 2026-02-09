import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PatientService, PageableResponse } from '../../../core/services/patient.service';
import { ToastService } from '../../../core/services/toast.service';
import { Patient } from '../../../core/models/patient.model';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="patient-list-container">
      <div class="page-header">
        <div>
          <h1>Patients</h1>
          <p class="subtitle">Search and manage patients</p>
        </div>
        <a routerLink="/patients/register" class="btn-primary">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
          </svg>
          Register Patient
        </a>
      </div>

      <!-- Search Bar -->
      <div class="search-section">
        <div class="search-box">
          <svg class="search-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
          <input 
            type="text" 
            [(ngModel)]="searchQuery" 
            (keyup.enter)="search()"
            placeholder="Search by name, phone, or UHID..."
            class="search-input">
          <button (click)="search()" class="search-btn">Search</button>
        </div>
      </div>

      <!-- Patients Table -->
      <div class="table-container">
        <table class="patient-table">
          <thead>
            <tr>
              <th>UHID</th>
              <th>Name</th>
              <th>Phone</th>
              <th>Gender</th>
              <th>Age</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let patient of patients">
              <td>
                <span class="uhid-badge">{{ patient.uhid }}</span>
              </td>
              <td>
                <div class="patient-name">
                  <span class="name">{{ patient.fullName }}</span>
                  <span class="email">{{ patient.email || 'No email' }}</span>
                </div>
              </td>
              <td>{{ patient.phonePrimary }}</td>
              <td>{{ patient.gender | titlecase }}</td>
              <td>{{ patient.age }} yrs</td>
              <td>
                <span class="status-badge" [class.active]="patient.active" [class.inactive]="!patient.active">
                  {{ patient.active ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <a [routerLink]="['/patients', patient.uuid]" class="btn-view">View</a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div *ngIf="patients.length === 0 && !isLoading" class="empty-state">
          <svg class="w-16 h-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
          </svg>
          <p>No patients found</p>
          <span *ngIf="searchQuery">Try adjusting your search criteria</span>
          <span *ngIf="!searchQuery">Register a new patient to get started</span>
        </div>

        <!-- Pagination -->
        <div *ngIf="totalPages > 1" class="pagination">
          <button 
            [disabled]="currentPage === 0" 
            (click)="goToPage(currentPage - 1)"
            class="pagination-btn">
            Previous
          </button>
          <span class="pagination-info">
            Page {{ currentPage + 1 }} of {{ totalPages }} ({{ totalElements }} patients)
          </span>
          <button 
            [disabled]="currentPage >= totalPages - 1" 
            (click)="goToPage(currentPage + 1)"
            class="pagination-btn">
            Next
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .patient-list-container {
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
      transition: background 0.2s;
    }

    .btn-primary:hover {
      background: #2563eb;
    }

    .search-section {
      margin-bottom: 1.5rem;
    }

    .search-box {
      display: flex;
      align-items: center;
      background: white;
      border: 1px solid #e5e7eb;
      border-radius: 10px;
      padding: 0.5rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .search-icon {
      width: 20px;
      height: 20px;
      margin-left: 12px;
      margin-right: 8px;
      color: #9ca3af;
    }

    .search-input {
      flex: 1;
      border: none;
      outline: none;
      font-size: 1rem;
      padding: 0.5rem;
    }

    .search-btn {
      padding: 0.625rem 1.25rem;
      background: #f3f4f6;
      border: none;
      border-radius: 6px;
      font-size: 0.875rem;
      color: #374151;
      cursor: pointer;
      transition: background 0.2s;
    }

    .search-btn:hover {
      background: #e5e7eb;
    }

    .table-container {
      background: white;
      border-radius: 12px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      overflow: hidden;
    }

    .patient-table {
      width: 100%;
      border-collapse: collapse;
    }

    .patient-table th {
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

    .patient-table td {
      padding: 1rem;
      border-bottom: 1px solid #f3f4f6;
    }

    .patient-table tr:hover {
      background: #f9fafb;
    }

    .uhid-badge {
      font-family: monospace;
      font-size: 0.875rem;
      background: #f3f4f6;
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
    }

    .patient-name {
      display: flex;
      flex-direction: column;
    }

    .patient-name .name {
      font-weight: 500;
      color: #1a1a1a;
    }

    .patient-name .email {
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

    .status-badge.active {
      background: #d1fae5;
      color: #065f46;
    }

    .status-badge.inactive {
      background: #fee2e2;
      color: #991b1b;
    }

    .action-buttons {
      display: flex;
      gap: 0.5rem;
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

    .empty-state span {
      font-size: 0.875rem;
    }

    .pagination {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 1rem;
      padding: 1rem;
      border-top: 1px solid #e5e7eb;
    }

    .pagination-btn {
      padding: 0.5rem 1rem;
      background: white;
      border: 1px solid #d1d5db;
      border-radius: 6px;
      font-size: 0.875rem;
      color: #374151;
      cursor: pointer;
      transition: all 0.2s;
    }

    .pagination-btn:hover:not(:disabled) {
      background: #f9fafb;
      border-color: #9ca3af;
    }

    .pagination-btn:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .pagination-info {
      font-size: 0.875rem;
      color: #6b7280;
    }
  `]
})
export class PatientListComponent implements OnInit {
  private patientService = inject(PatientService);
  private toastService = inject(ToastService);

  patients: Patient[] = [];
  searchQuery = '';
  isLoading = false;
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.isLoading = true;
    this.patientService.getAllPatients(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.patients = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.toastService.error('Failed to load patients', 'Error');
      }
    });
  }

  search(): void {
    this.currentPage = 0;
    if (this.searchQuery.trim()) {
      this.isLoading = true;
      this.patientService.searchPatients(this.searchQuery, this.currentPage, this.pageSize).subscribe({
        next: (response) => {
          this.patients = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.toastService.error('Search failed', 'Error');
        }
      });
    } else {
      this.loadPatients();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    if (this.searchQuery.trim()) {
      this.search();
    } else {
      this.loadPatients();
    }
  }
}
