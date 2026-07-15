import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateMealPlanRequest, MealPlan } from '../models/meal-plan.model';
import { map, Observable } from 'rxjs';
import { Page } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class MealplanningService {

  apiUrl = 'http://localhost:8182/api/meal-plan';

  constructor(private http: HttpClient) { }

  getAllMealPlans(): Observable<Page<MealPlan>> {
    return this.http.get<Page<MealPlan>>(this.apiUrl);
  }

  getMealPlanById(mealPlanId: number): Observable<MealPlan> {
    return this.http.get<MealPlan>(`${this.apiUrl}/${mealPlanId}`);
  }

  updateMealPlan(mealPlanId: number, mealPlan: CreateMealPlanRequest): Observable<MealPlan> {
    return this.http.put<MealPlan>(`${this.apiUrl}/${mealPlanId}`, mealPlan);
  }

  createMealPlan(mealPlan: CreateMealPlanRequest): Observable<MealPlan> {
    return this.http.post<MealPlan>(this.apiUrl, mealPlan);
  }

  deleteMealPlan(mealPlanId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${mealPlanId}`);
  }

  searchMealPlans(keyword: string): Observable<MealPlan[]> {
    const params = new HttpParams().set('keyword', keyword.trim());
    return this.http.get<Page<MealPlan>>(`${this.apiUrl}/search`, { params }).pipe(
      map(response => response.content)
    );
  }

  getMealPlansByPatientId(patientId: number): Observable<MealPlan[]> {
    return this.http.get<Page<MealPlan>>(`${this.apiUrl}/patient/${patientId}`).pipe(
      map(response => response.content)
    );
  }

  getMealPlansByCaloriesRange(minCalories: number, maxCalories: number): Observable<MealPlan[]> {
    const params = new HttpParams()
      .set('minCalories', minCalories.toString())
      .set('maxCalories', maxCalories.toString());
    return this.http.get<Page<MealPlan>>(`${this.apiUrl}/calories`, { params }).pipe(
      map(response => response.content)
    );
  }

  getActiveMealPlanByPatientId(patientId: number): Observable<MealPlan> {
    return this.http.get<MealPlan>(`${this.apiUrl}/patient/${patientId}/active`);
  }

}
