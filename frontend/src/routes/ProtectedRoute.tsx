import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'

export default function ProtectedRoute() {
  const location = useLocation()
  const status = useAuthStore((s) => s.status)
  const accessToken = useAuthStore((s) => s.accessToken)

  if (status === 'loading') {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center text-cyan-200">
        Cargando autenticación...
      </div>
    )
  }

  if (!accessToken) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  return <Outlet />
}

