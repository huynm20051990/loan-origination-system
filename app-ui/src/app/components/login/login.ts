import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  hidePassword = true;
  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  constructor() {}

  onSubmit() {
    if (this.loginForm.valid) {
      const { email } = this.loginForm.value;

      if (email === 'user@test.com') {
        this.authService.login('user');
        console.log('Redirecting to User Dashboard...');
        this.router.navigate(['/user-dashboard']);
      }
      else if (email === 'officer@test.com') {
        this.authService.login('officer');
        console.log('Redirecting to Officer Dashboard...');
        this.router.navigate(['/officer-dashboard']);
      }
      else {
        alert('User not recognized. Use user@test.com or officer@test.com');
      }
    }
  }
}
