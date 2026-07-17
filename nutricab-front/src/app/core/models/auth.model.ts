export interface LoginRequest{
  email:string ,
  password:string
}

export interface AuthResponse {
  accessToken: any;
  fullName: string;
  role: string;
}
