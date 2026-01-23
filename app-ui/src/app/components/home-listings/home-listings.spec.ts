import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeListings } from './home-listings';

describe('HomeListings', () => {
  let component: HomeListings;
  let fixture: ComponentFixture<HomeListings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeListings]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeListings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
