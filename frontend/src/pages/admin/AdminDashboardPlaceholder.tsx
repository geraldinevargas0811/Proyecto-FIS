import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Activity, CreditCard, Dumbbell, RefreshCw, Sparkles, Users } from 'lucide-react'
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { adminApi } from '../../api/adminApi'
import type { AdminClient, AdminInstructor, AdminMembership, AdminPayment } from '../../api/adminApi'
import { EmptyPanel, LoadingPanel, PageHeader, StatusBadge, formatMoney, normalizeList, panelClass, secondaryButtonClass } from './AdminUI'

export default function AdminDashboardPlaceholder() {
  const queryClient = useQueryClient()
  const dashboard = useQuery({ queryKey: ['admin', 'dashboard'], queryFn: () => adminApi.dashboard().then((r) => r.data), refetchInterval: 12000 })
  const clientes = useQuery({ queryKey: ['admin', 'clientes'], queryFn: () => adminApi.clientes().then((r) => r.data), refetchInterval: 12000 })
  const instructores = useQuery({ queryKey: ['admin', 'instructores'], queryFn: () => adminApi.instructores().then((r) => r.data), refetchInterval: 12000 })
  const pagos = useQuery({ queryKey: ['admin', 'pagos'], queryFn: () => adminApi.pagos().then((r) => r.data), refetchInterval: 12000 })
  const membresias = useQuery({ queryKey: ['admin', 'membresias'], queryFn: () => adminApi.membresias().then((r) => r.data), refetchInterval: 12000 })

  const safeClientes = normalizeList<AdminClient>(clientes.data)
  const safeInstructores = normalizeList<AdminInstructor>(instructores.data)
  const safePagos = normalizeList<AdminPayment>(pagos.data)
  const allMembresias: AdminMembership[] = [
    ...normalizeList<AdminMembership>(membresias.data?.activas),
    ...normalizeList<AdminMembership>(membresias.data?.pendientes),
    ...normalizeList<AdminMembership>(membresias.data?.vencidas),
    ...normalizeList<AdminMembership>(membresias.data?.suspendidas),
  ]

  const isLoading = dashboard.isLoading || clientes.isLoading || instructores.isLoading || pagos.isLoading || membresias.isLoading

  const growthData = buildGrowthData(safeClientes)
  const membershipData = [
    { name: 'Activas', value: normalizeList<AdminMembership>(membresias.data?.activas).length },
    { name: 'Pendientes', value: normalizeList<AdminMembership>(membresias.data?.pendientes).length },
    { name: 'Vencidas', value: normalizeList<AdminMembership>(membresias.data?.vencidas).length },
  ]
  const paymentData = [
    { name: 'Pendientes', value: safePagos.filter((p) => p.estado === 'PENDIENTE').length },
    { name: 'Pagados', value: safePagos.filter((p) => p.estado === 'PAGADO' || p.estado === 'VALIDADO' || p.estado === 'APROBADO').length },
    { name: 'Anulados', value: safePagos.filter((p) => p.estado === 'ANULADO').length },
  ]

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Panel principal"
        title="Control administrativo"
        description="Resumen vivo del gimnasio: usuarios, pagos, membresias y actividad reciente sin mezclar formularios ni tablas gigantes."
        action={
          <button type="button" className={secondaryButtonClass} onClick={() => queryClient.invalidateQueries({ queryKey: ['admin'] })}>
            <RefreshCw size={16} />
            Actualizar
          </button>
        }
      />

      {isLoading ? <LoadingPanel label="Sincronizando metricas administrativas..." /> : null}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <MetricCard icon={<Users size={22} />} label="Clientes" value={dashboard.data?.totalClientes ?? safeClientes.length} accent="from-cyan-500 to-blue-500" />
        <MetricCard icon={<Dumbbell size={22} />} label="Instructores" value={dashboard.data?.totalInstructores ?? safeInstructores.length} accent="from-violet-500 to-fuchsia-500" />
        <MetricCard icon={<CreditCard size={22} />} label="Pagos pendientes" value={dashboard.data?.totalPagosPendientes ?? paymentData[0].value} accent="from-amber-400 to-orange-500" />
        <MetricCard icon={<Activity size={22} />} label="Membresias activas" value={dashboard.data?.totalMembresiasActivas ?? membershipData[0].value} accent="from-emerald-400 to-cyan-500" />
      </div>

      <div className="grid gap-5 xl:grid-cols-[1.35fr_0.65fr]">
        <section className={panelClass}>
          <SectionTitle title="Crecimiento de clientes" subtitle="Registros agrupados por mes" />
          <div className="mt-5 h-72">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={growthData}>
                <defs>
                  <linearGradient id="clientGrowth" x1="0" x2="0" y1="0" y2="1">
                    <stop offset="0%" stopColor="#06b6d4" stopOpacity={0.45} />
                    <stop offset="100%" stopColor="#8b5cf6" stopOpacity={0.05} />
                  </linearGradient>
                </defs>
                <CartesianGrid stroke="#e2e8f0" strokeDasharray="4 4" />
                <XAxis dataKey="name" stroke="#64748b" />
                <YAxis allowDecimals={false} stroke="#64748b" />
                <Tooltip />
                <Area type="monotone" dataKey="clientes" stroke="#06b6d4" strokeWidth={3} fill="url(#clientGrowth)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </section>

        <section className={panelClass}>
          <SectionTitle title="Membresias" subtitle="Estado actual" />
          <div className="mt-5 h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={membershipData} dataKey="value" nameKey="name" innerRadius={58} outerRadius={88} paddingAngle={4}>
                  {['#06b6d4', '#f59e0b', '#ef4444'].map((color) => (
                    <Cell key={color} fill={color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="grid gap-2">
            {membershipData.map((item) => (
              <div key={item.name} className="flex items-center justify-between rounded-xl bg-slate-50 px-3 py-2 text-sm">
                <span className="font-semibold text-slate-700">{item.name}</span>
                <span className="font-black text-slate-950">{item.value}</span>
              </div>
            ))}
          </div>
        </section>
      </div>

      <div className="grid gap-5 xl:grid-cols-2">
        <section className={panelClass}>
          <SectionTitle title="Pagos" subtitle="Distribucion por estado" />
          <div className="mt-5 h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={paymentData}>
                <CartesianGrid stroke="#e2e8f0" strokeDasharray="4 4" />
                <XAxis dataKey="name" stroke="#64748b" />
                <YAxis allowDecimals={false} stroke="#64748b" />
                <Tooltip />
                <Bar dataKey="value" radius={[10, 10, 0, 0]} fill="#8b5cf6" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </section>

        <section className={panelClass}>
          <SectionTitle title="Actividad reciente" subtitle="Ultimos movimientos visibles" />
          <div className="mt-5 space-y-3">
            {safePagos.slice(0, 4).map((pago) => (
              <div key={pago.id} className="flex items-center justify-between gap-3 rounded-2xl bg-white p-3 shadow-sm">
                <div>
                  <div className="font-bold text-slate-900">{pago.cliente ? `${pago.cliente.nombre} ${pago.cliente.apellido}` : `Pago #${pago.id}`}</div>
                  <div className="text-sm text-slate-500">{formatMoney(pago.monto)}</div>
                </div>
                <StatusBadge value={pago.estado} />
              </div>
            ))}
            {!safePagos.length && !allMembresias.length ? <EmptyPanel icon={<Sparkles size={22} />} title="Sin actividad reciente" description="Cuando existan pagos o membresias, apareceran aqui." /> : null}
          </div>
        </section>
      </div>
    </div>
  )
}

function MetricCard({ icon, label, value, accent }: { icon: React.ReactNode; label: string; value: number; accent: string }) {
  return (
    <div className="group rounded-2xl border border-white/70 bg-white/80 p-5 shadow-[0_16px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl transition hover:-translate-y-1 hover:shadow-[0_20px_70px_rgba(14,165,233,0.14)]">
      <div className="flex items-center justify-between gap-3">
        <div>
          <div className="text-sm font-bold text-slate-500">{label}</div>
          <div className="mt-2 text-3xl font-black text-slate-950">{value}</div>
        </div>
        <div className={`rounded-2xl bg-gradient-to-br ${accent} p-3 text-white shadow-lg transition group-hover:scale-105`}>{icon}</div>
      </div>
    </div>
  )
}

function SectionTitle({ title, subtitle }: { title: string; subtitle: string }) {
  return (
    <div>
      <h2 className="text-lg font-black text-slate-950">{title}</h2>
      <p className="mt-1 text-sm text-slate-500">{subtitle}</p>
    </div>
  )
}

function buildGrowthData(clientes: AdminClient[]) {
  const months = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun']
  const current = new Date()
  return months.map((name, index) => {
    const targetMonth = (current.getMonth() - (months.length - 1 - index) + 12) % 12
    const count = clientes.filter((cliente) => {
      if (!cliente.fechaRegistro) return index === months.length - 1
      return new Date(cliente.fechaRegistro).getMonth() === targetMonth
    }).length
    return { name, clientes: count }
  })
}
