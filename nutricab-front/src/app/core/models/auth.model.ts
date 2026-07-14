export interface LoginRequest{
  email:string ,
  password:string
}

export interface AuthResponse {
  fullName: string;
  role: string;
}
