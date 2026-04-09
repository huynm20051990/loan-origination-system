import { TestBed } from '@angular/core/testing';
import { Home } from '../models/home';
import { ChatService, ChatSseEvent } from './chat';

/**
 * Unit tests for ChatService.
 *
 * <p>Validates the SSE-to-Observable bridge by replacing the global {@code EventSource}
 * with a lightweight mock. Each test drives the mock to emit specific events and asserts
 * the resulting Observable emissions and side-effects (e.g., connection closed on
 * {@code done}).
 *
 * <p>These tests are intentionally written in the <em>red</em> phase — they will fail
 * until {@code ChatService} is implemented in T027.
 */

/** Minimal mock that captures the registered event listeners for manual dispatch. */
class MockEventSource {
  static instances: MockEventSource[] = [];

  readonly url: string;
  readonly withCredentials: boolean;
  private readonly listeners = new Map<string, EventListener[]>();
  closed = false;

  constructor(url: string, init?: EventSourceInit) {
    this.url = url;
    this.withCredentials = init?.withCredentials ?? false;
    MockEventSource.instances.push(this);
  }

  addEventListener(type: string, listener: EventListener): void {
    if (!this.listeners.has(type)) {
      this.listeners.set(type, []);
    }
    this.listeners.get(type)!.push(listener);
  }

  removeEventListener(type: string, listener: EventListener): void {
    const existing = this.listeners.get(type) ?? [];
    this.listeners.set(
      type,
      existing.filter((l) => l !== listener),
    );
  }

  /** Simulates the server pushing a named SSE event with a data payload. */
  emit(type: string, data: string): void {
    const event = new MessageEvent(type, { data });
    (this.listeners.get(type) ?? []).forEach((fn) => fn(event as unknown as Event));
  }

  close(): void {
    this.closed = true;
  }
}

describe('ChatService', () => {
  let service: ChatService;
  let originalEventSource: typeof EventSource;

  beforeEach(() => {
    MockEventSource.instances = [];

    // Replace the global EventSource with our mock before each test.
    originalEventSource = globalThis.EventSource;
    (globalThis as unknown as Record<string, unknown>)['EventSource'] =
      MockEventSource as unknown as typeof EventSource;

    TestBed.configureTestingModule({
      providers: [ChatService],
    });
    service = TestBed.inject(ChatService);
  });

  afterEach(() => {
    // Restore the original EventSource implementation.
    (globalThis as unknown as Record<string, unknown>)['EventSource'] = originalEventSource;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('listings event triggers Observable emission with parsed Home[]', async () => {
    const homes: Home[] = [
      {
        id: 'home-1',
        price: 450000,
        beds: 3,
        baths: 2,
        sqft: 1800,
        imageUrl: 'https://example.com/home1.jpg',
        status: 'AVAILABLE',
        address: { street: '123 Main St', city: 'Austin', stateCode: 'TX', zipCode: '78701', country: 'US' },
        description: 'Nice home',
      },
    ];

    const collected: ChatSseEvent[] = [];
    const subscription = service.stream('session-1', '3 beds under 500k').subscribe((event) => {
      collected.push(event);
    });

    const mock = MockEventSource.instances[0];
    expect(mock).toBeTruthy();

    // Server emits a listings event carrying a JSON array of homes.
    mock.emit('listings', JSON.stringify(homes));

    expect(collected).toHaveLength(1);
    expect(collected[0].type).toBe('listings');
    // The data should be the original JSON string for the consumer to parse.
    expect(JSON.parse(collected[0].data)).toEqual(homes);

    subscription.unsubscribe();
  });

  it('token events emit string chunks in order', async () => {
    const chunks: string[] = [];
    const subscription = service.stream('session-2', 'query').subscribe((event) => {
      if (event.type === 'token') {
        chunks.push(event.data);
      }
    });

    const mock = MockEventSource.instances[0];
    mock.emit('token', 'Hello');
    mock.emit('token', ' world');
    mock.emit('token', '!');

    expect(chunks).toEqual(['Hello', ' world', '!']);

    subscription.unsubscribe();
  });

  it('done event completes the Observable and closes the EventSource connection', async () => {
    let completed = false;
    const subscription = service.stream('session-3', 'query').subscribe({
      complete: () => {
        completed = true;
      },
    });

    const mock = MockEventSource.instances[0];
    expect(mock.closed).toBe(false);

    mock.emit('done', '');

    expect(completed).toBe(true);
    expect(mock.closed).toBe(true);

    subscription.unsubscribe();
  });

  it('error event completes the Observable and closes the EventSource connection', async () => {
    let completed = false;
    const subscription = service.stream('session-4', 'query').subscribe({
      complete: () => {
        completed = true;
      },
    });

    const mock = MockEventSource.instances[0];
    mock.emit('error', 'home-service unavailable');

    expect(completed).toBe(true);
    expect(mock.closed).toBe(true);

    subscription.unsubscribe();
  });

  it('unsubscribing before done closes the EventSource connection', () => {
    const subscription = service.stream('session-5', 'query').subscribe(() => {});

    const mock = MockEventSource.instances[0];
    expect(mock.closed).toBe(false);

    subscription.unsubscribe();

    expect(mock.closed).toBe(true);
  });
});
