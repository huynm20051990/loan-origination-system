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
import { interval, Subscription, of } from 'rxjs';
import { switchMap, takeWhile, tap, delay } from 'rxjs/operators';

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

  private pollingSubscriptions: Map<string, Subscription> = new Map();

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

  ngOnDestroy() {
    this.pollingSubscriptions.forEach(sub => sub.unsubscribe());
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
    if (this.loadingAppId === app.id) return;
    this.loadingAppId = app.id;

    // SIMULATION: Mocking the 'POST' trigger with a 1-second network delay
    of({ success: true }).pipe(delay(1000)).subscribe({
      next: () => {
        this.snackBar.open(`Assessment triggered for ${app.applicationNumber}. AI is now evaluating.`, 'Close', {
          duration: 5000
        });

        app.status = 'ASSESSING';
        this.loadingAppId = null; // Clear loading state so UI updates
        this.pollApplicationStatus(app);
      },
      error: (err: any) => {
        this.loadingAppId = null;
        this.snackBar.open('Simulation Error: Failed to start.', 'Close');
      }
    });
  }

  pollApplicationStatus(app: any) {
    if (this.pollingSubscriptions.has(app.id)) return;

    const simulationDuration = 12000; // 12 seconds of "AI Thinking"
    const startTime = Date.now();

    const sub = interval(3000) // Poll every 3 seconds
      .pipe(
        switchMap(() => {
          const elapsed = Date.now() - startTime;

          let mockStatus = 'ASSESSING';

          // Only change the status if the "processing time" has passed
          if (elapsed >= simulationDuration) {
            // New Logic: Check loan amount for approval/rejection
            mockStatus = app.loanAmount < 500000 ? 'APPROVED' : 'REJECTED';
          }

          // Return the mock application state
          return of({ ...app, status: mockStatus });
        }),
        // Stop polling once the status flips to APPROVED or REJECTED
        takeWhile((updatedApp) => updatedApp.status === 'ASSESSING', true)
      )
      .subscribe({
        next: (updatedApp) => {
          app.status = updatedApp.status;

          if (app.status !== 'ASSESSING') {
            const message = app.status === 'APPROVED'
              ? `Success! Application ${app.applicationNumber} was approved.`
              : `Application ${app.applicationNumber} was rejected due to high loan amount.`;

            this.snackBar.open(message, 'Dismiss', { duration: 5000 });
            this.pollingSubscriptions.delete(app.id);
          }
        },
        error: (err) => {
          console.error('Simulation polling error', err);
          this.pollingSubscriptions.delete(app.id);
        }
      });

    this.pollingSubscriptions.set(app.id, sub);
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
