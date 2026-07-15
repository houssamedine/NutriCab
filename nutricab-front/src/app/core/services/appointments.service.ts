import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Appointment, CreateAppointmentRequest } from '../models/appointment.model';
import { map, Observable } from 'rxjs';
import { Page } from '../models/user.model';


@Injectable({
  providedIn: 'root'
})
export class AppointmentsService {
  private apiUrl = "http://localhost:8182/api/appointments";

  constructor(private http: HttpClient) { }

  getAllAppointments(): Observable<Appointment[]> {
    return this.http.get<Page<Appointment>>(this.apiUrl).pipe(
      map(response => response.content)
    );
  }

  deleteAppointment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAppointmentById(id: number): Observable<Appointment> {
    return this.http.get<Appointment>(`${this.apiUrl}/${id}`);
  }

  searchAppointments(keyword: string): Observable<Appointment[]> {
    const params = new HttpParams().set('keyword', keyword.trim());
    return this.http.get<Page<Appointment>>(`${this.apiUrl}/search`, { params }).pipe(
      map(response => response.content)
    );
  }

  updateAppointment(id: number, appointment: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.apiUrl}/${id}`, appointment);
  }

  createAppointment(appointment: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(this.apiUrl, appointment);
  }

  getAppointmentsByPatientId(patientId: number): Observable<Appointment[]> {
    return this.http.get<Page<Appointment>>(`${this.apiUrl}/patient/${patientId}`).pipe(
      map(response => response.content)
    );
  }

}
