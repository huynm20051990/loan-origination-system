import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
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
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm = this.fb.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  constructor() {}

  onSubmit() {
    if (this.registerForm.valid) {
          const userData = this.registerForm.value;

          // 1. Call your backend service
          this.authService.registerUser(userData).subscribe({
            next: (response) => {
              // 2. Store the token/user info
              //this.authService.setSession(response.token);

              // 3. Update the UI state
              //this.authService.isLoggedIn$.set(true);

              // 4. Send them to the dashboard or application
              this.router.navigate(['/listings']);
            },
            error: (err) => {
              // Handle "Email already exists" or other errors
              //this.errorMessage = err.message;
            }
          });
        }
  }
}
