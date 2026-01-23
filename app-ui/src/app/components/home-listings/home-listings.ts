import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-listings',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './home-listings.html',
  styleUrls: ['./home-listings.scss']
})
export class HomeListingsComponent {
  homes = [
    { id: 1, address: '123 Maple Ave, Springfield', price: 350000, beds: 3, baths: 2, imageUrl: 'assets/home1.jpg' },
    { id: 2, address: '456 Oak Lane, Riverside', price: 525000, beds: 4, baths: 3, imageUrl: 'assets/home2.jpg' },
    { id: 3, address: '789 Pine Rd, Hilltop', price: 275000, beds: 2, baths: 1, imageUrl: 'assets/home3.jpg' }
  ];

  constructor(private router: Router) {}

  applyForLoan(home: any) {
    // Navigate to the application page, potentially passing the home ID or price
    this.router.navigate(['/apply'], { queryParams: { propertyId: home.id, price: home.price } });
  }
}
