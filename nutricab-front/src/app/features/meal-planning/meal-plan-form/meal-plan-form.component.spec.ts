import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MealPlanFormComponent } from './meal-plan-form.component';

describe('MealPlanFormComponent', () => {
  let component: MealPlanFormComponent;
  let fixture: ComponentFixture<MealPlanFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MealPlanFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MealPlanFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
