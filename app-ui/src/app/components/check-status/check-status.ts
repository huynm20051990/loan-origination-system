import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Required for [(ngModel)]
import { MatCardModule } from '@angular/material/card'; // Required for mat-card
import { MatFormFieldModule } from '@angular/material/form-field'; // Required for mat-form-field
import { MatInputModule } from '@angular/material/input'; // Required for matInput
import { MatButtonModule } from '@angular/material/button'; // Required for mat-flat-button

@Component({
  selector: 'app-check-status',
  standalone: true, // Ensure this is true
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './check-status.html',
  styleUrls: ['./check-status.scss'] // Make sure this file exists!
})
export class CheckStatusComponent {
  email: string = '';

  constructor(private router: Router) {}

  goToDashboard() {
    this.router.navigate(['/user-dashboard']);
  }
}
