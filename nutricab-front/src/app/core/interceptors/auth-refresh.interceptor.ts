import { isPlatformBrowser } from '@angular/common';
import { HttpContextToken, HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const AUTH_REFRESH_RETRIED = new HttpContextToken<boolean>(() => false);

export const authRefreshInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // The SSR process does not own the browser's HttpOnly cookies. Authentication
  // recovery must therefore run after hydration, in the browser.
  if (!isPlatformBrowser(platformId)) {
    return next(request);
  }

  return next(request).pipe(
    catchError((error: unknown) => {
      const isAuthRequest = request.url.includes('/auth/login') ||
        request.url.includes('/auth/refresh') ||
        request.url.includes('/auth/logout');

      const canRefresh =
        error instanceof HttpErrorResponse &&
        error.status === 401 &&
        !isAuthRequest &&
        !request.context.get(AUTH_REFRESH_RETRIED);

      if (!canRefresh) {
        return throwError(() => error);
      }

      return authService.refresh().pipe(
        switchMap(() => next(request.clone({
          context: request.context.set(AUTH_REFRESH_RETRIED, true),
          withCredentials: true
        }))),
        catchError((refreshError: unknown) => {
          authService.clearUserSession();

          router.navigateByUrl(router.createUrlTree(['/login'], {
            queryParams: { returnUrl: router.url }
          }));

          return throwError(() => refreshError);
        })
      );
    })
  );
};
