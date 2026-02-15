import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

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
    MatFormFieldModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './home-listings.html',
  styleUrls: ['./home-listings.scss']
})
export class HomeListingsComponent implements OnInit {
  @ViewChild('searchInput') searchInput!: ElementRef;
  homes$: Observable<Home[]> | undefined;

  constructor(
    private homeService: HomeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Assign the observable from the service
    this.homes$ = this.homeService.getHomes();
  }

  onAiSearch() {
    const query = this.searchInput.nativeElement.value;
    if (query && query.trim() !== '') {
      // We assume your homeService has a searchHomes(query) method
      this.homes$ = this.homeService.searchHomes(query);
    } else {
      // If empty, revert to showing all homes
      this.homes$ = this.homeService.getHomes();
    }
  }

  applyForLoan(home: Home) {
    // Navigates using the real ID from the backend
    this.router.navigate(['/apply', home.id], {
      queryParams: { price: home.price }
    });
  }
}
