import { Routes } from '@angular/router';
import { HomeListingsComponent } from './components/home-listings/home-listings';
import { LoanApplicationComponent } from './components/loan-application/loan-application';

export const routes: Routes = [
  { path: '', redirectTo: 'listings', pathMatch: 'full' }, // Default to listings
  { path: 'listings', component: HomeListingsComponent },
  { path: 'apply', component: LoanApplicationComponent },
];
