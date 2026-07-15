import { TestBed } from '@angular/core/testing';

import { MealplanningService } from './mealplanning.service';

describe('MealplanningService', () => {
  let service: MealplanningService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MealplanningService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
