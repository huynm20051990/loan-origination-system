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
import { ApplicationService } from '../../core/services/application';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

type SubmissionStatus = 'IDLE' | 'SUBMITTING' | 'SUCCESS' | 'ERROR';

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
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './loan-application.html',
  styleUrl: './loan-application.scss'
})

export class LoanApplicationComponent implements OnInit {
  @ViewChild('stepper') stepper!: MatStepper;

  private _formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private applicationService = inject(ApplicationService);

  Math = Math;
  isSubmitted = false;

  submissionStatus: SubmissionStatus = 'IDLE';
  referenceId: string = '';

  // Step 1: Personal
  personalInfo = this._formBuilder.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^[0-9\-+ ]{10,15}$/)]]
  });

  // Step 2: Identity (Moved SSN here, added DOB)
  identityInfo = this._formBuilder.group({
    dob: ['', Validators.required],
    ssn: ['', [Validators.required, Validators.pattern(/^\d{3}-\d{2}-\d{4}$/)]]
  });

  // Step 3: Loan Request
  loanDetails = this._formBuilder.group({
    loanAmount: ['', [Validators.required, Validators.min(1000)]],
    loanPurpose: ['', Validators.required]
  });

  ngOnInit() {
    const homeId = this.route.snapshot.paramMap.get('id');
    if (homeId) {
      this.loanDetails.get('loanPurpose')?.setValue('Home Purchase');
      this.loanDetails.get('loanPurpose')?.disable();
    }
  }

  handleSubmission(stepper: MatStepper) {
    if (this.personalInfo.valid && this.identityInfo.valid && this.loanDetails.valid) {

      this.submissionStatus = 'SUBMITTING';
      stepper.next(); // Move to the "Finish" step immediately to show loader

      const payload = {
        homeId: this.route.snapshot.paramMap.get('id'),
        personal: this.personalInfo.value,
        identity: this.identityInfo.value,
        request: this.loanDetails.getRawValue()
      };

      this.applicationService.submitApplication(payload).subscribe({
        next: (response) => {
          this.referenceId = response.applicationNumber || response.id;
          this.submissionStatus = 'SUCCESS';
          this.isSubmitted = true;
        },
        error: (err) => {
          console.error('Submission failed:', err);
          this.submissionStatus = 'ERROR';
        }
      });
    }
  }

  retry(): void {
    this.submissionStatus = 'IDLE';
    this.stepper.previous(); // Takes user back to the Review step
  }

  goToCheckStatus() {
    this.router.navigate(['/check-status']);
  }
}
