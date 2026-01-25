import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-loan-details',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatChipsModule,
    RouterLink
  ],
  templateUrl: './loan-details.html',
  styleUrl: './loan-details.scss'
})
export class LoanDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  loanId: string | null = '';

  // Mock data - In the future, this will come from your Product Microservice
  loanData = {
    id: 'L-99283',
    status: 'In Review',
    progress: 65,
    amount: 250000,
    purpose: 'Home Purchase',
    dateSubmitted: 'Jan 20, 2026',
    checklist: [
      { name: 'ID Verification', status: 'Completed', icon: 'check_circle' },
      { name: 'Proof of Income', status: 'Pending', icon: 'hourglass_empty' },
      { name: 'Property Appraisal', status: 'Required', icon: 'error_outline' }
    ]
  };

  ngOnInit() {
    this.loanId = this.route.snapshot.paramMap.get('id');
  }
}
