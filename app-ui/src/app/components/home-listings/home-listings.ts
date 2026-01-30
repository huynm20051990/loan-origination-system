import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

// Import the service and model we created in previous steps
import { HomeService } from '../../core/services/home';
import { Home } from '../../core/models/home';

@Component({
  selector: 'app-home-listings',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule
  ],
  templateUrl: './home-listings.html',
  styleUrls: ['./home-listings.scss']
})
export class HomeListingsComponent implements OnInit {
  // Observable to hold the stream of home data
  homes$: Observable<Home[]> | undefined;

  constructor(
    private homeService: HomeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Assign the observable from the service
    this.homes$ = this.homeService.getHomes();
  }

  applyForLoan(home: Home) {
    // Navigates using the real ID from the backend
    this.router.navigate(['/apply', home.id], {
      queryParams: { price: home.price }
    });
  }
}
