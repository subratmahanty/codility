import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { User, AuthResponse, UserRole } from '../types';
import { authApi } from '../api/authApi';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

interface AuthActions {
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
  checkToken: () => boolean;
  hasRole: (role: UserRole) => boolean;
  hasAnyRole: (roles: UserRole[]) => boolean;
  hasPermission: (permission: string) => boolean;
}

type AuthStore = AuthState & AuthActions;

const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Actions
      login: async (username: string, password: string) => {
        set({ isLoading: true, error: null });
        
        try {
          const response: AuthResponse = await authApi.login({ username, password });
          
          // Store token in localStorage and set in axios defaults
          localStorage.setItem('heartbeat_token', response.token);
          
          set({
            user: response.user,
            token: response.token,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Login failed';
          set({
            user: null,
            token: null,
            isAuthenticated: false,
            isLoading: false,
            error: errorMessage,
          });
          throw error;
        }
      },

      logout: () => {
        localStorage.removeItem('heartbeat_token');
        set({
          user: null,
          token: null,
          isAuthenticated: false,
          isLoading: false,
          error: null,
        });
      },

      clearError: () => {
        set({ error: null });
      },

      checkToken: () => {
        const { token } = get();
        if (!token) return false;

        try {
          // Decode JWT to check expiration
          const payload = JSON.parse(atob(token.split('.')[1]));
          const currentTime = Date.now() / 1000;
          
          if (payload.exp < currentTime) {
            get().logout();
            return false;
          }
          
          return true;
        } catch (error) {
          get().logout();
          return false;
        }
      },

      hasRole: (role: UserRole) => {
        const { user } = get();
        return user?.role === role;
      },

      hasAnyRole: (roles: UserRole[]) => {
        const { user } = get();
        return user ? roles.includes(user.role) : false;
      },

      hasPermission: (permission: string) => {
        const { user } = get();
        if (!user) return false;

        // Define role-based permissions
        const rolePermissions: Record<UserRole, string[]> = {
          ADMIN: ['*'], // Admin has all permissions
          DOCTOR: [
            'patients.read',
            'patients.update.medical',
            'treatments.create',
            'treatments.read',
            'treatments.update',
            'prescriptions.create',
            'lab-tests.request',
            'vitals.read',
            'files.upload',
            'notifications.create',
          ],
          PHARMA_ADMIN: [
            'medicines.create',
            'medicines.read',
            'medicines.update',
            'lab-tests.create',
            'lab-tests.read',
            'lab-tests.update',
          ],
          LAB_ADMIN: [
            'patients.read',
            'lab-tests.read',
            'lab-results.create',
            'lab-results.read',
            'lab-results.update',
            'files.upload',
          ],
          NURSE: [
            'patients.read',
            'patients.update.basic',
            'vitals.create',
            'vitals.read',
            'treatments.read',
            'files.upload',
            'notifications.create',
          ],
          HELPDESK: [
            'patients.create',
            'patients.read',
            'patients.update.basic',
            'patients.search',
          ],
          SUPERVISOR: [
            'patients.read',
            'treatments.read',
            'practice-templates.create',
            'practice-templates.read',
            'practice-templates.update',
            'dashboard.read',
          ],
        };

        const userPermissions = rolePermissions[user.role] || [];
        
        // Admin has all permissions
        if (userPermissions.includes('*')) {
          return true;
        }
        
        return userPermissions.includes(permission);
      },
    }),
    {
      name: 'heartbeat-auth',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

export default useAuthStore;