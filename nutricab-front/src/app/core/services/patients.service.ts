import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Patients} from '../interfaces/interface';
import * as http from 'node:http';

@Injectable({
  providedIn: 'root'
})
export class PatientsService {

  private apiUrl="http://localhost:8182/api/patients";

  constructor(private http:HttpClient){ }

  getAllPatients():Observable<Patients[]>{
    return this.http.get<Patients[]>(this.apiUrl);
  }

  getById(id:number):Observable<Patients>{
    return this.http.get<Patients>(`${this.apiUrl}/${id}`);
  }

  createPatient(patient:Partial<Patients>):Observable<Patients>{
    return  this.http.post<Patients>(this.apiUrl,patient);
  }

  updatePatient(id:number,patient:Partial<Patients>):Observable<Patients>{
    return  this.http.put<Patients>(`${this.apiUrl}/${id}`,patient);
  }

  deletePatient(id:number):Observable<void>{
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchPatients(keyword:string):Observable<Patients[]>{
    return this.http.get<Patients[]>(`${this.apiUrl}/search?keyword=${keyword}`);
  }
}
