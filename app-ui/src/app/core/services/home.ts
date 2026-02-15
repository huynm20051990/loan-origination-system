import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Home } from '../models/home'; // Import the model we made in Step 1

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  // Use the Gateway URL. It handles the routing to the microservice.
  private readonly API_URL = 'https://minikube.me/api/v1/homes';

  constructor(private http: HttpClient) {}

  /**
   * Fetches all homes from the backend via the Gateway
   */
  getHomes(): Observable<Home[]> {
    return this.http.get<Home[]>(this.API_URL);
  }

  /**
   * Fetches a single home by its UUID
   */
  getHomeById(id: string): Observable<Home> {
    return this.http.get<Home>(`${this.API_URL}/${id}`);
  }

  searchHomes(query: string): Observable<Home[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Home[]>(`${this.API_URL}/search`, { params });
  }
}
