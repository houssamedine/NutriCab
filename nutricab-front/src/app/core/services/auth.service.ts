import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthResponse, LoginRequest} from '../models/auth.model';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8182/api/auth';

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request)
      .pipe(
        tap((res: AuthResponse) => {
          this.saveUserSession(res);
        })
      );
  }

  refresh(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {})
      .pipe(
        tap((res: AuthResponse) => {
          this.saveUserSession(res);
        })
      );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {})
      .pipe(
        tap(() => {
          this.clearUserSession();
        })
      );
  }

  getToken(): string | null {
    return null;
  }

  getRole(): string | null {
    return sessionStorage.getItem('role');
  }

  isAuthenticated(): boolean {
    return sessionStorage.getItem('authenticated') === 'true';
  }

  private saveUserSession(res: AuthResponse): void {
    sessionStorage.setItem('authenticated', 'true');
    sessionStorage.setItem('role', res.role);
    sessionStorage.setItem('fullName', res.fullName);
  }

  private clearUserSession(): void {
    sessionStorage.removeItem('authenticated');
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('fullName');
  }
}
