import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth';
import { switchMap, take } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // 1. Skip adding a token if we are calling the token endpoint itself
  // or if it's a call to a public assets folder
  if (req.url.includes('/oauth2/token') || req.url.includes('/assets/')) {
    return next(req);
  }

  // 2. Get the token from our AuthService and inject it into the request
  return authService.getAccessToken().pipe(
    // take(1) ensures the stream completes after getting the first token
    take(1),
    switchMap((token: string) => {
      // 3. Clone the original request because they are immutable
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          Accept: 'application/json'
        }
      });

      // 4. Pass the cloned request to the next handler in the chain
      return next(authReq);
    })
  );
};
