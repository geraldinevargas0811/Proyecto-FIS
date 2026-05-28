import { useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Activity, CreditCard, Dumbbell, RefreshCw, UserPlus, Users } from 'lucide-react'
import { adminApi, type CreateClientPayload, type CreateInstructorPayload } from '../../api/adminApi'
import Card from '../../components/ui/Card'

const inputClass = 'w-full rounded-lg border border-white/10 bg-black/30 px-3 py-2 text-sm text-white outline-none focus:border-cyan-300/50'

const emptyClient: CreateClientPayload = {
  nombre: '',
  apellido: '',
  correo: '',
  contrasena: '123456',
  documento: '',
  telefono: '',
  objetivo: 'AUMENTAR_MASA',
  frecuenciaEntrenamiento: '3 dias/semana',
  genero: 'OTHER',
}

const emptyInstructor: CreateInstructorPayload = {
  nombre: '',
  apellido: '',
  correo: '',
  contrasena: '123456',
  documento: '',
  telefono: '',
  especialidad: '',
  certificaciones: '',
  anosExperiencia: 0,
  horarioTrabajo: '',
  contractType: 'PART_TIME',
}

export default function AdminDashboardPlaceholder() {
  const queryClient = useQueryClient()
  const [clientForm, setClientForm] = useState<CreateClientPayload>(emptyClient)
  const [instructorForm, setInstructorForm] = useState<CreateInstructorPayload>(emptyInstructor)

  const dashboard = useQuery({ queryKey: ['admin', 'dashboard'], queryFn: () => adminApi.dashboard().then((r) => r.data) })
  const clientes = useQuery({ queryKey: ['admin', 'clientes'], queryFn: () => adminApi.clientes().then((r) => r.data) })
  const instructores = useQuery({ queryKey: ['admin', 'instructores'], queryFn: () => adminApi.instructores().then((r) => r.data) })
  const pagos = useQuery({ queryKey: ['admin', 'pagos-pendientes'], queryFn: () => adminApi.pagosPendientes().then((r) => r.data) })

  const invalidateAdmin = () => queryClient.invalidateQueries({ queryKey: ['admin'] })

  const createClient = useMutation({
    mutationFn: adminApi.crearCliente,
    onSuccess: () => {
      setClientForm(emptyClient)
      invalidateAdmin()
    },
  })

  const createInstructor = useMutation({
    mutationFn: adminApi.crearInstructor,
    onSuccess: () => {
      setInstructorForm(emptyInstructor)
      invalidateAdmin()
    },
  })

  const activeClients = useMemo(
    () => clientes.data?.filter((cliente) => cliente.activo ?? cliente.active).length ?? 0,
    [clientes.data],
  )

  const isLoading = dashboard.isLoading || clientes.isLoading || instructores.isLoading || pagos.isLoading
  const error = dashboard.error || clientes.error || instructores.error || pagos.error

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white">Dashboard ADMIN</h1>
          <p className="mt-1 text-sm text-slate-300">Datos reales desde Spring Boot y PostgreSQL.</p>
        </div>
        <button
          type="button"
          onClick={() => invalidateAdmin()}
          className="inline-flex items-center justify-center gap-2 rounded-lg border border-white/10 bg-white/5 px-3 py-2 text-sm text-cyan-100 hover:bg-white/10"
        >
          <RefreshCw size={16} />
          Actualizar
        </button>
      </div>

      {error ? (
        <div className="rounded-lg border border-rose-500/30 bg-rose-500/10 p-3 text-sm text-rose-100">
          No se pudieron cargar datos administrativos. Revisa la sesion o el backend.
        </div>
      ) : null}

      <div className="grid gap-3 md:grid-cols-4">
        <Metric icon={<Users size={18} />} label="Clientes" value={dashboard.data?.totalClientes ?? activeClients} />
        <Metric icon={<Dumbbell size={18} />} label="Instructores" value={dashboard.data?.totalInstructores ?? instructores.data?.length ?? 0} />
        <Metric icon={<CreditCard size={18} />} label="Pagos pendientes" value={dashboard.data?.totalPagosPendientes ?? pagos.data?.length ?? 0} />
        <Metric icon={<Activity size={18} />} label="Membresias activas" value={dashboard.data?.totalMembresiasActivas ?? 0} />
      </div>

      <div className="grid gap-4 xl:grid-cols-2">
        <Card>
          <SectionTitle icon={<Users size={18} />} title="Clientes" loading={isLoading} />
          <div className="mt-4 overflow-x-auto">
            <table className="w-full min-w-[560px] text-left text-sm">
              <thead className="text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Nombre</th>
                  <th>Correo</th>
                  <th>Documento</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/10 text-slate-200">
                {(clientes.data ?? []).slice(0, 8).map((cliente) => (
                  <tr key={cliente.id}>
                    <td className="py-3">{cliente.nombre} {cliente.apellido}</td>
                    <td>{cliente.correo}</td>
                    <td>{cliente.documento ?? '-'}</td>
                    <td>{cliente.activo ?? cliente.active ? 'Activo' : 'Inactivo'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>

        <Card>
          <SectionTitle icon={<Dumbbell size={18} />} title="Instructores" loading={isLoading} />
          <div className="mt-4 overflow-x-auto">
            <table className="w-full min-w-[560px] text-left text-sm">
              <thead className="text-xs uppercase text-slate-400">
                <tr>
                  <th className="py-2">Nombre</th>
                  <th>Correo</th>
                  <th>Documento</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/10 text-slate-200">
                {(instructores.data ?? []).slice(0, 8).map((instructor) => (
                  <tr key={instructor.id}>
                    <td className="py-3">{instructor.nombre} {instructor.apellido}</td>
                    <td>{instructor.correo}</td>
                    <td>{instructor.documento ?? '-'}</td>
                    <td>{instructor.activo ?? instructor.active ? 'Activo' : 'Inactivo'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      </div>

      <div className="grid gap-4 xl:grid-cols-2">
        <Card>
          <SectionTitle icon={<UserPlus size={18} />} title="Registrar cliente" />
          <form
            className="mt-4 grid gap-3 sm:grid-cols-2"
            onSubmit={(event) => {
              event.preventDefault()
              createClient.mutate(clientForm)
            }}
          >
            <TextInput label="Nombre" value={clientForm.nombre} onChange={(nombre) => setClientForm({ ...clientForm, nombre })} />
            <TextInput label="Apellido" value={clientForm.apellido} onChange={(apellido) => setClientForm({ ...clientForm, apellido })} />
            <TextInput label="Correo" type="email" value={clientForm.correo} onChange={(correo) => setClientForm({ ...clientForm, correo })} />
            <TextInput label="Documento" value={clientForm.documento} onChange={(documento) => setClientForm({ ...clientForm, documento })} />
            <TextInput label="Telefono" value={clientForm.telefono ?? ''} onChange={(telefono) => setClientForm({ ...clientForm, telefono })} />
            <select className={inputClass} value={clientForm.objetivo} onChange={(e) => setClientForm({ ...clientForm, objetivo: e.target.value as CreateClientPayload['objetivo'] })}>
              <option value="AUMENTAR_MASA">Aumentar masa</option>
              <option value="DEFINICION">Definicion</option>
              <option value="PERDER_PESO">Perder peso</option>
            </select>
            <button className="rounded-lg bg-cyan-400 px-3 py-2 text-sm font-semibold text-black disabled:opacity-60" disabled={createClient.isPending}>
              Crear cliente
            </button>
          </form>
        </Card>

        <Card>
          <SectionTitle icon={<UserPlus size={18} />} title="Registrar instructor" />
          <form
            className="mt-4 grid gap-3 sm:grid-cols-2"
            onSubmit={(event) => {
              event.preventDefault()
              createInstructor.mutate(instructorForm)
            }}
          >
            <TextInput label="Nombre" value={instructorForm.nombre} onChange={(nombre) => setInstructorForm({ ...instructorForm, nombre })} />
            <TextInput label="Apellido" value={instructorForm.apellido} onChange={(apellido) => setInstructorForm({ ...instructorForm, apellido })} />
            <TextInput label="Correo" type="email" value={instructorForm.correo} onChange={(correo) => setInstructorForm({ ...instructorForm, correo })} />
            <TextInput label="Documento" value={instructorForm.documento} onChange={(documento) => setInstructorForm({ ...instructorForm, documento })} />
            <TextInput label="Especialidad" value={instructorForm.especialidad ?? ''} onChange={(especialidad) => setInstructorForm({ ...instructorForm, especialidad })} />
            <TextInput label="Horario" value={instructorForm.horarioTrabajo ?? ''} onChange={(horarioTrabajo) => setInstructorForm({ ...instructorForm, horarioTrabajo })} />
            <button className="rounded-lg bg-cyan-400 px-3 py-2 text-sm font-semibold text-black disabled:opacity-60" disabled={createInstructor.isPending}>
              Crear instructor
            </button>
          </form>
        </Card>
      </div>
    </div>
  )
}

function Metric({ icon, label, value }: { icon: ReactNode; label: string; value: number }) {
  return (
    <Card>
      <div className="flex items-center justify-between gap-3">
        <div>
          <div className="text-xs uppercase text-slate-400">{label}</div>
          <div className="mt-2 text-2xl font-bold text-white">{value}</div>
        </div>
        <div className="rounded-lg border border-cyan-300/20 bg-cyan-300/10 p-2 text-cyan-100">{icon}</div>
      </div>
    </Card>
  )
}

function SectionTitle({ icon, title, loading = false }: { icon: ReactNode; title: string; loading?: boolean }) {
  return (
    <div className="flex items-center justify-between gap-3">
      <div className="flex items-center gap-2 text-white">
        <span className="text-cyan-200">{icon}</span>
        <h2 className="text-base font-semibold">{title}</h2>
      </div>
      {loading ? <span className="text-xs text-slate-400">Cargando...</span> : null}
    </div>
  )
}

function TextInput({
  label,
  value,
  onChange,
  type = 'text',
}: {
  label: string
  value: string
  onChange: (value: string) => void
  type?: string
}) {
  return (
    <label className="space-y-1 text-sm text-slate-300">
      <span>{label}</span>
      <input className={inputClass} type={type} value={value} onChange={(event) => onChange(event.target.value)} required />
    </label>
  )
}
