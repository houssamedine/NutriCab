import { Patient } from './patient.model';

export interface MealPlan {
  id: number;
  patient: Patient;
  title: string;
  objective?: string;
  calories?: number;
  content?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CreateMealPlanRequest {
  patientId: number;
  title: string;
  objective?: string;
  calories?: number;
  content?: string;
}
