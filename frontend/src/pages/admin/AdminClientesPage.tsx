import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Eye, Pencil, Plus, Search, Trash2, Users } from 'lucide-react'
import { adminApi } from '../../api/adminApi'
import type { AdminClient, AdminInstructor, AdminPlan, CreateClientPayload, FitnessGoal, UpdateClientPayload } from '../../api/adminApi'
import {
  EmptyPanel,
  LoadingPanel,
  Modal,
  PageHeader,
  SelectField,
  StatusBadge,
  TextField,
  confirmAction,
  dangerButtonClass,
  isUserActive,
  normalizeList,
  panelClass,
  primaryButtonClass,
  secondaryButtonClass,
  showError,
  showSuccess,
} from './AdminUI'

type ClientForm = CreateClientPayload & { confirmPassword: string }
type ClientFormErrors = Partial<Record<keyof ClientForm, string>>

const emptyForm: ClientForm = {
  nombre: '',
  apellido: '',
  correo: '',
  contrasena: '',
  confirmPassword: '',
  documento: '',
  telefono: '',
  peso: undefined,
  altura: undefined,
  objetivo: 'AUMENTAR_MASA',
  frecuenciaEntrenamiento: '3 dias/semana',
  genero: 'OTHER',
  fechaNacimiento: '',
  quiereInstructor: false,
  instructorId: undefined,
  planId: undefined,
}

