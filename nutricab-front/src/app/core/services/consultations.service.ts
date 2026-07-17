import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Consultation } from '../../core/models/consultation.model';
import { Page } from '../../core/models/user.model';
import { API_BASE_URL } from '../config/api.config';
@Injectable({
  providedIn: 'root'
})
export class ConsultationsService {

  private apiBaseUrl = inject(API_BASE_URL);
  private apiUrl = `${this.apiBaseUrl}/consultations`;

  constructor(private http: HttpClient) { }

  getAllConsultations(page: number = 0, size: number = 10, sort: string = 'consultationDate,desc'): Observable<Page<Consultation>> {
    return this.http.get<Page<Consultation>>(this.apiUrl, {
      params: { page, size, sort }
    });
  }

  getConsultationById(id: number): Observable<Consultation> {
    return this.http.get<Consultation>(`${this.apiUrl}/${id}`);
  }

  updateConsultation(id: number, consultation: any): Observable<Consultation> {
    return this.http.put<Consultation>(`${this.apiUrl}/${id}`, consultation);
  }

  createConsultation(consultation: any): Observable<Consultation> {
    return this.http.post<Consultation>(this.apiUrl, consultation);
  }

  deleteConsultation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getRecentConsultations(): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(`${this.apiUrl}/recent`);
  }

  getConsultationsByPatient(
    patientId: number,
    page: number = 0,
    size: number = 20,
    sort: string = 'consultationDate,desc'
  ): Observable<Page<Consultation>> {
    return this.http
      .get<Page<Consultation>>(`${this.apiUrl}/patient/${patientId}`, {
        params: {
          page,
          size,
          sort
        }
      });
  }

  getWeightHistoryByPatient(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(
      `${this.apiUrl}/patient/${patientId}/weight-history`
    );
  }

  searchConsultations(keyword: string, page: number = 0, size: number = 10, sort: string = 'consultationDate,desc'): Observable<Page<Consultation>> {
      const params = new HttpParams()
        .set('keyword', keyword.trim())
        .set('page', page)
        .set('size', size)
        .set('sort', sort);
      return this.http.get<Page<Consultation>>(`${this.apiUrl}/search`, { params });
    }
}
