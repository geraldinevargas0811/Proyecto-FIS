import { useEffect, useMemo } from 'react'
import { Outlet, useNavigate } from 'react-router-dom'
import { setupInterceptors } from '../../api/interceptors'

import { useAuthStore } from '../../store/authStore'

export default function AuthProvider() {
  const navigate = useNavigate()

  const hydrateFromStorage = useAuthStore((s) => s.hydrateFromStorage)
  const loadMe = useAuthStore((s) => s.loadMe)

  // Create a stable callback for interceptor
  const onSessionExpired = useMemo(
    () => () => {
      // limpia store (store loadMe también gestiona estado)
      navigate('/login', { replace: true, state: { reason: 'expired' } })
    },
    [navigate],
  )

  useEffect(() => {
    hydrateFromStorage()
    setupInterceptors(onSessionExpired)

    // Bootstrap: si hay token, intentamos /me
    // (si falla, el store deja unauthenticated)
    loadMe()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    // cuando cambie el estado, el router decide
  }, [])

  return <Outlet />
}

