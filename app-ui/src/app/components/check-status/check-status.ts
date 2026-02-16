import { Component, inject } from '@angular/core'; // Added inject
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ApplicationService } from '../../core/services/application';
import { Application } from '../../core/models/application';

@Component({
  selector: 'app-check-status',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './check-status.html',
  styleUrls: ['./check-status.scss']
})
export class CheckStatusComponent {
  email: string = '';
  // FIX: Added these missing properties
  isLoading: boolean = false;
  errorMessage: string | null = null;

  // FIX: Inject the service
  private applicationService = inject(ApplicationService);
  private router = inject(Router);

  goToUserDashboard() {
    if (!this.email) return;

    this.isLoading = true;
    this.errorMessage = null;

    this.applicationService.getApplicationsByEmail(this.email).subscribe({
      next: (data: Application[]) => {
        this.isLoading = false;
        if (data && data.length > 0) {
          this.router.navigate(['/user-dashboard'], { state: { applications: data } });
        } else {
          this.errorMessage = 'No applications found for this email address.';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'An error occurred while fetching your status.';
        console.error('Fetch error:', err);
      }
    });
  }
}
