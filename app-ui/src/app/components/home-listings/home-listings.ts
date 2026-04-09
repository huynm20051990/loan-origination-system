import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { HomeSearchStateService } from '../../core/services/home-search-state';
import { Home } from '../../core/models/home';
import { ChatBoxComponent } from '../chat-box/chat-box';

/**
 * Displays the home listings grid alongside the AI chat sidebar.
 *
 * <p>On initialisation the component delegates to {@link HomeSearchStateService#reset}
 * so the full listing set is loaded from the server. Subsequent filtering and
 * reset actions are driven entirely through the chat sidebar and the shared
 * state service — no inline search bar is rendered by this component.</p>
 */
@Component({
  selector: 'app-home-listings',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    ChatBoxComponent
  ],
  templateUrl: './home-listings.html',
  styleUrls: ['./home-listings.scss']
})
export class HomeListingsComponent implements OnInit {
  constructor(
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

  /**
   * Loads the full unfiltered listing set on component initialisation.
   */
  ngOnInit(): void {
    this.homeSearchState.reset();
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
