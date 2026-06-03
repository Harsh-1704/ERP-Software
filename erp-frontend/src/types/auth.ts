export interface User {
  id: number;
  username: string;
  password?: string;
  role: Role;
  active: boolean;
}

export interface Role {
  id: number;
  name: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserCreateRequest {
  username: string;
  password: string;
  role: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  role: string;
}
