import { LogOut, Menu } from 'lucide-react'
import { useState } from 'react'
import { motion } from 'framer-motion'
import { useAuthStore } from '../../../store/authStore'
import MobileSidebarDrawer from './MobileSidebarDrawer'

export default function Topbar() {
  const logout = useAuthStore((s) => s.logout)
  const role = useAuthStore((s) => s.role)
  const [drawerOpen, setDrawerOpen] = useState(false)

  return (
    <>
      <MobileSidebarDrawer open={drawerOpen} onOpenChange={setDrawerOpen} />

      <header className="sticky top-0 z-30 border-b border-white/70 bg-white/70 backdrop-blur-xl">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3 md:px-8">
          <div className="flex items-center gap-3">
            <motion.button
              type="button"
              className="inline-flex items-center justify-center rounded-xl border border-slate-200 bg-white p-2 text-cyan-600 md:hidden"
              whileTap={{ scale: 0.98 }}
              onClick={() => setDrawerOpen(true)}
              aria-label="Open menu"
            >
              <Menu size={18} />
            </motion.button>

            <div className="flex items-center gap-2">
              <div className="h-9 w-9 rounded-2xl bg-gradient-to-br from-cyan-500 to-violet-500 shadow-[0_10px_30px_rgba(34,211,238,0.25)]" />
              <div>
                <div className="text-sm font-black text-slate-950">GymPro</div>
                <div className="text-[11px] font-semibold text-slate-500">Fitness management</div>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="hidden rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs font-semibold text-slate-600 md:block">
              Rol: <span className="font-bold text-cyan-600">{role ?? '-'}</span>
            </div>

            <motion.button
              type="button"
              whileTap={{ scale: 0.98 }}
              onClick={logout}
              className="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-cyan-50"
            >
              <LogOut size={16} />
              <span className="hidden sm:inline">Salir</span>
            </motion.button>
          </div>
        </div>
      </header>
    </>
  )
}
