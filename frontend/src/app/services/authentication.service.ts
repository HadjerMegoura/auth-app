import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  register(payload: RegisterPayload): Observable<AuthResponse> {
    payload = { ...payload, role: 'ADMIN' };
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, payload).pipe(
      tap(res => this.saveToken(res.token))
    );
  }

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap(res => this.saveToken(res.token))
    );
  }
  loginWithCookies(payload: LoginPayload): Observable<string> {
  return this.http.post(
    `${this.apiUrl}/loginCookies`,
    payload,
    {
      responseType: 'text',
      withCredentials: true
    }
  ) as Observable<string>;
}
  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
  }

  
  logoutCookies(): Observable<string> {
  return this.http.post(
    `${this.apiUrl}/logout`,
    {},
    {
      withCredentials: true,
      responseType: 'text'
    }
  );

  }

    refresh(): Observable<string> {
  return this.http.post(
    `${this.apiUrl}/refresh`,
    {},
    {
      withCredentials: true,
      responseType: 'text'
    }
  );

  }

  loginWithGithub(): void {
    window.location.href = `http://localhost:8080/oauth2/authorization/github`;
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`http://localhost:8080/user/me`, { withCredentials: true });
  }
}