import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Appointment, CreateAppointmentRequest } from '../models/appointment.model';
import { Observable } from 'rxjs';
import { Page } from '../models/user.model';
import { API_BASE_URL } from '../config/api.config';


@Injectable({
  providedIn: 'root'
})
export class AppointmentsService {
  private apiBaseUrl = inject(API_BASE_URL);
  private apiUrl = `${this.apiBaseUrl}/appointments`;

  constructor(private http: HttpClient) { }

  getAllAppointments(page: number = 0, size: number = 10, sort: string = 'appointmentDate,desc'): Observable<Page<Appointment>> {
    return this.http.get<Page<Appointment>>(this.apiUrl, {
      params: { page, size, sort }
    });
  }

  deleteAppointment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAppointmentById(id: number): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.apiUrl}/${id}`);
  }

  searchAppointments(keyword: string, page: number = 0, size: number = 10, sort: string = 'appointmentDate,desc'): Observable<Page<Appointment>> {
    const params = new HttpParams()
      .set('keyword', keyword.trim())
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    return this.http.get<Page<Appointment>>(`${this.apiUrl}/search`, { params });
  }

  updateAppointment(id: number, appointment: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.apiUrl}/${id}`, appointment);
  }

  createAppointment(appointment: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(this.apiUrl, appointment);
  }

  getAppointmentsByPatientId(patientId: number, page: number = 0, size: number = 10, sort: string = 'appointmentDate,desc'): Observable<Page<Appointment>> {
    return this.http.get<Page<Appointment>>(`${this.apiUrl}/patient/${patientId}`, {
      params: { page, size, sort }
    });
  }

}
