import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  const returnUrl = route.queryParamMap.get('returnUrl') || '/patients';
  const authenticatedUrlTree = router.parseUrl(normalizeReturnUrl(returnUrl));

  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  if (authService.isAuthenticated()) {
    return authenticatedUrlTree;
  }

  return authService.refresh().pipe(
    map(() => authenticatedUrlTree),
    catchError(() => {
      authService.clearUserSession();
      return of(true);
    })
  );
};

function normalizeReturnUrl(returnUrl: string): string {
  if (!returnUrl.startsWith('/') || returnUrl.startsWith('//') || returnUrl.startsWith('/login')) {
    return '/patients';
  }

  return returnUrl;
}
