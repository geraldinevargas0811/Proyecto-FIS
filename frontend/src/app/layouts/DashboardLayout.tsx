import { Outlet } from 'react-router-dom'
import Sidebar from './SideNav/Sidebar'
import Topbar from './Topbar/Topbar'
import { useAuthStore } from '../../store/authStore'

export default function DashboardLayout() {
  const role = useAuthStore((s) => s.role)

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,rgba(6,182,212,0.16),transparent_32%),radial-gradient(circle_at_top_right,rgba(139,92,246,0.16),transparent_30%),linear-gradient(135deg,#f8fafc_0%,#eef7ff_48%,#f7f3ff_100%)] text-slate-950">
      <div className="flex min-h-screen">
        <Sidebar role={role} />

        <div className="flex flex-1 flex-col">
          <Topbar />

          <main className="flex-1 px-4 pb-10 pt-6 md:px-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  )
}

