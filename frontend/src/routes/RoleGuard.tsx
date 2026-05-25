import { Navigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import type { Role } from '../types/auth'

const roleToRedirect: Record<Role, string> = {
  ADMIN: '/admin/dashboard',
  INSTRUCTOR: '/instructor/dashboard',
  CLIENTE: '/cliente/dashboard',
}

type Props = {
  allowedRoles: Role[]
  children?: React.ReactNode
}

export default function RoleGuard({ allowedRoles, children }: Props) {
  const role = useAuthStore((s) => s.role)

  if (!role) {
    return <Navigate to="/login" replace />
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to={roleToRedirect[role]} replace />
  }

  return <>{children ?? null}</>
}

