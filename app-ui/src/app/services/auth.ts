import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, delay, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private router = inject(Router);

  // BehaviorSubject stores the current value and emits it to new subscribers
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor() {}

  /**
   * Mock registration method
   * Simulates a server response and logs the user in automatically
   */
  registerUser(userData: any): Observable<any> {
    console.log('Mocking registration for:', userData.email);

    const mockResponse = {
      status: 'success',
      user: { email: userData.email, id: Date.now() },
      token: 'mock-jwt-token-xyz'
    };

    // Return mock data with a 1.5s delay to simulate network latency
    return of(mockResponse).pipe(
      delay(1500),
      tap(() => {
        // Automatically log the user in upon successful "registration"
        this.isLoggedInSubject.next(true);
        localStorage.setItem('auth_token', mockResponse.token);
      })
    );
  }

  login(role: 'user' | 'officer') {
    // In a real app, you'd save a token here
    this.isLoggedInSubject.next(true);
  }

  logout() {
    this.isLoggedInSubject.next(false);
    localStorage.removeItem('auth_token');
    // Navigates back to listings (Home) and hides the Logout button
    this.router.navigate(['/listings']);
  }

  // Helper to get the current state without subscribing
  get currentLoginStatus(): boolean {
    return this.isLoggedInSubject.value;
  }
}
