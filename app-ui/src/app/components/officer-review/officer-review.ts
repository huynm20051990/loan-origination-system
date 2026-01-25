import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // 1. Add this for ngModel
import { MatFormFieldModule } from '@angular/material/form-field'; // 2. Add this for mat-label
import { MatInputModule } from '@angular/material/input'; // 3. Add this for matInput
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-officer-review',
  standalone: true,
  imports: [
      CommonModule,
      FormsModule,
      RouterLink,
      MatCardModule,
      MatButtonModule,
      MatIconModule,
      MatChipsModule,
      MatListModule,
      MatFormFieldModule,
      MatInputModule
    ],
  templateUrl: './officer-review.html',
  styleUrl: './officer-review.scss'
})
export class OfficerReviewComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  loanId: string | null = '';
  reviewNote: string = '';

  // Mocked Underwriting Data
  application = {
    id: 'L-7701',
    applicant: 'John Doe',
    creditScore: 742,
    riskGrade: 'A',
    income: 85000,
    requested: 250000,
    dti: '32%', // Debt-to-Income
    docs: ['ID_Front.pdf', 'Paystub_Jan.pdf', 'Tax_Return_2025.pdf']
  };

  ngOnInit() {
    this.loanId = this.route.snapshot.paramMap.get('id');
  }

  processDecision(status: 'Approved' | 'Rejected') {
    console.log(`Loan ${this.loanId} has been ${status}. Notes: ${this.reviewNote}`);
    // Here you would call your product-composite-service
    this.router.navigate(['/officer-dashboard']);
  }
}
