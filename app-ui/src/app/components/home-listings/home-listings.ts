import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize, catchError } from 'rxjs/operators';
import { HomeService } from '../../core/services/home';
import { HomeSearchStateService } from '../../core/services/home-search-state';
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

  constructor(
    private readonly homeService: HomeService,
    private readonly homeSearchState: HomeSearchStateService,
    private readonly router: Router
  ) {}

  /**
   * Returns the currently displayed homes from the shared state service.
   * Reads the signal so that Angular's reactivity system tracks this dependency
   * and re-renders the template whenever the signal value changes.
   */
  get homes(): Home[] {
    return this.homeSearchState.homes();
  }

  /**
   * Returns the current loading flag from the shared state service.
   */
  get loading(): boolean {
    return this.homeSearchState.isLoading();
  }

  ngOnInit(): void {
    this.loadAllHomes();
  }

  private loadAllHomes() {
    this.executeSearch('all');
  }

  onAiSearch() {
    const query = this.searchInput?.nativeElement?.value;
    if (query && query.trim() !== '') {
      this.executeSearch(query);
    } else {
      this.loadAllHomes();
    }
  }

  private executeSearch(query: string) {
    this.homeSearchState.setLoading(true);

    const request$ = query === 'all'
      ? this.homeService.getHomes()
      : this.homeService.searchHomes(query);

    request$.pipe(
      catchError(error => {
        console.error('Search failed', error);
        return of([]);
      }),
      finalize(() => {
        this.homeSearchState.setLoading(false);
      })
    ).subscribe(data => {
      this.homeSearchState.updateHomes(data);
    });
  }

  applyForLoan(home: Home) {
    this.router.navigate(['/apply', home.id], {
      queryParams: { price: home.price }
    });
  }
}
