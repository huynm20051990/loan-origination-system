import { Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
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

  // Use a plain array instead of an Observable to prevent async pipe race conditions
  homes: Home[] = [];
  loading = false;

  constructor(
    private homeService: HomeService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

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
    this.loading = true;
    this.cdr.markForCheck();

    const request$ = query === 'all'
      ? this.homeService.getHomes()
      : this.homeService.searchHomes(query);

    request$.pipe(
      catchError(error => {
        console.error('Search failed', error);
        return of([]);
      }),
      finalize(() => {
        this.loading = false;
        // Forces Angular to recognize the loading state change and the new data
        this.cdr.detectChanges();
      })
    ).subscribe(data => {
      this.homes = data;
    });
  }

  applyForLoan(home: Home) {
    this.router.navigate(['/apply', home.id], {
      queryParams: { price: home.price }
    });
  }
}
