import { Component, inject, ViewChild } from '@angular/core'; // Added inject
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatStepperModule, MatStepper } from '@angular/material/stepper';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
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
    MatIconModule
  ],
  templateUrl: './loan-application.html',
  styleUrl: './loan-application.scss'
})
export class LoanApplicationComponent {
  @ViewChild('stepper') stepper!: MatStepper;
  private _formBuilder = inject(FormBuilder);

  personalInfo = this._formBuilder.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  financialInfo = this._formBuilder.group({
    annualIncome: ['', Validators.required],
    employer: ['', Validators.required]
  });

  constructor() {}

  isCompleted = false;
  submitApplication() {
    if (this.personalInfo.valid && this.financialInfo.valid) {
      // Simulate a submission delay or API call
      this.isCompleted = true;
      console.log('Submitted successfully');
      setTimeout(() => {
        if (this.stepper) {
          this.stepper.next();
        }
      }, 100);
    }
  }
}
