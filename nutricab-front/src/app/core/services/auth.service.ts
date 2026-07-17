import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  inject,
  Injectable,
  PLATFORM_ID
} from '@angular/core';
import { finalize, Observable, shareReplay, tap } from 'rxjs';

import {
  AuthResponse,
  LoginRequest
} from '../models/auth.model';

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  private readonly apiUrl = `${this.apiBaseUrl}/auth`;
  private refreshRequest?: Observable<AuthResponse>;

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/login`,
      request,
      { withCredentials: true }
    )
      .pipe(
        tap((res: AuthResponse) => {
          this.saveUserSession(res);
        })
      );
  }

  refresh(): Observable<AuthResponse> {
    if (!this.refreshRequest) {
      this.refreshRequest = this.http.post<AuthResponse>(
        `${this.apiUrl}/refresh`,
        {},
        { withCredentials: true }
      ).pipe(
        tap((res: AuthResponse) => {
          this.saveUserSession(res);
        }),
        finalize(() => {
          this.refreshRequest = undefined;
        }),
        shareReplay({ bufferSize: 1, refCount: false })
      );
    }

    return this.refreshRequest;
  }

  logout(): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/logout`,
      {},
      { withCredentials: true }
    )
      .pipe(
        tap(() => {
          this.clearUserSession();
        })
      );
  }

  getRole(): string | null {
    return this.getSessionItem('role');
  }

  getFullName(): string | null {
    return this.getSessionItem('fullName');
  }

  isAuthenticated(): boolean {
    return this.getSessionItem('authenticated') === 'true';
  }

  private saveUserSession(res: AuthResponse): void {
    if (!this.isBrowser()) {
      return;
    }

    sessionStorage.setItem('authenticated', 'true');
    sessionStorage.setItem('role', res.role);
    sessionStorage.setItem('fullName', res.fullName);
  }

  clearUserSession(): void {
    if (!this.isBrowser()) {
      return;
    }

    sessionStorage.removeItem('authenticated');
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('fullName');
  }

  private getSessionItem(key: string): string | null {
    if (!this.isBrowser()) {
      return null;
    }

    return sessionStorage.getItem(key);
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }
}
