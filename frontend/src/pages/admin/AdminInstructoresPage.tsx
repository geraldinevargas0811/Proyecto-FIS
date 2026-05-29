import { useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Dumbbell, Eye, Pencil, Plus, Search, Trash2 } from 'lucide-react'
import { adminApi } from '../../api/adminApi'
import type { AdminInstructor, CreateInstructorPayload, UpdateInstructorPayload } from '../../api/adminApi'
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

type InstructorForm = CreateInstructorPayload & { confirmPassword: string; startTime: string; endTime: string }
type InstructorFormErrors = Partial<Record<keyof InstructorForm, string>>

const emptyForm: InstructorForm = {
  nombre: '',
  apellido: '',
  correo: '',
  contrasena: '',
  confirmPassword: '',
  documento: '',
  telefono: '',
  especialidad: 'Funcional',
  certificaciones: '',
  anosExperiencia: 0,
  salario: undefined,
  horarioTrabajo: '',
  startTime: '08:00',
  endTime: '16:00',
  contractType: 'PART_TIME',
}

export default function AdminInstructoresPage() {
  const queryClient = useQueryClient()
  const [search, setSearch] = useState('')
  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<AdminInstructor | null>(null)
  const [detail, setDetail] = useState<AdminInstructor | null>(null)
  const [form, setForm] = useState<InstructorForm>(emptyForm)
  const [errors, setErrors] = useState<InstructorFormErrors>({})

  const instructores = useQuery({ queryKey: ['admin', 'instructores'], queryFn: () => adminApi.instructores().then((r) => r.data), refetchInterval: 12000 })
  const safeInstructores = normalizeList<AdminInstructor>(instructores.data)

  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase()
    return safeInstructores.filter((instructor) => `${instructor.nombre} ${instructor.apellido} ${instructor.correo} ${instructor.especialidad ?? ''}`.toLowerCase().includes(term))
  }, [safeInstructores, search])

  const createInstructor = useMutation({
    mutationFn: adminApi.crearInstructor,
    onSuccess: () => {
      showSuccess('Instructor creado')
      setFormOpen(false)
      setForm(emptyForm)
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  const updateInstructor = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: UpdateInstructorPayload }) => adminApi.actualizarInstructor(id, payload),
    onSuccess: () => {
      showSuccess('Instructor actualizado')
      setFormOpen(false)
      setEditing(null)
      setForm(emptyForm)
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  const deactivateInstructor = useMutation({
    mutationFn: adminApi.desactivarInstructor,
    onSuccess: () => {
      showSuccess('Instructor desactivado')
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

  const openEdit = (instructor: AdminInstructor) => {
    const { startTime, endTime } = parseSchedule(instructor.horarioTrabajo)
    setEditing(instructor)
    setForm({
      ...emptyForm,
      nombre: instructor.nombre,
      apellido: instructor.apellido,
      correo: instructor.correo,
      documento: instructor.documento ?? '',
      telefono: instructor.telefono ?? '',
      especialidad: instructor.especialidad ?? 'Funcional',
      certificaciones: instructor.certificaciones ?? '',
      anosExperiencia: instructor.anosExperiencia ?? 0,
      salario: instructor.salario,
      horarioTrabajo: instructor.horarioTrabajo ?? '',
      startTime,
      endTime,
      contractType: instructor.contractType ?? 'PART_TIME',
    })
    setErrors({})
    setFormOpen(true)
  }

  const submit = (event: FormEvent) => {
    event.preventDefault()
    const nextErrors = validateInstructorForm(form, Boolean(editing), safeInstructores, editing?.id)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length) {
      showError(Object.values(nextErrors)[0] ?? 'Revisa los campos del formulario.')
      return
    }

    const horarioTrabajo = `${form.startTime} - ${form.endTime}`

    if (editing) {
      updateInstructor.mutate({
        id: editing.id,
        payload: {
          nombre: form.nombre,
          apellido: form.apellido,
          correo: form.correo,
          documento: form.documento,
          telefono: form.telefono,
          especialidad: form.especialidad,
          certificaciones: form.certificaciones,
          anosExperiencia: form.anosExperiencia,
          salario: form.salario,
          horarioTrabajo,
          contractType: form.contractType,
        },
      })
      return
    }

    createInstructor.mutate({
      nombre: form.nombre,
      apellido: form.apellido,
      correo: form.correo,
      documento: form.documento,
      telefono: form.telefono,
      contrasena: form.contrasena,
      especialidad: form.especialidad,
      certificaciones: form.certificaciones,
      anosExperiencia: form.anosExperiencia,
      salario: form.salario,
      horarioTrabajo,
      contractType: form.contractType,
    })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        eyebrow="Modulo instructores"
        title="Instructores"
        description="Gestion de entrenadores con especialidad flexible, disponibilidad, horarios por rango y formularios con password de alta."
        action={<button type="button" className={primaryButtonClass} onClick={openCreate}><Plus size={17} />Nuevo instructor</button>}
      />

      <section className={panelClass}>
        <label className="relative block">
          <Search className="absolute left-3 top-3 text-slate-400" size={18} />
          <input className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100" value={search} placeholder="Buscar por nombre, correo o especialidad" onChange={(event) => setSearch(event.target.value)} />
        </label>

        {instructores.isLoading ? <div className="mt-4"><LoadingPanel /></div> : null}

        <div className="mt-5 overflow-x-auto">
          {filtered.length ? (
            <table className="w-full min-w-[860px] text-left text-sm">
              <thead className="text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="py-3">Instructor</th>
                  <th>Correo</th>
                  <th>Especialidad</th>
                  <th>Horario</th>
                  <th>Estado</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.map((instructor) => (
                  <tr key={instructor.id} className="text-slate-700">
                    <td className="py-3 font-bold text-slate-950">{instructor.nombre} {instructor.apellido}</td>
                    <td>{instructor.correo}</td>
                    <td>{instructor.especialidad ?? '-'}</td>
                    <td>{instructor.horarioTrabajo ?? '-'}</td>
                    <td><StatusBadge value={isUserActive(instructor)} /></td>
                    <td>
                      <div className="flex justify-end gap-2">
                        <button className={secondaryButtonClass} type="button" onClick={() => setDetail(instructor)}><Eye size={16} /></button>
                        <button className={secondaryButtonClass} type="button" onClick={() => openEdit(instructor)}><Pencil size={16} /></button>
                        <button
                          className={dangerButtonClass}
                          type="button"
                          disabled={!isUserActive(instructor) || deactivateInstructor.isPending}
                          onClick={async () => {
                            if (await confirmAction('Desactivar instructor', 'El instructor no podra ingresar mientras este inactivo.')) deactivateInstructor.mutate(instructor.id)
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
            <EmptyPanel icon={<Dumbbell size={22} />} title="No hay instructores para mostrar" description="Crea un instructor o cambia el termino de busqueda." />
          )}
        </div>
      </section>

      {formOpen ? (
        <Modal title={editing ? 'Editar instructor' : 'Crear instructor'} onClose={() => setFormOpen(false)}>
          <form className="grid gap-4 sm:grid-cols-2" onSubmit={submit}>
            <TextField label="Nombre" value={form.nombre} error={errors.nombre} required onChange={(nombre) => setForm({ ...form, nombre })} />
            <TextField label="Apellido" value={form.apellido} error={errors.apellido} required onChange={(apellido) => setForm({ ...form, apellido })} />
            <TextField label="Correo" type="email" value={form.correo} error={errors.correo} required onChange={(correo) => setForm({ ...form, correo })} />
            <TextField label="Documento" value={form.documento} error={errors.documento} required onChange={(documento) => setForm({ ...form, documento: onlyDigits(documento) })} />
            <TextField label="Telefono" value={form.telefono ?? ''} error={errors.telefono} onChange={(telefono) => setForm({ ...form, telefono: onlyDigits(telefono) })} />
            <SelectField label="Especialidad" value={form.especialidad ?? 'Funcional'} onChange={(especialidad) => setForm({ ...form, especialidad })} options={[
              { value: 'Musculacion', label: 'Musculacion' },
              { value: 'Powerlifting', label: 'Powerlifting' },
              { value: 'Crossfit', label: 'Crossfit' },
              { value: 'Cardio', label: 'Cardio' },
              { value: 'Funcional', label: 'Funcional' },
              { value: 'Rehabilitacion', label: 'Rehabilitacion' },
              { value: 'Perdida de peso', label: 'Perdida de peso' },
              { value: 'Hipertrofia', label: 'Hipertrofia' },
            ]} />
            <TextField label="Certificaciones" value={form.certificaciones ?? ''} onChange={(certificaciones) => setForm({ ...form, certificaciones })} />
            <TextField label="Anos experiencia" type="number" value={form.anosExperiencia?.toString() ?? '0'} onChange={(anosExperiencia) => setForm({ ...form, anosExperiencia: Number(anosExperiencia || 0) })} />
            <TextField label="Salario" type="number" value={form.salario?.toString() ?? ''} onChange={(salario) => setForm({ ...form, salario: salario ? Number(salario) : undefined })} />
            <SelectField label="Contrato" value={form.contractType ?? 'PART_TIME'} onChange={(contractType) => setForm({ ...form, contractType })} options={[
              { value: 'FULL_TIME', label: 'Tiempo completo' },
              { value: 'PART_TIME', label: 'Medio tiempo' },
              { value: 'TEMPORARY', label: 'Temporal' },
            ]} />
            <TextField label="Inicio" type="time" value={form.startTime} onChange={(startTime) => setForm({ ...form, startTime })} />
            <TextField label="Fin" type="time" value={form.endTime} onChange={(endTime) => setForm({ ...form, endTime })} />
            {!editing ? (
              <>
                <TextField label="Contrasena" type="password" value={form.contrasena} error={errors.contrasena} required onChange={(contrasena) => setForm({ ...form, contrasena })} />
                <TextField label="Confirmar contrasena" type="password" value={form.confirmPassword} error={errors.confirmPassword} required onChange={(confirmPassword) => setForm({ ...form, confirmPassword })} />
              </>
            ) : null}
            <div className="sm:col-span-2 flex justify-end gap-3 pt-2">
              <button type="button" className={secondaryButtonClass} onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className={primaryButtonClass} disabled={createInstructor.isPending || updateInstructor.isPending}>
                {editing ? 'Guardar cambios' : 'Crear instructor'}
              </button>
            </div>
          </form>
        </Modal>
      ) : null}

      {detail ? (
        <Modal title="Detalle del instructor" onClose={() => setDetail(null)}>
          <div className="grid gap-3 text-sm text-slate-700 sm:grid-cols-2">
            <Info label="Nombre" value={`${detail.nombre} ${detail.apellido}`} />
            <Info label="Correo" value={detail.correo} />
            <Info label="Especialidad" value={detail.especialidad} />
            <Info label="Horario" value={detail.horarioTrabajo} />
            <Info label="Contrato" value={detail.contractType} />
            <Info label="Salario" value={detail.salario?.toString()} />
            <Info label="Clientes asignados" value={detail.cantidadClientes?.toString()} />
          </div>
        </Modal>
      ) : null}
    </div>
  )
}

function parseSchedule(value?: string) {
  const match = value?.match(/(\d{2}:\d{2}).*(\d{2}:\d{2})/)
  return { startTime: match?.[1] ?? '08:00', endTime: match?.[2] ?? '16:00' }
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

function validateInstructorForm(form: InstructorForm, editing: boolean, instructores: AdminInstructor[], editingId?: number) {
  const errors: InstructorFormErrors = {}
  const namePattern = /^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/
  if (!namePattern.test(form.nombre.trim())) errors.nombre = 'Solo letras y espacios.'
  if (!namePattern.test(form.apellido.trim())) errors.apellido = 'Solo letras y espacios.'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.correo)) errors.correo = 'Correo invalido.'
  if (!/^[0-9]{5,20}$/.test(form.documento)) errors.documento = 'Solo numeros, entre 5 y 20 digitos.'
  if (form.telefono && !/^[0-9]{7,15}$/.test(form.telefono)) errors.telefono = 'Solo numeros, entre 7 y 15 digitos.'
  if (!editing && form.contrasena.length < 6) errors.contrasena = 'Minimo 6 caracteres.'
  if (!editing && form.contrasena !== form.confirmPassword) errors.confirmPassword = 'Las contrasenas no coinciden.'
  const duplicate = instructores.find((instructor) => instructor.id !== editingId && (instructor.documento === form.documento || instructor.correo.toLowerCase() === form.correo.toLowerCase()))
  if (duplicate?.documento === form.documento) errors.documento = 'Ya existe un usuario con este documento.'
  if (duplicate?.correo.toLowerCase() === form.correo.toLowerCase()) errors.correo = 'Ya existe un usuario con este correo.'
  return errors
}

function onlyDigits(value: string) {
  return value.replace(/\D/g, '')
}
