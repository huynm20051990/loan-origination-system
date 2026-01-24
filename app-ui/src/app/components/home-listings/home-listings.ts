import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router, RouterLink } from '@angular/router';


@Component({
  selector: 'app-home-listings',
  standalone: true,
  imports: [
      CommonModule,
      RouterLink,
      MatCardModule,
      MatButtonModule,
      MatIconModule,
      MatInputModule,
      MatFormFieldModule
    ],
  templateUrl: './home-listings.html',
  styleUrls: ['./home-listings.scss']
})
export class HomeListingsComponent {
  homes = [
    { id: 1, address: '123 Maple Ave, Springfield', price: 350000, beds: 3, baths: 2, imageUrl: 'assets/home.png' },
    { id: 2, address: '456 Oak Lane, Riverside', price: 525000, beds: 4, baths: 3, imageUrl: 'assets/home.png' },
    { id: 3, address: '789 Pine Rd, Hilltop', price: 275000, beds: 2, baths: 1, imageUrl: 'assets/home.png' },
    { id: 4, address: '101 Cedar Court, Lakeshore', price: 610000, beds: 4, baths: 4, imageUrl: 'assets/home.png' },
    { id: 5, address: '202 Birch Street, Midtown', price: 425000, beds: 3, baths: 2, imageUrl: 'assets/home.png' },
    { id: 6, address: '303 Willow Blvd, Suburbia', price: 890000, beds: 5, baths: 4, imageUrl: 'assets/home.png' },
    { id: 7, address: '404 Aspen Ridge, Mountain View', price: 720000, beds: 4, baths: 3, imageUrl: 'assets/home.png' },
    { id: 8, address: '505 Ocean Dr, Coastal Bay', price: 1200000, beds: 6, baths: 5, imageUrl: 'assets/home.png' },
    { id: 9, address: '606 Ivy Lane, Old Town', price: 315000, beds: 2, baths: 2, imageUrl: 'assets/home.png' }
  ];

  constructor(private router: Router) {}

  applyForLoan(home: any) {
    // Navigate to the application page, potentially passing the home ID or price
    this.router.navigate(['/apply'], { queryParams: { propertyId: home.id, price: home.price } });
  }
}
