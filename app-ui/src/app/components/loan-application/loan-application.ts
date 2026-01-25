import { Component, inject, ViewChild, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';

// Material Imports
import { MatStepperModule, MatStepper } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-loan-application',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatStepperModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatIconModule
  ],
  templateUrl: './loan-application.html',
  styleUrl: './loan-application.scss'
})
export class LoanApplicationComponent implements OnInit {
  @ViewChild('stepper') stepper!: MatStepper;

  private _formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  // Reference for the template to use Math functions
  Math = Math;

  // Controls the locking of previous steps and hiding of the stepper header
  isSubmitted = false;

  // Personal Information Group
  personalInfo = this._formBuilder.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^[0-9\-+ ]{10,15}$/)]],
    ssn: ['', [Validators.required, Validators.pattern(/^\d{3}-\d{2}-\d{4}$/)]]
  });

  // Financial Information Group
  financialInfo = this._formBuilder.group({
    annualIncome: ['', [Validators.required, Validators.min(1)]],
    employer: ['', Validators.required]
  });

  // Loan Request Group
  loanDetails = this._formBuilder.group({
    loanAmount: ['', [Validators.required, Validators.min(1000)]],
    loanPurpose: ['', Validators.required]
  });

  ngOnInit() {
    // Check if the user came from a specific home listing
    const homeId = this.route.snapshot.paramMap.get('id');

    if (homeId) {
      // Set default value and disable selection if coming from a specific home
      this.loanDetails.get('loanPurpose')?.setValue('Home Purchase');
      this.loanDetails.get('loanPurpose')?.disable();
    }
  }

  /**
   * Final Submission Process
   * 1. Validates all forms
   * 2. Sets isSubmitted to true (locks [editable] property in HTML)
   * 3. Moves stepper to the final "Finish" step
   */
  handleSubmission(stepper: MatStepper) {
    if (this.personalInfo.valid && this.financialInfo.valid && this.loanDetails.valid) {

      // Construct final payload (including disabled fields like loanPurpose)
      const applicationData = {
        ...this.personalInfo.value,
        ...this.financialInfo.value,
        ...this.loanDetails.getRawValue() // getRawValue includes disabled fields
      };

      console.log('Submitting Loan Application:', applicationData);

      // Lock navigation and move to success screen
      this.isSubmitted = true;
      stepper.next();
    }
  }

  goToDashboard() {
    this.router.navigate(['/user-dashboard']);
  }
}
