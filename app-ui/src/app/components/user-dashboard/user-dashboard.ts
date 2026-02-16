import { Component, inject, OnInit } from '@angular/core'; // Added OnInit
import { Router, RouterLink } from '@angular/router'; // Added Router
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { Application } from '../../core/models/application';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatChipsModule,
    MatListModule,
    RouterLink
  ],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.scss'
})
export class UserDashboardComponent implements OnInit {
  private router = inject(Router);

  applications: Application[] = [];
  userName: string = 'User';

  constructor() {
    const navigation = this.router.getCurrentNavigation();
    // FIX: Proper type casting for state
    const state = navigation?.extras.state as { applications: Application[] } | undefined;

    if (state?.applications) {
      this.applications = state.applications;
      this.userName = this.applications[0]?.fullName || 'User';
    }
  }

  ngOnInit(): void {
    if (this.applications.length === 0) {
      this.router.navigate(['/check-status']);
    }
  }

  getProgress(status: string): number {
    const s = status ? status.toUpperCase() : '';
    switch (s) {
      case 'SUBMITTED': return 25;
      case 'IN_REVIEW': return 65;
      case 'APPROVED': return 100;
      case 'REJECTED': return 100;
      default: return 10;
    }
  }
}
