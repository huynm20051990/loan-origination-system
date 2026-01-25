import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';

export interface LoanApplication {
  id: string;
  applicant: string;
  amount: number;
  riskScore: number;
  status: 'New' | 'Reviewing' | 'Approved' | 'Rejected';
}

@Component({
  selector: 'app-loan-officer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatCardModule
  ],
  templateUrl: './loan-officer-dashboard.html',
  styleUrl: './loan-officer-dashboard.scss'
})
export class LoanOfficerDashboardComponent {
  private router = inject(Router);
  // Summary Stats
  stats = [
    { label: 'Pending Apps', value: 12, icon: 'pending_actions', color: '#3f51b5' },
    { label: 'Avg. Risk Score', value: 710, icon: 'speed', color: '#4caf50' },
    { label: 'Total Volume', value: '$240k', icon: 'payments', color: '#ff9800' }
  ];

  dataSource: LoanApplication[] = [
    { id: 'L-7701', applicant: 'Michael Scott', amount: 45000, riskScore: 780, status: 'New' },
    { id: 'L-7702', applicant: 'Dwight Schrute', amount: 15000, riskScore: 820, status: 'Reviewing' },
    { id: 'L-7703', applicant: 'Jim Halpert', amount: 8000, riskScore: 640, status: 'New' },
  ];

  displayedColumns: string[] = ['id', 'applicant', 'amount', 'riskScore', 'status', 'actions'];

  process(id: string, action: string) {
    if (action === 'view') {
      this.router.navigate(['/officer-review', id]);
    } else if (action === 'approve') {
      // Logic for quick approval
      console.log('Quick approving loan:', id);
    }
  }
}
