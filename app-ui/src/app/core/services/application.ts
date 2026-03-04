import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from '../../core/models/application';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private readonly API_URL = 'https://easy-apply.pro/api/v1/applications';

  constructor(private http: HttpClient) {}

  submitApplication(payload: any): Observable<any> {
    // The authInterceptor will automatically attach the Bearer token
    return this.http.post<any>(this.API_URL, payload);
  }

  getApplicationsByEmail(email: string): Observable<Application[]> {
    const params = new HttpParams().set('email', email);
    return this.http.get<Application[]>(this.API_URL, { params });
  }

  deleteApplication(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
