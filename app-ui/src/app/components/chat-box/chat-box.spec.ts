import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { Subject } from 'rxjs';
import { vi } from 'vitest';
import { ChatBoxComponent } from './chat-box';
import { ChatService, ChatSseEvent } from '../../core/services/chat';
import { HomeSearchStateService } from '../../core/services/home-search-state';
import { HomeListingsComponent } from '../home-listings/home-listings';
import { HomeService } from '../../core/services/home';
import { Home } from '../../core/models/home';
import { of } from 'rxjs';

/**
 * Unit and integration tests for {@link ChatBoxComponent} covering the US2 empty-results path.
 *
 * <p>Test suite 1 — verifies that when the chat-service emits a {@code listings} SSE event
 * whose data payload is {@code []}, {@link HomeSearchStateService#updateHomes} is called with
 * an empty array.
 *
 * <p>Test suite 2 — verifies that when {@link HomeSearchStateService#homes} signal holds an
 * empty array, the listings panel renders the {@code @empty} block ("No homes found").
 */
describe('ChatBoxComponent — US2 empty results', () => {
  let fixture: ComponentFixture<ChatBoxComponent>;
  let component: ChatBoxComponent;
  let streamSubject: Subject<ChatSseEvent>;
  let updateHomesMock: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    streamSubject = new Subject<ChatSseEvent>();
    updateHomesMock = vi.fn();

    const mockChatService = {
      stream: vi.fn().mockReturnValue(streamSubject.asObservable()),
    };

    const mockHomeSearchState = {
      homes: signal<Home[]>([]),
      isLoading: signal<boolean>(false),
      updateHomes: updateHomesMock,
      setLoading: vi.fn(),
      reset: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ChatBoxComponent],
      providers: [
        { provide: ChatService, useValue: mockChatService },
        { provide: HomeSearchStateService, useValue: mockHomeSearchState },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ChatBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    streamSubject.complete();
  });

  it('should call updateHomes([]) when listings SSE event data is "[]"', () => {
    // Arrange: prime the input and trigger a submit.
    component.queryInput = '40-bed mansion on the moon';
    component.onSubmit();

    // Act: server emits a listings event carrying an empty JSON array.
    streamSubject.next({ type: 'listings', data: '[]' });

    // Assert: state service receives an empty array.
    expect(updateHomesMock).toHaveBeenCalledWith([]);
  });

  it('should remain isLoading=false after done when listings were empty', () => {
    component.queryInput = '40-bed mansion on the moon';
    component.onSubmit();

    streamSubject.next({ type: 'listings', data: '[]' });
    streamSubject.next({ type: 'done', data: '' });

    expect(component.isLoading()).toBe(false);
    expect(updateHomesMock).toHaveBeenCalledWith([]);
  });
});

/**
 * Integration test verifying the {@code @empty} block in {@link HomeListingsComponent}.
 *
 * <p>When {@link HomeSearchStateService#homes} signal holds {@code []}, the listings grid
 * must render the "No homes found" empty-state message rather than any home cards.
 */
describe('HomeListingsComponent — @empty block renders when homes is []', () => {
  let fixture: ComponentFixture<HomeListingsComponent>;
  let mockHomeSearchState: {
    homes: ReturnType<typeof signal<Home[]>>;
    isLoading: ReturnType<typeof signal<boolean>>;
    updateHomes: ReturnType<typeof vi.fn>;
    setLoading: ReturnType<typeof vi.fn>;
    reset: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    const mockHomeService = {
      getHomes: vi.fn().mockReturnValue(of([])),
      searchHomes: vi.fn().mockReturnValue(of([])),
    };

    mockHomeSearchState = {
      homes: signal<Home[]>([]),
      isLoading: signal<boolean>(false),
      updateHomes: vi.fn(),
      setLoading: vi.fn(),
      reset: vi.fn(),
    };

    // ChatBoxComponent (child) also needs ChatService.
    const mockChatService = {
      stream: vi.fn().mockReturnValue(of()),
    };

    await TestBed.configureTestingModule({
      imports: [HomeListingsComponent],
      providers: [
        provideRouter([]),
        { provide: HomeService, useValue: mockHomeService },
        { provide: HomeSearchStateService, useValue: mockHomeSearchState },
        { provide: ChatService, useValue: mockChatService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeListingsComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should render the @empty block when homes signal is []', () => {
    const compiled: HTMLElement = fixture.nativeElement;
    // The @empty block contains an h3 with "No homes found".
    const emptyHeading = compiled.querySelector('.no-results-message h3');
    expect(emptyHeading).not.toBeNull();
    expect(emptyHeading?.textContent?.trim()).toBe('No homes found');
  });

  it('should not render any home-card elements when homes signal is []', () => {
    const compiled: HTMLElement = fixture.nativeElement;
    const cards = compiled.querySelectorAll('mat-card.home-card');
    expect(cards.length).toBe(0);
  });
});
