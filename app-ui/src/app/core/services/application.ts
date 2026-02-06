import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private readonly API_URL = 'https://localhost:8443/api/v1/applications';

  constructor(private http: HttpClient) {}

  submitApplication(payload: any): Observable<any> {
    // The authInterceptor will automatically attach the Bearer token
    return this.http.post<any>(this.API_URL, payload);
  }
}
