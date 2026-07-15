import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Consultation } from '../../core/models/consultation.model';
import { Page } from '../../core/models/user.model';
@Injectable({
  providedIn: 'root'
})
export class ConsultationsService {

  private apiUrl = "http://localhost:8182/api/consultations";

  constructor(private http: HttpClient) { }

  getAllConsultations(): Observable<Consultation[]> {
    return this.http.get<Page<Consultation>>(this.apiUrl).pipe(
      map(response => response.content)
    );
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
  ): Observable<Consultation[]> {
    return this.http
      .get<Page<Consultation>>(`${this.apiUrl}/patient/${patientId}`, {
        params: {
          page,
          size,
          sort
        }
      })
      .pipe(map(response => response.content));
  }

  getWeightHistoryByPatient(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(
      `${this.apiUrl}/patient/${patientId}/weight-history`
    );
  }

  searchConsultations(keyword: string): Observable<Consultation[]> {
      const params = new HttpParams().set('keyword', keyword.trim());
      return this.http.get<Page<Consultation>>(`${this.apiUrl}/search`, { params }).pipe(
        map(response => response.content)
      );
    }
}