export default function AdminClientesPage() {
  const queryClient = useQueryClient()
  const [search, setSearch] = useState('')
  const [status, setStatus] = useState<'ALL' | 'ACTIVE' | 'INACTIVE'>('ALL')
  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<AdminClient | null>(null)
  const [detail, setDetail] = useState<AdminClient | null>(null)
  const [form, setForm] = useState<ClientForm>(emptyForm)
  const [errors, setErrors] = useState<ClientFormErrors>({})

  const clientes = useQuery({ queryKey: ['admin', 'clientes'], queryFn: () => adminApi.clientes().then((r) => r.data), refetchInterval: 12000 })
  const instructores = useQuery({ queryKey: ['admin', 'instructores'], queryFn: () => adminApi.instructores().then((r) => r.data), refetchInterval: 12000 })
  const planes = useQuery({ queryKey: ['admin', 'planes'], queryFn: () => adminApi.planes().then((r) => r.data), refetchInterval: 12000 })
  const safeClientes = normalizeList<AdminClient>(clientes.data)
  const safeInstructores = normalizeList<AdminInstructor>(instructores.data).filter((instructor) => isUserActive(instructor))
  const safePlanes = normalizeList<AdminPlan>(planes.data)

  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase()
    return safeClientes.filter((cliente) => {
      const active = isUserActive(cliente)
      const matchesStatus = status === 'ALL' || (status === 'ACTIVE' ? active : !active)
      const text = `${cliente.nombre} ${cliente.apellido} ${cliente.correo} ${cliente.documento ?? ''}`.toLowerCase()
      return matchesStatus && (!term || text.includes(term))
    })
  }, [safeClientes, search, status])

  const createClient = useMutation({
    mutationFn: adminApi.crearCliente,
    onSuccess: () => {
      showSuccess('Cliente creado')
      setFormOpen(false)
      setForm(emptyForm)
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  const updateClient = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateClientPayload }) => adminApi.actualizarCliente(id, payload),
    onSuccess: () => {
      showSuccess('Cliente actualizado')
      setFormOpen(false)
      setEditing(null)
      setForm(emptyForm)
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  const deactivateClient = useMutation({
    mutationFn: adminApi.desactivarCliente,
    onSuccess: () => {
      showSuccess('Cliente desactivado')
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  const openCreate = () => {
    setEditing(null)
    setForm(emptyForm)
    setErrors({})
    setFormOpen(true)
  }

  const openEdit = (cliente: AdminClient) => {
    setEditing(cliente)
    setForm({
      ...emptyForm,
      nombre: cliente.nombre,
      apellido: cliente.apellido,
      correo: cliente.correo,
      documento: cliente.documento ?? '',
      telefono: cliente.telefono ?? '',
      peso: cliente.peso,
      altura: cliente.altura,
      objetivo: cliente.objetivo ?? 'AUMENTAR_MASA',
      frecuenciaEntrenamiento: cliente.frecuenciaEntrenamiento ?? '3 dias/semana',
      genero: (cliente.genero as ClientForm['genero']) ?? 'OTHER',
      fechaNacimiento: cliente.fechaNacimiento ?? '',
      quiereInstructor: Boolean(cliente.quiereInstructor),
      instructorId: cliente.instructor?.id,
    })
    setErrors({})
    setFormOpen(true)
  }

  const submit = (event: React.FormEvent) => {
    event.preventDefault()
    const nextErrors = validateClientForm(form, Boolean(editing), safeClientes, editing?.id)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length) {
      showError(Object.values(nextErrors)[0] ?? 'Revisa los campos del formulario.')
      return
    }

    if (editing) {
      const payload: UpdateClientPayload = {
        nombre: form.nombre,
        apellido: form.apellido,
        correo: form.correo,
        documento: form.documento,
        telefono: form.telefono,
        peso: form.peso,
        altura: form.altura,
        objetivo: form.objetivo,
        frecuenciaEntrenamiento: form.frecuenciaEntrenamiento,
        genero: form.genero,
        fechaNacimiento: form.fechaNacimiento || undefined,
        quiereInstructor: form.quiereInstructor,
        instructorId: form.quiereInstructor ? form.instructorId : undefined,
      }
      updateClient.mutate({ id: editing.id, payload })
      return
    }

    createClient.mutate({
      nombre: form.nombre,
      apellido: form.apellido,
      correo: form.correo,
      documento: form.documento,
      telefono: form.telefono,
      contrasena: form.contrasena,
      peso: form.peso,
      altura: form.altura,
      objetivo: form.objetivo,
      frecuenciaEntrenamiento: form.frecuenciaEntrenamiento,
      genero: form.genero,
      fechaNacimiento: form.fechaNacimiento || undefined,
      quiereInstructor: form.quiereInstructor,
      instructorId: form.quiereInstructor ? form.instructorId : undefined,
      planId: form.planId,
    })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Modulo clientes"
        title="Clientes"
        description="Busqueda, filtros, detalle y gestion de clientes conectada a los endpoints admin existentes."
        action={
          <button type="button" className={primaryButtonClass} onClick={openCreate}>
            <Plus size={17} />
            Nuevo cliente
          </button>
        }
      />

      <section className={panelClass}>
        <div className="grid gap-3 md:grid-cols-[1fr_220px]">
          <label className="relative">
            <Search className="absolute left-3 top-3 text-slate-400" size={18} />
            <input className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100" value={search} placeholder="Buscar por nombre, correo o documento" onChange={(event) => setSearch(event.target.value)} />
          </label>
          <select className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none focus:border-violet-400 focus:ring-4 focus:ring-violet-100" value={status} onChange={(event) => setStatus(event.target.value as typeof status)}>
            <option value="ALL">Todos los estados</option>
            <option value="ACTIVE">Activos</option>
            <option value="INACTIVE">Inactivos</option>
          </select>
        </div>

        {clientes.isLoading ? <div className="mt-4"><LoadingPanel /></div> : null}

        <div className="mt-5 overflow-x-auto">
          {filtered.length ? (
            <table className="w-full min-w-[840px] text-left text-sm">
              <thead className="text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="py-3">Cliente</th>
                  <th>Correo</th>
                  <th>Documento</th>
                  <th>Objetivo</th>
                  <th>Estado</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.map((cliente) => (
                  <tr key={cliente.id} className="text-slate-700">
                    <td className="py-3 font-bold text-slate-950">{cliente.nombre} {cliente.apellido}</td>
                    <td>{cliente.correo}</td>
                    <td>{cliente.documento ?? '-'}</td>
                    <td>{cliente.objetivo ?? '-'}</td>
                    <td><StatusBadge value={isUserActive(cliente)} /></td>
                    <td>
                      <div className="flex justify-end gap-2">
                        <button className={secondaryButtonClass} type="button" onClick={() => setDetail(cliente)}><Eye size={16} /></button>
                        <button className={secondaryButtonClass} type="button" onClick={() => openEdit(cliente)}><Pencil size={16} /></button>
                        <button
                          className={dangerButtonClass}
                          type="button"
                          disabled={!isUserActive(cliente) || deactivateClient.isPending}
                          onClick={async () => {
                            if (await confirmAction('Desactivar cliente', 'El cliente no podra ingresar mientras este inactivo.')) deactivateClient.mutate(cliente.id)
                          }}
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyPanel icon={<Users size={22} />} title="No hay clientes para mostrar" description="Ajusta los filtros o crea un nuevo cliente." />
          )}
        </div>
      </section>

      {formOpen ? (
        <Modal title={editing ? 'Editar cliente' : 'Crear cliente'} onClose={() => setFormOpen(false)}>
          <form className="grid gap-4 sm:grid-cols-2" onSubmit={submit}>
            <TextField label="Nombre" value={form.nombre} error={errors.nombre} required onChange={(nombre) => setForm({ ...form, nombre })} />
            <TextField label="Apellido" value={form.apellido} error={errors.apellido} required onChange={(apellido) => setForm({ ...form, apellido })} />
            <TextField label="Correo" type="email" value={form.correo} error={errors.correo} required onChange={(correo) => setForm({ ...form, correo })} />
            <TextField label="Documento" value={form.documento} error={errors.documento} required onChange={(documento) => setForm({ ...form, documento: onlyDigits(documento) })} />
            <TextField label="Telefono" value={form.telefono ?? ''} error={errors.telefono} onChange={(telefono) => setForm({ ...form, telefono: onlyDigits(telefono) })} />
            <TextField label="Frecuencia" value={form.frecuenciaEntrenamiento ?? ''} onChange={(frecuenciaEntrenamiento) => setForm({ ...form, frecuenciaEntrenamiento })} />
            <TextField label="Peso kg" type="number" value={form.peso?.toString() ?? ''} onChange={(peso) => setForm({ ...form, peso: peso ? Number(peso) : undefined })} />
            <TextField label="Altura m" type="number" value={form.altura?.toString() ?? ''} onChange={(altura) => setForm({ ...form, altura: altura ? Number(altura) : undefined })} />
            <TextField label="Fecha nacimiento" type="date" value={form.fechaNacimiento ?? ''} onChange={(fechaNacimiento) => setForm({ ...form, fechaNacimiento })} />
            <SelectField label="Objetivo" value={form.objetivo} onChange={(objetivo) => setForm({ ...form, objetivo: objetivo as FitnessGoal })} options={[
              { value: 'PERDER_PESO', label: 'Perder peso' },
              { value: 'AUMENTAR_MASA', label: 'Aumentar masa' },
              { value: 'DEFINICION', label: 'Definicion' },
              { value: 'RECOMPOSICION', label: 'Recomposicion' },
              { value: 'MANTENIMIENTO', label: 'Mantenimiento' },
            ]} />
            <SelectField label="Genero" value={form.genero ?? 'OTHER'} onChange={(genero) => setForm({ ...form, genero })} options={[
              { value: 'MALE', label: 'Masculino' },
              { value: 'FEMALE', label: 'Femenino' },
              { value: 'OTHER', label: 'Otro' },
            ]} />
            <label className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm font-semibold text-slate-700">
              <input type="checkbox" checked={Boolean(form.quiereInstructor)} onChange={(event) => setForm({ ...form, quiereInstructor: event.target.checked, instructorId: event.target.checked ? form.instructorId : undefined })} />
              Requiere instructor
            </label>
            {form.quiereInstructor ? (
              <SelectField label="Instructor" value={String(form.instructorId ?? '')} onChange={(instructorId) => setForm({ ...form, instructorId: instructorId ? Number(instructorId) : undefined })} options={[
                { value: '', label: 'Asignar automaticamente' },
                ...safeInstructores.map((instructor) => ({ value: String(instructor.id), label: `${instructor.nombre} ${instructor.apellido} - ${instructor.especialidad ?? 'Disponible'}` })),
              ]} />
            ) : null}
            {!editing ? (
              <SelectField label="Plan inicial" value={String(form.planId ?? '')} onChange={(planId) => setForm({ ...form, planId: planId ? Number(planId) : undefined })} options={[
                { value: '', label: 'Sin plan inicial' },
                ...safePlanes.map((plan) => ({ value: String(plan.id), label: `${plan.nombre} (${plan.duracionMeses} mes(es))` })),
              ]} />
            ) : null}
            {!editing ? (
              <>
                <TextField label="Contrasena" type="password" value={form.contrasena} error={errors.contrasena} required onChange={(contrasena) => setForm({ ...form, contrasena })} />
                <TextField label="Confirmar contrasena" type="password" value={form.confirmPassword} error={errors.confirmPassword} required onChange={(confirmPassword) => setForm({ ...form, confirmPassword })} />
              </>
            ) : null}
            <div className="sm:col-span-2 flex justify-end gap-3 pt-2">
              <button type="button" className={secondaryButtonClass} onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className={primaryButtonClass} disabled={createClient.isPending || updateClient.isPending}>
                {editing ? 'Guardar cambios' : 'Crear cliente'}
              </button>
            </div>
          </form>
        </Modal>
      ) : null}

      {detail ? (
        <Modal title="Detalle del cliente" onClose={() => setDetail(null)}>
          <div className="grid gap-3 text-sm text-slate-700 sm:grid-cols-2">
            <Info label="Nombre" value={`${detail.nombre} ${detail.apellido}`} />
            <Info label="Correo" value={detail.correo} />
            <Info label="Documento" value={detail.documento} />
            <Info label="Telefono" value={detail.telefono} />
            <Info label="Objetivo" value={detail.objetivo} />
            <Info label="IMC" value={detail.imc?.toString()} />
            <Info label="Instructor" value={detail.instructor ? `${detail.instructor.nombre} ${detail.instructor.apellido}` : undefined} />
            <Info label="Membresia" value={detail.membresia?.plan?.nombre} />
          </div>
        </Modal>
      ) : null}
    </div>
  )
}

function Info({ label, value }: { label: string; value?: string }) {
  return (
    <div className="rounded-xl bg-slate-50 p-3">
      <div className="text-xs font-bold uppercase text-slate-400">{label}</div>
      <div className="mt-1 font-semibold text-slate-900">{value || '-'}</div>
    </div>
  )
}

function getErrorMessage(error: unknown) {
  const maybe = error as { response?: { data?: { error?: string; message?: string } } }
  return maybe.response?.data?.error ?? maybe.response?.data?.message ?? 'Revisa los datos e intenta de nuevo.'
}

function validateClientForm(form: ClientForm, editing: boolean, clientes: AdminClient[], editingId?: number) {
  const errors: ClientFormErrors = {}
  const namePattern = /^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/
  if (!namePattern.test(form.nombre.trim())) errors.nombre = 'Solo letras y espacios.'
  if (!namePattern.test(form.apellido.trim())) errors.apellido = 'Solo letras y espacios.'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.correo)) errors.correo = 'Correo invalido.'
  if (!/^[0-9]{5,20}$/.test(form.documento)) errors.documento = 'Solo numeros, entre 5 y 20 digitos.'
  if (form.telefono && !/^[0-9]{7,15}$/.test(form.telefono)) errors.telefono = 'Solo numeros, entre 7 y 15 digitos.'
  if (!editing && form.contrasena.length < 6) errors.contrasena = 'Minimo 6 caracteres.'
  if (!editing && form.contrasena !== form.confirmPassword) errors.confirmPassword = 'Las contrasenas no coinciden.'
  const duplicate = clientes.find((cliente) => cliente.id !== editingId && (cliente.documento === form.documento || cliente.correo.toLowerCase() === form.correo.toLowerCase()))
  if (duplicate?.documento === form.documento) errors.documento = 'Ya existe un usuario con este documento.'
  if (duplicate?.correo.toLowerCase() === form.correo.toLowerCase()) errors.correo = 'Ya existe un usuario con este correo.'
  return errors
}

function onlyDigits(value: string) {
  return value.replace(/\D/g, '')
}
