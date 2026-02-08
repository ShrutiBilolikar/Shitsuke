import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';
import { queryClient } from '@/lib/queryClient';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle 401 (token expiration) and 403 (forbidden)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Unauthorized - clear everything
      queryClient.clear(); // Clear React Query cache
      localStorage.removeItem('auth_token');
      useAuthStore.getState().clearAuth();

      // Redirect to login if not already there
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    } else if (error.response?.status === 403) {
      // 403 Forbidden - be more careful about redirecting
      // Only redirect if:
      // 1. We have a token (meaning we should be authenticated)
      // 2. We're on a protected route
      // 3. The error is not from a login/register endpoint
      const token = localStorage.getItem('auth_token');
      const isProtectedRoute = window.location.pathname.startsWith('/app');
      const isAuthEndpoint = error.config?.url?.includes('/auth/');
      
      if (token && isProtectedRoute && !isAuthEndpoint) {
        console.error('403 Forbidden - Token may be invalid or expired for:', error.config?.url);
        // Don't immediately redirect - let the component handle it
        // This prevents redirect loops during initial load
      }
      // Let the error propagate so components can handle it
    }
    return Promise.reject(error);
  }
);

export default apiClient;
