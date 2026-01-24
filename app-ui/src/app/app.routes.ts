import { Routes } from '@angular/router';
import { HomeListingsComponent } from './components/home-listings/home-listings';
import { LoanApplicationComponent } from './components/loan-application/loan-application';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';

export const routes: Routes = [
  { path: '', redirectTo: 'listings', pathMatch: 'full' }, // Default to listings
  { path: 'listings', component: HomeListingsComponent },
  { path: 'apply', component: LoanApplicationComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent }
];
