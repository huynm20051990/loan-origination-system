import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfficerReview } from './officer-review';

describe('OfficerReview', () => {
  let component: OfficerReview;
  let fixture: ComponentFixture<OfficerReview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OfficerReview]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OfficerReview);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
