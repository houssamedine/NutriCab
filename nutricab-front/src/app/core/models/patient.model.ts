export interface Patient{
  id:number,
  fullName:string,
  birthDate:string,
  phone:string,
  heightCm:number,
  initialWeightKg?:number,
  objective?:string,
  initialBmi?:number,
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CreatePatientRequest{
  userId?:number,
  fullName:string,
  birthDate:string,
  phone:string,
  heightCm:number,
  initialWeightKg:number,
  objective?:string,
}
