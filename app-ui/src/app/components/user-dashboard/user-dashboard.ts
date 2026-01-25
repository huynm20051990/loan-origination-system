import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { RouterLink } from '@angular/router';
import { MatListModule } from '@angular/material/list';

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
export class UserDashboardComponent {
  // Mock data representing a user's loan applications
  applications = [
    {
      id: 'L-99283',
      type: 'Home Purchase',
      amount: 300000,
      status: 'In Review',
      progress: 65,
      date: '2026-01-20'
    }
  ];
}
