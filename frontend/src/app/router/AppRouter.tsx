import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from '../../routes/ProtectedRoute'
import RoleGuard from '../../routes/RoleGuard'
import AuthProvider from '../providers/AuthProvider'
import LoginPage from '../../features/auth/pages/LoginPage'
import { useAuthStore } from '../../store/authStore'

function RoleRedirect() {
  const role = useAuthStore((s) => s.role)
  if (!role) return <Navigate to="/login" replace />
  const to = role === 'ADMIN' ? '/admin/dashboard' : role === 'INSTRUCTOR' ? '/instructor/dashboard' : '/cliente/dashboard'
  return <Navigate to={to} replace />
}

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<AuthProvider />}>
        <Route path="/" element={<RoleRedirect />} />

        <Route
          element={
            <ProtectedRoute />
          }
        >
          <Route
            path="/admin/dashboard"
            element={
              <RoleGuard allowedRoles={['ADMIN']}>
                <div className="min-h-screen bg-black text-white flex items-center justify-center">ADMIN dashboard (placeholder)</div>
              </RoleGuard>
            }
          />

          <Route
            path="/instructor/dashboard"
            element={
              <RoleGuard allowedRoles={['INSTRUCTOR']}>
                <div className="min-h-screen bg-black text-white flex items-center justify-center">INSTRUCTOR dashboard (placeholder)</div>
              </RoleGuard>
            }
          />

          <Route
            path="/cliente/dashboard"
            element={
              <RoleGuard allowedRoles={['CLIENTE']}>
                <div className="min-h-screen bg-black text-white flex items-center justify-center">CLIENTE dashboard (placeholder)</div>
              </RoleGuard>
            }
          />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

