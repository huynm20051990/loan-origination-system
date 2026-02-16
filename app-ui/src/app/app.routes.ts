import { Routes } from '@angular/router';
import { HomeListingsComponent } from './components/home-listings/home-listings';
import { CheckStatusComponent } from './components/check-status/check-status';
import { LoanApplicationComponent } from './components/loan-application/loan-application';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { UserDashboardComponent } from './components/user-dashboard/user-dashboard';
import { LoanOfficerDashboardComponent } from './components/loan-officer-dashboard/loan-officer-dashboard';
import { LoanDetailsComponent } from './components/loan-details/loan-details';
import { OfficerReviewComponent } from './components/officer-review/officer-review';

export const routes: Routes = [
  { path: '', redirectTo: 'listings', pathMatch: 'full' }, // Default to listings
  { path: 'listings', component: HomeListingsComponent },
  { path: 'check-status', component: CheckStatusComponent },
  { path: 'apply/:id', component: LoanApplicationComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'user-dashboard', component: UserDashboardComponent },
  { path: 'officer-dashboard', component: LoanOfficerDashboardComponent },
  { path: 'loan-details/:id', component: LoanDetailsComponent },
  { path: 'officer-review/:id', component: OfficerReviewComponent},
];
