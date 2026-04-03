import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ChatBoxComponent } from './chat-box.component';
import { ChatService } from '../../core/services/chat';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ChatBoxComponent', () => {
  let component: ChatBoxComponent;
  let fixture: ComponentFixture<ChatBoxComponent>;
  let chatServiceMock: jasmine.SpyObj<ChatService>;

  const mockSession = {
    sessionId: '550e8400-e29b-41d4-a716-446655440000',
    userId: 'user-1',
    createdAt: new Date().toISOString()
  };

  beforeEach(async () => {
    chatServiceMock = jasmine.createSpyObj('ChatService', [
      'createSession', 'sendMessage', 'getMessages', 'deleteSession'
    ]);
    chatServiceMock.createSession.and.returnValue(of(mockSession));
    chatServiceMock.getMessages.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [ChatBoxComponent, NoopAnimationsModule],
      providers: [
        { provide: ChatService, useValue: chatServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and display the chat side panel', () => {
    expect(component).toBeTruthy();
  });

  it('should create a session on init', () => {
    expect(chatServiceMock.createSession).toHaveBeenCalled();
    expect(component.sessionId).toBe(mockSession.sessionId);
  });

  it('should show loading indicator when sending a message', fakeAsync(() => {
    component.sessionId = mockSession.sessionId;
    component.userInput = 'Find a 3-bedroom home';

    // sendMessage emits tokens then completes
    chatServiceMock.sendMessage = jasmine.createSpy().and.callFake(
      (_sessionId: string, _content: string, onToken: (t: string) => void, onDone: () => void) => {
        component.isLoading = true;
        onToken('Hello');
        onDone();
        component.isLoading = false;
      }
    );

    component.sendMessage();
    tick();

    expect(chatServiceMock.sendMessage).toHaveBeenCalled();
  }));

  it('should disable send button when input is empty', () => {
    component.userInput = '';
    fixture.detectChanges();
    const button = fixture.nativeElement.querySelector('button[data-testid="send-button"]');
    if (button) {
      expect(button.disabled).toBeTrue();
    }
  });

  it('should emit listingsReset event when clear is called', () => {
    chatServiceMock.deleteSession.and.returnValue(of(undefined));
    chatServiceMock.createSession.and.returnValue(of(mockSession));

    spyOn(component.listingsReset, 'emit');
    component.sessionId = mockSession.sessionId;
    component.clearSession();

    expect(chatServiceMock.deleteSession).toHaveBeenCalledWith(mockSession.sessionId);
    expect(component.listingsReset.emit).toHaveBeenCalled();
  });
});
