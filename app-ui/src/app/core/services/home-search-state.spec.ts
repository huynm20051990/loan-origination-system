import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { HomeSearchStateService } from './home-search-state';
import { HomeService } from './home';
import { Home } from '../models/home';

/** Minimal stub that satisfies the {@link Home} interface. */
function makeHome(id: string): Home {
  return {
    id,
    price: 300_000,
    beds: 3,
    baths: 2,
    sqft: 1_500,
    imageUrl: '',
    status: 'AVAILABLE',
    address: { street: '1 Main St', city: 'Austin', stateCode: 'TX', zipCode: '78701', country: 'US' },
    description: 'Test home',
  };
}

/**
 * Unit tests for {@link HomeSearchStateService} covering the US3 Reset feature.
 *
 * <p>Verifies that {@link HomeSearchStateService#reset} delegates to
 * {@link HomeService#getHomes} and updates the {@code homes} signal with the
 * full listing set returned by the HTTP layer.
 *
 * <p>These tests are in the RED phase (T038) until the implementation is complete in T039.
 */
describe('HomeSearchStateService — reset()', () => {
  let service: HomeSearchStateService;
  let getHomesMock: ReturnType<typeof vi.fn>;

  const fullListings: Home[] = [makeHome('a1'), makeHome('a2'), makeHome('a3')];

  beforeEach(() => {
    getHomesMock = vi.fn().mockReturnValue(of(fullListings));

    TestBed.configureTestingModule({
      providers: [
        HomeSearchStateService,
        { provide: HomeService, useValue: { getHomes: getHomesMock } },
      ],
    });

    service = TestBed.inject(HomeSearchStateService);
  });

  it('should call HomeService.getHomes() when reset() is invoked', () => {
    service.reset();

    expect(getHomesMock).toHaveBeenCalledTimes(1);
  });

  it('should update homes signal with the full listing set returned by getHomes()', () => {
    // Pre-condition: state holds some filtered subset.
    service.updateHomes([makeHome('filtered')]);
    expect(service.homes()).toHaveLength(1);

    service.reset();

    expect(service.homes()).toEqual(fullListings);
  });

  it('should set isLoading to false after getHomes() completes', () => {
    service.reset();

    expect(service.isLoading()).toBe(false);
  });

  it('should set isLoading to false even if getHomes() errors', () => {
    getHomesMock.mockReturnValue(throwError(() => new Error('network error')));

    service.reset();

    expect(service.isLoading()).toBe(false);
  });

  it('should not mutate homes signal when getHomes() errors', () => {
    const prior = [makeHome('prior')];
    service.updateHomes(prior);
    getHomesMock.mockReturnValue(throwError(() => new Error('fail')));

    service.reset();

    // On error homes stay at whatever they were; the important thing is no crash.
    expect(service.homes()).toEqual(prior);
  });
});
