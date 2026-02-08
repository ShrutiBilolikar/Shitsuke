import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';
import { ReactNode } from 'react';

interface ProtectedRouteProps {
  children: ReactNode;
}

export const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const { isAuthenticated, token } = useAuthStore();
  const location = useLocation();

  // Check both Zustand state and localStorage as fallback
  // This handles cases where Zustand hasn't hydrated yet
  const tokenFromStorage = localStorage.getItem('auth_token');
  const hasToken = token || tokenFromStorage;
  const isAuth = isAuthenticated || (hasToken !== null);

  if (!isAuth) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
};
