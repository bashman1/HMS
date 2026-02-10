import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '@env/environment';
import { Visit, CreateVisitRequest, UpdateVisitRequest, VisitStatus } from '../models/visit.model';
import { PageableResponse } from './patient.service';

@Injectable({
  providedIn: 'root'
})
export class VisitService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/visits`;

  createVisit(request: CreateVisitRequest): Observable<Visit> {
    return this.http.post<Visit>(this.baseUrl, request);
  }

  updateVisit(uuid: string, request: UpdateVisitRequest): Observable<Visit> {
    return this.http.put<Visit>(`${this.baseUrl}/${uuid}`, request);
  }

  getVisit(uuid: string): Observable<Visit> {
    return this.http.get<Visit>(`${this.baseUrl}/${uuid}`);
  }

  getVisitByNumber(visitNumber: string): Observable<Visit> {
    return this.http.get<Visit>(`${this.baseUrl}/number/${visitNumber}`);
  }

  getPatientVisits(patientUuid: string): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.baseUrl}/patient/${patientUuid}/visits`);
  }

  getPatientVisitsById(patientId: number, page: number = 0, size: number = 20): Observable<PageableResponse<Visit>> {
    return this.http.get<PageableResponse<Visit>>(`${this.baseUrl}/patient/${patientId}?page=${page}&size=${size}`);
  }

  getTodayVisitsByDepartment(departmentId: number, page: number = 0, size: number = 50): Observable<PageableResponse<Visit>> {
    return this.http.get<PageableResponse<Visit>>(`${this.baseUrl}/department/${departmentId}/today?page=${page}&size=${size}`);
  }

  getDepartmentQueue(departmentId: number): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.baseUrl}/department/${departmentId}/queue`);
  }

  getAllVisits(): Observable<Visit[]> {
    return this.http.get<{ content: Visit[] }>(this.baseUrl).pipe(
      map(response => response.content)
    );
  }

  getVisitsByStatus(status: VisitStatus): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.baseUrl}/status/${status}`);
  }

  getDoctorVisits(doctorId: number, status: VisitStatus = VisitStatus.IN_QUEUE): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.baseUrl}/doctor/${doctorId}/visits?status=${status}`);
  }

  updateVisitStatus(uuid: string, status: VisitStatus): Observable<Visit> {
    return this.http.patch<Visit>(`${this.baseUrl}/${uuid}/status?status=${status}`, {});
  }

  checkInPatient(uuid: string): Observable<Visit> {
    return this.http.post<Visit>(`${this.baseUrl}/${uuid}/check-in`, {});
  }

  saveConsultationNotes(
    uuid: string,
    diagnosis: string,
    treatmentPlan?: string,
    prescription?: string
  ): Observable<Visit> {
    let params = new HttpParams().set('diagnosis', diagnosis);
    if (treatmentPlan) params = params.set('treatmentPlan', treatmentPlan);
    if (prescription) params = params.set('prescription', prescription);
    return this.http.post<Visit>(`${this.baseUrl}/${uuid}/consultation`, {}, { params });
  }

  referPatient(
    uuid: string,
    referredDepartmentId: number,
    referredDepartmentName?: string,
    referralReason?: string
  ): Observable<Visit> {
    let params = new HttpParams().set('referredDepartmentId', referredDepartmentId.toString());
    if (referredDepartmentName) params = params.set('referredDepartmentName', referredDepartmentName);
    if (referralReason) params = params.set('referralReason', referralReason);
    return this.http.post<Visit>(`${this.baseUrl}/${uuid}/refer`, {}, { params });
  }
}
