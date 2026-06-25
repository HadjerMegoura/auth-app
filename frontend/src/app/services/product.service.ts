import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './authentication.service';


@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8080/products';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`, {
      //headers: this.authService.getAuthHeaders()
      withCredentials: true
    });
  }
}