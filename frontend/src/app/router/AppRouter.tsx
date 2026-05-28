import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from '../../routes/ProtectedRoute'
import RoleGuard from '../../routes/RoleGuard'
import AuthProvider from '../providers/AuthProvider'
import LoginPage from '../../features/auth/pages/LoginPage'
import { useAuthStore } from '../../store/authStore'
import DashboardLayout from '../layouts/DashboardLayout'
import AdminDashboardPlaceholder from '../../pages/admin/AdminDashboardPlaceholder'
import InstructorDashboardPlaceholder from '../../pages/instructor/InstructorDashboardPlaceholder'
import ClienteDashboardPlaceholder from '../../pages/client/ClienteDashboardPlaceholder'

function RoleRedirect() {
  const role = useAuthStore((s) => s.role)
  if (!role) return <Navigate to="/login" replace />
  const to = role === 'ADMIN' ? '/admin/dashboard' : role === 'INSTRUCTOR' ? '/instructor/dashboard' : '/cliente/dashboard'
  return <Navigate to={to} replace />
}

export default function AppRouter() {
  return (
    <Routes>
      <Route element={<AuthProvider />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<RoleRedirect />} />

        <Route element={<ProtectedRoute />}>
          <Route
            path="/admin/dashboard"
            element={
              <RoleGuard allowedRoles={['ADMIN']}>
                <DashboardLayout />
              </RoleGuard>
            }
          >
            <Route index element={<AdminDashboardPlaceholder />} />
          </Route>

          <Route
            path="/instructor/dashboard"
            element={
              <RoleGuard allowedRoles={['INSTRUCTOR']}>
                <DashboardLayout />
              </RoleGuard>
            }
          >
            <Route index element={<InstructorDashboardPlaceholder />} />
          </Route>

          <Route
            path="/cliente/dashboard"
            element={
              <RoleGuard allowedRoles={['CLIENTE']}>
                <DashboardLayout />
              </RoleGuard>
            }
          >
            <Route index element={<ClienteDashboardPlaceholder />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

