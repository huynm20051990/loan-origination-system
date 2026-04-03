import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ChatService } from './chat';

describe('ChatService', () => {
  let service: ChatService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'https://localhost:8443/api/v1/chat';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ChatService]
    });
    service = TestBed.inject(ChatService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createSession()', () => {
    it('should POST to /sessions and return a ChatSession', (done) => {
      const mockSession = {
        sessionId: '550e8400-e29b-41d4-a716-446655440000',
        userId: 'user-1',
        createdAt: '2026-04-02T10:00:00Z'
      };

      service.createSession().subscribe(session => {
        expect(session.sessionId).toBe(mockSession.sessionId);
        done();
      });

      const req = httpMock.expectOne(`${BASE_URL}/sessions`);
      expect(req.request.method).toBe('POST');
      req.flush(mockSession);
    });
  });

  describe('getMessages()', () => {
    it('should GET messages for a session', (done) => {
      const sessionId = '550e8400-e29b-41d4-a716-446655440000';
      const mockMessages = [
        { messageId: 'msg-1', role: 'USER', content: 'Hello', timestamp: '2026-04-02T10:00:00Z' },
        { messageId: 'msg-2', role: 'ASSISTANT', content: 'Hi there!', timestamp: '2026-04-02T10:00:01Z' }
      ];

      service.getMessages(sessionId).subscribe(messages => {
        expect(messages.length).toBe(2);
        expect(messages[0].role).toBe('USER');
        done();
      });

      const req = httpMock.expectOne(`${BASE_URL}/sessions/${sessionId}/messages`);
      expect(req.request.method).toBe('GET');
      req.flush(mockMessages);
    });
  });

  describe('deleteSession()', () => {
    it('should DELETE a session', (done) => {
      const sessionId = '550e8400-e29b-41d4-a716-446655440000';

      service.deleteSession(sessionId).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${BASE_URL}/sessions/${sessionId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
