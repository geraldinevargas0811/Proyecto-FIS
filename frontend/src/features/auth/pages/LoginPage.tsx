import { useEffect, useMemo } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '../../../store/authStore'
import type { Role } from '../../../types/auth'

const schema = z.object({
  username: z.string().min(3, 'Usuario requerido'),
  password: z.string().min(6, 'Contraseña mínima 6 caracteres'),
})

type FormValues = z.infer<typeof schema>

const roleToPath: Record<Role, string> = {
  ADMIN: '/admin/dashboard',
  INSTRUCTOR: '/instructor/dashboard',
  CLIENTE: '/cliente/dashboard',
}

export default function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation() as any

  const status = useAuthStore((s) => s.status)
  const error = useAuthStore((s) => s.error)
  const login = useAuthStore((s) => s.login)
  const loadMe = useAuthStore((s) => s.loadMe)
  const role = useAuthStore((s) => s.role)

  const resolver = useMemo(() => zodResolver(schema), [])

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver,
    defaultValues: { username: '', password: '' },
  })

  useEffect(() => {
    // mensaje por expiración
    if (location?.state?.reason === 'expired') {
      // el store manejará error de forma general; aquí no forzamos
    }
  }, [location?.state?.reason])

  useEffect(() => {
    if (role) {
      navigate(roleToPath[role], { replace: true })
    }
  }, [role, navigate])

  const onSubmit = handleSubmit(async (values) => {
    try {
      await login(values)

      // Si el backend no devuelve me en /login, cargamos.
      // En tu backend existe /me y roles.
      await loadMe()

      // Redirección por rol
      // (triggered by role effect)
    } catch {
      // error se muestra desde store
    }
  })

  return (
    <div className="min-h-screen bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-cyan-950 via-black to-black flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="relative overflow-hidden rounded-2xl border border-white/10 bg-black/60 shadow-[0_0_60px_rgba(34,211,238,0.15)]">
          <div className="pointer-events-none absolute inset-0 bg-[linear-gradient(120deg,rgba(34,211,238,0.18),transparent_40%,transparent_60%,rgba(244,63,94,0.12))]" />

          <div className="relative p-6">
            <div className="flex items-start justify-between gap-4">
              <div>
                <h1 className="text-3xl font-bold text-white">Iniciar sesión</h1>
                <p className="mt-1 text-sm text-slate-300">Accede a tu cuenta gym.</p>
              </div>
              <div className="h-10 w-10 rounded-xl bg-gradient-to-br from-cyan-500 to-fuchsia-500 blur-[0.2px]" />
            </div>

            <div className="mt-5">
              {error ? (
                <div className="mb-4 rounded-xl border border-rose-500/20 bg-rose-500/10 p-3 text-rose-200 text-sm">
                  {error}
                </div>
              ) : null}

              <form onSubmit={onSubmit} className="space-y-4">
                <label className="block">
                  <span className="text-sm text-slate-200">Usuario</span>
                  <input
                    {...register('username')}
                    className="mt-1 w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-white placeholder:text-slate-500 outline-none focus:border-cyan-400/40 focus:ring-2 focus:ring-cyan-400/20"
                    placeholder="tu.usuario"
                    autoComplete="username"
                  />
                  {errors.username ? (
                    <span className="mt-1 block text-xs text-rose-300">{errors.username.message}</span>
                  ) : null}
                </label>

                <label className="block">
                  <span className="text-sm text-slate-200">Contraseña</span>
                  <input
                    {...register('password')}
                    type="password"
                    className="mt-1 w-full rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-white placeholder:text-slate-500 outline-none focus:border-cyan-400/40 focus:ring-2 focus:ring-cyan-400/20"
                    placeholder="••••••••"
                    autoComplete="current-password"
                  />
                  {errors.password ? (
                    <span className="mt-1 block text-xs text-rose-300">{errors.password.message}</span>
                  ) : null}
                </label>

                <button
                  type="submit"
                  disabled={status === 'loading'}
                  className="w-full rounded-xl bg-gradient-to-r from-cyan-500 to-fuchsia-500 px-4 py-3 font-semibold text-black shadow-[0_10px_30px_rgba(34,211,238,0.25)] disabled:opacity-70"
                >
                  {status === 'loading' ? 'Validando...' : 'Entrar'}
                </button>

                <div className="text-center text-xs text-slate-400">
                  Al iniciar sesión aceptas el uso de tokens JWT.
                </div>
              </form>
            </div>

            <div className="mt-6 flex items-center justify-between text-xs text-slate-400">
              <span>GymPro Auth</span>
              <span className="tabular-nums">v1</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

