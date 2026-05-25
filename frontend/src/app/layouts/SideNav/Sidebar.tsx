import { NavLink, useLocation } from 'react-router-dom'
import { LayoutGrid, User, Dumbbell } from 'lucide-react'
import { motion } from 'framer-motion'
import type { Role } from '../../../types/auth'

const roleMenus: Record<Role, Array<{ to: string; label: string; icon: React.ReactNode }>> = {
  ADMIN: [{ to: '/admin/dashboard', label: 'Dashboard', icon: <LayoutGrid size={16} /> }],
  INSTRUCTOR: [{ to: '/instructor/dashboard', label: 'Dashboard', icon: <Dumbbell size={16} /> }],
  CLIENTE: [{ to: '/cliente/dashboard', label: 'Dashboard', icon: <User size={16} /> }],
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
          : 'hidden md:flex w-72 shrink-0 flex-col border-r border-white/10 bg-black/60 backdrop-blur'}
      initial={mobile ? { opacity: 0 } : undefined}
      animate={mobile ? { opacity: 1 } : undefined}
    >
      <div className="p-4">
        <div className="rounded-2xl border border-white/10 bg-white/5 p-3">
          <div className="text-xs font-semibold text-slate-100">Navegación</div>
          <div className="mt-1 text-[11px] text-slate-400">Fitness premium · JWT</div>
        </div>
      </div>

      <nav className="flex-1 px-2 pb-4">
        {menus.length ? (
          <div className="space-y-1">
            {menus.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end
                onClick={onNavigate}
                className={({ isActive }) =>
                  [
                    'flex items-center gap-3 rounded-xl px-3 py-2 text-sm transition',
                    isActive
                      ? 'bg-gradient-to-r from-cyan-500/15 to-fuchsia-500/10 border border-cyan-300/20 text-cyan-100'
                      : 'border border-transparent text-slate-200 hover:bg-white/5 hover:border-white/10',
                  ].join(' ')
                }
              >
                <span className={location.pathname === item.to ? 'text-cyan-200' : 'text-slate-300'}>
                  {item.icon}
                </span>
                <span>{item.label}</span>
              </NavLink>
            ))}
          </div>
        ) : (
          <div className="px-3 text-xs text-slate-400">Inicia sesión para ver tus menús.</div>
        )}
      </nav>

      <div className="p-4 pt-0">
        <div className="rounded-2xl border border-white/10 bg-gradient-to-br from-cyan-500/10 to-fuchsia-500/10 p-3">
          <div className="text-xs font-semibold text-white">GymPro</div>
          <div className="mt-1 text-[11px] text-slate-300">Premium UI base</div>
        </div>
      </div>
    </motion.aside>
  )
}

