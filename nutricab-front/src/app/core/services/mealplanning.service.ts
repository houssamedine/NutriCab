import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CreateMealPlanRequest, MealPlan } from '../models/meal-plan.model';
import { Observable } from 'rxjs';
import { Page } from '../models/user.model';
import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class MealplanningService {

  private apiBaseUrl = inject(API_BASE_URL);
  apiUrl = `${this.apiBaseUrl}/meal-plan`;

  constructor(private http: HttpClient) { }

  getAllMealPlans(page: number = 0, size: number = 10, sort: string = 'id,desc'): Observable<Page<MealPlan>> {
    return this.http.get<Page<MealPlan>>(this.apiUrl, {
      params: { page, size, sort }
    });
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

  searchMealPlans(keyword: string, page: number = 0, size: number = 10, sort: string = 'id,desc'): Observable<Page<MealPlan>> {
    const params = new HttpParams()
      .set('keyword', keyword.trim())
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    return this.http.get<Page<MealPlan>>(`${this.apiUrl}/search`, { params });
  }

  getMealPlansByPatientId(patientId: number, page: number = 0, size: number = 10, sort: string = 'id,desc'): Observable<Page<MealPlan>> {
    return this.http.get<Page<MealPlan>>(`${this.apiUrl}/patient/${patientId}`, {
      params: { page, size, sort }
    });
  }

  getMealPlansByCaloriesRange(minCalories: number, maxCalories: number, page: number = 0, size: number = 10, sort: string = 'calories,asc'): Observable<Page<MealPlan>> {
    const params = new HttpParams()
      .set('minCalories', minCalories.toString())
      .set('maxCalories', maxCalories.toString())
      .set('page', page)
      .set('size', size)
      .set('sort', sort);
    return this.http.get<Page<MealPlan>>(`${this.apiUrl}/calories`, { params });
  }

  getActiveMealPlanByPatientId(patientId: number): Observable<MealPlan> {
    return this.http.get<MealPlan>(`${this.apiUrl}/patient/${patientId}/active`);
  }

}
