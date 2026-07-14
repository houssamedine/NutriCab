import { Patient } from './patient.model';

export type UserRole = 'ADMIN' | 'NUTRITIONIST' | 'SECRETARY' | 'PATIENT';

export interface User {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  active: boolean;
  patients?: Patient[];
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CreateUserRequest {
  fullName: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// Pour la pagination (retournée par Spring Data)
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
