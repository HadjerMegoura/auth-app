import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Cookies will be sent automatically
    const cloned = req.clone({
      withCredentials: true
    });

    return next.handle(cloned).pipe(

      catchError((error: HttpErrorResponse) => {

        if (error.status === 403 && !req.url.includes('/auth/refresh')) {

          return this.authService.refresh().pipe(

            switchMap(() => {

              // Retry the original request
              return next.handle(cloned);

            })

          );

        }

        return throwError(() => error);

      })

    );
  }
}