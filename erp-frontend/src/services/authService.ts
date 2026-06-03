import api from '../config/api';
import type { LoginRequest, RegisterRequest, UserCreateRequest, User, Role } from '../types/auth';

export const authService = {
  login: async (credentials: LoginRequest): Promise<string> => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<string> => {
    const response = await api.post('/auth/register', data);
    return response.data;
  },

  createUser: async (user: UserCreateRequest): Promise<User> => {
    const response = await api.post<User>('/users/create', user);
    return response.data;
  },

  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get<User[]>('/users/all');
    return response.data;
  },

  createRole: async (name: string): Promise<Role> => {
    const response = await api.post<Role>(`/roles/create?name=${encodeURIComponent(name)}`);
    return response.data;
  }
};
