import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { User } from '@/types/api.types';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  _hasHydrated: boolean;
  setAuth: (user: User, token: string) => void;
  clearAuth: () => void;
  setUser: (user: User) => void;
  setHasHydrated: (state: boolean) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      _hasHydrated: false,

      setAuth: (user, token) => {
        // Store in localStorage first for immediate access
        localStorage.setItem('auth_token', token);
        // Then update Zustand state (persist will sync to localStorage automatically)
        set({ user, token, isAuthenticated: true });
      },

      clearAuth: () => {
        localStorage.removeItem('auth_token');
        // Clear all localStorage items related to auth
        localStorage.removeItem('auth-storage');
        set({ user: null, token: null, isAuthenticated: false, _hasHydrated: false });
      },

      setUser: (user) => {
        set({ user });
      },

      setHasHydrated: (state) => {
        set({ _hasHydrated: state });
      },
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
      onRehydrateStorage: () => (state) => {
        // Sync token from localStorage if it exists
        if (state) {
          const token = localStorage.getItem('auth_token');
          if (token && !state.token) {
            state.token = token;
            state.isAuthenticated = true;
          }
          state._hasHydrated = true;
        }
      },
    }
  )
);
