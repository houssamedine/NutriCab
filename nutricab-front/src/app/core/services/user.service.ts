import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ChangePasswordRequest, CreateUserRequest, Page, UpdateUserRequest, User, UserRole } from '../models/user.model';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiBaseUrl = inject(API_BASE_URL);
  private apiUrl = `${this.apiBaseUrl}/users`;

  constructor(private http: HttpClient) { }

  getAllUsers(page: number = 0, size: number = 10, sort: string = 'id,asc'): Observable<Page<User>> {
    return this.http.get<Page<User>>(this.apiUrl, {
      params: { page, size, sort }
    });
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  createUser(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  updateUser(id: number, user: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleUserStatus(id: number): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/status`, {});
  }

  getUserByPatientId(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}/patients`);
  }

  getUsersByRole(
    role: UserRole,
    page: number = 0,
    size: number = 10,
    sort: string = 'id,asc'
  ): Observable<Page<User>> {
    return this.http.get<Page<User>>(`${this.apiUrl}/role/${role}`, {
      params: {
        page,
        size,
        sort
      }
    });
  }

  getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/email/${encodeURIComponent(email)}`);
  }

  changePassword(id: number, body: ChangePasswordRequest): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/password`, body);
  }

  getUsersByFilter(
    active: boolean,
    role?: UserRole,
    page: number = 0,
    size: number = 10,
    sort: string = 'id,asc'
  ): Observable<Page<User>> {
    const params: any = {
      active,
      page,
      size,
      sort
    };

    if (role) {
      params.role = role;
    }
    return this.http.get<Page<User>>(`${this.apiUrl}/filter`, { params });
  }
}
