import { apiClient } from './client';
import { AuthRequest, AuthResponse, RegisterRequest } from '@/types/api.types';

export const authApi = {
  login: async (credentials: AuthRequest): Promise<AuthResponse> => {
    const { data } = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return data;
  },

  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const { data } = await apiClient.post<AuthResponse>('/auth/register', userData);
    return data;
  },
};
