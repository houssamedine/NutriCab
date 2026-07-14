import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import { CreatePatientRequest, Patient } from '../models/patient.model';
import { Page } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class PatientsService {

  private apiUrl="http://localhost:8182/api/patients";

  constructor(private http:HttpClient){ }

  getAllPatients():Observable<Patient[]>{
    return this.http.get<Page<Patient>>(this.apiUrl).pipe(
      map(response => response.content)
    );
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

  searchPatients(keyword:string):Observable<Patient[]>{
    return this.http.get<Page<Patient>>(`${this.apiUrl}/search?keyword=${encodeURIComponent(keyword)}`).pipe(
      map(response => response.content)
    );
  }
}
