import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from './pages/auth/LoginPage';
import { RegisterPage } from './pages/auth/RegisterPage';
import { DashboardPage } from './pages/dashboard/DashboardPage';
import { HabitsListPage } from './pages/habits/HabitsListPage';
import { HabitDetailPage } from './pages/habits/HabitDetailPage';
import { CreateHabitPage } from './pages/habits/CreateHabitPage';
import { GroupsListPage } from './pages/groups/GroupsListPage';
import { GroupDetailPage } from './pages/groups/GroupDetailPage';
import { CreateGroupPage } from './pages/groups/CreateGroupPage';
import { FriendsPage } from './pages/friends/FriendsPage';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { useAuthStore } from './stores/authStore';

function App() {
  const { isAuthenticated, _hasHydrated } = useAuthStore();

  // Wait for hydration before making routing decisions
  const tokenFromStorage = localStorage.getItem('auth_token');
  const isAuth = _hasHydrated ? isAuthenticated : !!tokenFromStorage;

  return (
    <BrowserRouter>
      <Routes>
        {/* Redirect root to appropriate page */}
        <Route
          path="/"
          element={
            isAuth ? (
              <Navigate to="/app/dashboard" replace />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />

        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected routes */}
        <Route
          path="/app/*"
          element={
            <ProtectedRoute>
              <Routes>
                <Route path="dashboard" element={<DashboardPage />} />
                <Route path="habits" element={<HabitsListPage />} />
                <Route path="habits/new" element={<CreateHabitPage />} />
                <Route path="habits/:id" element={<HabitDetailPage />} />
                <Route path="groups" element={<GroupsListPage />} />
                <Route path="groups/new" element={<CreateGroupPage />} />
                <Route path="groups/:id" element={<GroupDetailPage />} />
                <Route path="friends" element={<FriendsPage />} />
                <Route path="*" element={<Navigate to="/app/dashboard" replace />} />
              </Routes>
            </ProtectedRoute>
          }
        />

        {/* 404 fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
