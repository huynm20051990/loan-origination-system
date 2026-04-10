import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { HomeSearchStateService } from '../../core/services/home-search-state';
import { Home } from '../../core/models/home';
import { ChatBoxComponent } from '../chat-box/chat-box';

/**
 * Displays the home listings grid with an AI search bar and the fixed chat sidebar.
 *
 * <p>On initialisation the component delegates to {@link HomeSearchStateService#reset}
 * so the full listing set is loaded from the server. The search bar triggers
 * {@link HomeSearchStateService#search} for keyword-based filtering. The AI chat
 * sidebar is rendered as a fixed side-panel overlay via {@link ChatBoxComponent}.
 */
@Component({
  selector: 'app-home-listings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    ChatBoxComponent
  ],
  templateUrl: './home-listings.html',
  styleUrls: ['./home-listings.scss']
})
export class HomeListingsComponent implements OnInit {
  /** Current value of the search input field. */
  searchQuery = '';

  constructor(
    private readonly homeSearchState: HomeSearchStateService,
    private readonly router: Router
  ) {}

  /** Returns the currently displayed homes from the shared state service. */
  get homes(): Home[] {
    return this.homeSearchState.homes();
  }

  /** Returns the current loading flag from the shared state service. */
  get loading(): boolean {
    return this.homeSearchState.isLoading();
  }

  /** Loads the full unfiltered listing set on component initialisation. */
  ngOnInit(): void {
    this.homeSearchState.reset();
  }

  /** Triggers a search if the query is non-empty, otherwise resets to all listings. */
  onSearch(): void {
    const query = this.searchQuery.trim();
    if (query) {
      this.homeSearchState.search(query);
    } else {
      this.homeSearchState.reset();
    }
  }

  /**
   * Navigates to the loan application page for the selected home.
   *
   * @param home the home the user wants to apply for
   */
  applyForLoan(home: Home): void {
    this.router.navigate(['/apply', home.id], {
      queryParams: { price: home.price }
    });
  }
}
