import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Patient, PatientRegistrationRequest, PatientUpdateRequest } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/patients`;

  registerPatient(request: PatientRegistrationRequest): Observable<Patient> {
    return this.http.post<Patient>(this.baseUrl, request);
  }

  getPatient(uuid: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.baseUrl}/${uuid}`);
  }

  getPatientByUhid(uhid: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.baseUrl}/uhid/${uhid}`);
  }

  searchPatients(query: string, page: number = 0, size: number = 20): Observable<PageableResponse<Patient>> {
    let params = `page=${page}&size=${size}`;
    if (query) {
      params += `&query=${encodeURIComponent(query)}`;
    }
    return this.http.get<PageableResponse<Patient>>(`${this.baseUrl}?${params}`);
  }

  getAllPatients(page: number = 0, size: number = 20): Observable<PageableResponse<Patient>> {
    return this.http.get<PageableResponse<Patient>>(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  updatePatient(uuid: string, request: PatientUpdateRequest): Observable<Patient> {
    return this.http.put<Patient>(`${this.baseUrl}/${uuid}`, request);
  }

  deactivatePatient(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${uuid}/deactivate`);
  }

  activatePatient(uuid: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${uuid}/activate`, {});
  }
}

export interface PageableResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
