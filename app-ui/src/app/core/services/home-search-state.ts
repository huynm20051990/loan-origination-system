import { Injectable, signal } from '@angular/core';
import { Home } from '../models/home';
import { HomeService } from './home';

/**
 * State service that holds the currently displayed home listings and their loading status.
 *
 * <p>Acts as a shared reactive store (using Angular signals) between the
 * {@code HomeListingsComponent} and the {@code ChatBoxComponent}. The chat box
 * calls {@link updateHomes} after receiving a {@code listings} SSE event so that
 * the listings panel reactively reflects the filtered results without a page reload.
 */
@Injectable({
  providedIn: 'root',
})
export class HomeSearchStateService {
  /** Currently displayed homes. Initialized to an empty array; populated on first load. */
  readonly homes = signal<Home[]>([]);

  /** True while a search or reset request is in-flight. */
  readonly isLoading = signal<boolean>(false);

  constructor(private readonly homeService: HomeService) {}

  /**
   * Replaces the current home listing with the provided array.
   *
   * @param homes the new set of homes to display
   */
  updateHomes(homes: Home[]): void {
    this.homes.set(homes);
  }

  /**
   * Updates the loading indicator.
   *
   * @param v {@code true} to show a spinner; {@code false} to hide it
   */
  setLoading(v: boolean): void {
    this.isLoading.set(v);
  }

  /**
   * Resets the listings to the full unfiltered set by fetching all homes from
   * {@link HomeService}. Sets the loading flag while the request is in-flight.
   */
  reset(): void {
    this.setLoading(true);
    this.homeService.getHomes().subscribe({
      next: (result) => {
        this.updateHomes(result);
        this.setLoading(false);
      },
      error: () => {
        this.setLoading(false);
      },
    });
  }
}
