import { NavLink, useLocation } from 'react-router-dom'
import type { ReactNode } from 'react'
import { BarChart3, CreditCard, Crown, Dumbbell, LayoutGrid, Settings, ShieldCheck, User, UserPlus, Users } from 'lucide-react'
import { motion } from 'framer-motion'
import type { Role } from '../../../types/auth'

const roleMenus: Record<Role, Array<{ title?: string; items: Array<{ to: string; label: string; icon: ReactNode }> }>> = {
  ADMIN: [
    { title: 'Panel principal', items: [
      { to: '/admin/dashboard', label: 'Dashboard', icon: <LayoutGrid size={16} /> },
      { to: '/admin/dashboard', label: 'Estadisticas y graficas', icon: <BarChart3 size={16} /> },
    ] },
    { title: 'Clientes', items: [
      { to: '/admin/clientes', label: 'Ver clientes', icon: <Users size={16} /> },
      { to: '/admin/clientes', label: 'Registrar cliente', icon: <UserPlus size={16} /> },
      { to: '/admin/membresias', label: 'Membresias', icon: <ShieldCheck size={16} /> },
      { to: '/admin/pagos', label: 'Pagos clientes', icon: <CreditCard size={16} /> },
    ] },
    { title: 'Instructores', items: [
      { to: '/admin/instructores', label: 'Ver instructores', icon: <Dumbbell size={16} /> },
      { to: '/admin/instructores', label: 'Registrar instructor', icon: <UserPlus size={16} /> },
      { to: '/admin/pagos', label: 'Pagos instructores', icon: <CreditCard size={16} /> },
    ] },
    { title: 'Planes', items: [
      { to: '/admin/planes', label: 'Ver planes', icon: <Crown size={16} /> },
      { to: '/admin/planes', label: 'Crear planes', icon: <Crown size={16} /> },
    ] },
    { title: 'Configuracion', items: [
      { to: '/admin/dashboard', label: 'Ajustes basicos', icon: <Settings size={16} /> },
    ] },
  ],
  INSTRUCTOR: [{ items: [{ to: '/instructor/dashboard', label: 'Dashboard', icon: <Dumbbell size={16} /> }] }],
  CLIENTE: [{ items: [{ to: '/cliente/dashboard', label: 'Dashboard', icon: <User size={16} /> }] }],
}

export default function Sidebar({
  role,
  mobile = false,
  onNavigate,
}: {
  role: Role | null
  mobile?: boolean
  onNavigate?: () => void
}) {
  const location = useLocation()
  const menus = role ? roleMenus[role] : []

  return (
    <motion.aside
      className={
        mobile
          ? 'h-full'
          : 'hidden md:flex w-72 shrink-0 flex-col border-r border-slate-200/70 bg-white/75 backdrop-blur-xl'
      }
      initial={mobile ? { opacity: 0 } : undefined}
      animate={mobile ? { opacity: 1 } : undefined}
    >
      <div className="p-4">
        <div className="rounded-2xl border border-white/70 bg-gradient-to-br from-cyan-50 to-violet-50 p-4 shadow-sm">
          <div className="text-sm font-black text-slate-950">GymPro Admin</div>
          <div className="mt-1 text-[11px] font-semibold uppercase tracking-wide text-slate-500">Fitness premium</div>
        </div>
      </div>

      <nav className="flex-1 px-2 pb-4">
        {menus.length ? (
          <div className="space-y-4">
            {menus.map((section, index) => (
              <div key={`${section.title ?? 'menu'}-${index}`} className="space-y-1">
                {section.title ? <div className="px-3 pb-1 text-[11px] font-black uppercase tracking-[0.16em] text-slate-400">{section.title}</div> : null}
                {section.items.map((item) => (
              <NavLink
                key={`${section.title}-${item.label}`}
                to={item.to}
                end
                onClick={onNavigate}
                className={({ isActive }) =>
                  [
                    'flex items-center gap-3 rounded-xl px-3 py-2 text-sm font-semibold transition',
                    isActive
                      ? 'border border-cyan-200 bg-gradient-to-r from-cyan-50 to-violet-50 text-slate-950 shadow-sm'
                      : 'border border-transparent text-slate-600 hover:border-slate-200 hover:bg-white',
                  ].join(' ')
                }
              >
                <span className={location.pathname === item.to ? 'text-cyan-600' : 'text-slate-400'}>
                  {item.icon}
                </span>
                <span>{item.label}</span>
              </NavLink>
                ))}
              </div>
            ))}
          </div>
        ) : (
          <div className="px-3 text-xs text-slate-400">Inicia sesion para ver tus menus.</div>
        )}
      </nav>

      <div className="p-4 pt-0">
        <div className="rounded-2xl border border-slate-200 bg-slate-950 p-4">
          <div className="text-xs font-bold text-white">Operacion segura</div>
          <div className="mt-1 text-[11px] text-slate-300">JWT y roles intactos</div>
        </div>
      </div>
    </motion.aside>
  )
}
