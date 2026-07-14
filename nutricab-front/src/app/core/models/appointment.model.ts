import { Patient } from './patient.model';

export type AppointmentStatus = 'PLANNED' | 'DONE' | 'CANCELLED';

export interface Appointment{
  id:number,
  patient:Patient,
  appointmentDate:string,
  status:AppointmentStatus,
  notes:string,
  createdAt:string,
  updatedAt:string,
  createdBy:string,
  updatedBy:string
}

export interface CreateAppointmentRequest{
  patientId:number,
  appointmentDate:string,
  notes:string
}
