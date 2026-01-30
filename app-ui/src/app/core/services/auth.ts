import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, tap, map, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenUrl = 'https://localhost:8443/oauth2/token';
  private cachedToken: string | null = null;

  constructor(private http: HttpClient) {}

  getAccessToken(): Observable<string> {
    if (this.cachedToken) return of(this.cachedToken);

    // Basic Auth header: 'writer:secret-writer' in base64
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa('writer:secret-writer'),
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    // Request body matching your -d flags
    const body = new HttpParams()
      .set('grant_type', 'client_credentials')
      .set('scope', 'product:read product:write');

    return this.http.post<any>(this.tokenUrl, body.toString(), { headers }).pipe(
      map(res => res.access_token),
      tap(token => this.cachedToken = token)
    );
  }
}
