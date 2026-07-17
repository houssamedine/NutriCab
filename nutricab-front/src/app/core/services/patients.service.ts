import { inject, Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import { CreatePatientRequest, Patient } from '../models/patient.model';
import { Page } from '../models/user.model';
import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class PatientsService {

  private apiBaseUrl = inject(API_BASE_URL);
  private apiUrl=`${this.apiBaseUrl}/patients`;

  constructor(private http:HttpClient){ }

  getAllPatients(page: number = 0, size: number = 10, sort: string = 'fullName,asc'):Observable<Page<Patient>>{
    return this.http.get<Page<Patient>>(this.apiUrl, {
      params: { page, size, sort }
    });
  }

  getById(id:number):Observable<Patient>{
    return this.http.get<Patient>(`${this.apiUrl}/${id}`);
  }

  createPatient(patient:CreatePatientRequest):Observable<Patient>{
    return  this.http.post<Patient>(this.apiUrl,patient);
  }

  updatePatient(id:number,patient:Partial<Patient>):Observable<Patient>{
    return  this.http.put<Patient>(`${this.apiUrl}/${id}`,patient);
  }

  deletePatient(id:number):Observable<void>{
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchPatients(keyword:string, page: number = 0, size: number = 10, sort: string = 'fullName,asc'):Observable<Page<Patient>>{
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    return this.http.get<Page<Patient>>(`${this.apiUrl}/search`, { params });
  }
}
