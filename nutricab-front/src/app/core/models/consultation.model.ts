import { Patient } from './patient.model';

export interface Consultation {
  id: number;
  patient: Patient;
  consultationDate: string;
  weightKg: number;
  waistCm?: number;
  notes?: string;
  recommendations?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CreateConsultationRequest {
  patientId: number;
  consultationDate: string;
  weightKg: number;
  waistCm?: number;
  notes?: string;
  recommendations?: string;
}
