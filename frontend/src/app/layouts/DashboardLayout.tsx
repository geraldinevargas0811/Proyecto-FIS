import { Outlet } from 'react-router-dom'
import Sidebar from './SideNav/Sidebar'
import Topbar from './Topbar/Topbar'
import { useAuthStore } from '../../store/authStore'

export default function DashboardLayout() {
  const role = useAuthStore((s) => s.role)

  return (
    <div className="min-h-screen bg-black text-white">
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

