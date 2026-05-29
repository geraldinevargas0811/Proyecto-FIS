import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from '../../routes/ProtectedRoute'
import RoleGuard from '../../routes/RoleGuard'
import AuthProvider from '../providers/AuthProvider'
import LoginPage from '../../features/auth/pages/LoginPage'
import { useAuthStore } from '../../store/authStore'
import DashboardLayout from '../layouts/DashboardLayout'
import AdminDashboardPlaceholder from '../../pages/admin/AdminDashboardPlaceholder'
import AdminClientesPage from '../../pages/admin/AdminClientesPage'
import AdminInstructoresPage from '../../pages/admin/AdminInstructoresPage'
import AdminPagosPage from '../../pages/admin/AdminPagosPage'
import AdminMembresiasPage from '../../pages/admin/AdminMembresiasPage'
import AdminPlanesPage from '../../pages/admin/AdminPlanesPage'
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
            path="/admin"
            element={
              <RoleGuard allowedRoles={['ADMIN']}>
                <DashboardLayout />
              </RoleGuard>
            }
          >
            <Route index element={<Navigate to="/admin/dashboard" replace />} />
            <Route path="dashboard" element={<AdminDashboardPlaceholder />} />
            <Route path="clientes" element={<AdminClientesPage />} />
            <Route path="instructores" element={<AdminInstructoresPage />} />
            <Route path="pagos" element={<AdminPagosPage />} />
            <Route path="membresias" element={<AdminMembresiasPage />} />
            <Route path="planes" element={<AdminPlanesPage />} />
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

