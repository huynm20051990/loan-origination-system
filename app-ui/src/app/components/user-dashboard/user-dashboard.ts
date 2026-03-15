import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router'; // Removed RouterLink
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatListModule } from '@angular/material/list';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Application } from '../../core/models/application';
import { ApplicationService } from '../../core/services/application';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog';

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
    MatSnackBarModule,
    MatDialogModule
  ],
  providers: [MatSnackBar],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.scss'
})
export class UserDashboardComponent implements OnInit {
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private applicationService = inject(ApplicationService);

  applications: Application[] = [];
  userName: string = 'User';
  loadingAppId: string | null = null;

  constructor() {
    const navigation = this.router.getCurrentNavigation();
    // Proper type casting for state
    const state = navigation?.extras.state as { applications: Application[] } | undefined;

    if (state?.applications) {
      this.applications = state.applications;
      this.userName = this.applications[0]?.fullName || 'User';
    }
  }

  openFeatureNotAvailable() {
    this.snackBar.open('Under development. Contact Huy Nguyen (huynm20051990@gmail.com)', 'Close', {
      duration: 6000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
    });
  }

  ngOnInit(): void {
    if (this.applications.length === 0) {
      this.router.navigate(['/check-status']);
    }
  }

  getProgress(status: string): number {
    const s = status ? status.toUpperCase() : '';
    switch (s) {
      case 'SUBMITTED': return 0;
      case 'ASSESSING': return 45;
      case 'APPROVED': return 100;
      case 'REJECTED': return 100;
      default: return 10;
    }
  }

  cancelApplication(app: any) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      minWidth: '350px',
      maxWidth: '600px',
      data: { appNumber: app.applicationNumber }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.executeDelete(app.id);
      }
    });
  }

  startAssessmentApplication(app: any) {
    // Guard clause: prevent multiple clicks if already loading this specific app
    if (this.loadingAppId === app.id) return;

    this.loadingAppId = app.id;

    this.applicationService.startAssessmentApplication(app.id).subscribe({
      next: () => {
        this.snackBar.open(`Assessment triggered for ${app.applicationNumber}. AI is now evaluating.`, 'Close', {
          duration: 5000
        });

        app.status = 'ASSESSING';
      },
      error: (err: any) => {
        this.loadingAppId = null; // Reset on error so user can try again
        this.snackBar.open('Failed to start assessment. Please try again later.', 'Close', {
          duration: 5000
        });
        console.error('Assessment trigger error:', err);
      }
    });
  }

  private executeDelete(id: string) {
    this.applicationService.deleteApplication(id).subscribe({
      next: () => {
        this.applications = this.applications.filter(a => a.id !== id);
        this.snackBar.open('Application successfully cancelled.', 'Dismiss', { duration: 3000 });
      },
      error: (err: any) => {
        this.snackBar.open('System error: Could not cancel application.', 'Close');
      }
    });
  }
}
