import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Check, Crown, Plus } from 'lucide-react'
import { useState } from 'react'
import { adminApi } from '../../api/adminApi'
import type { AdminPlan } from '../../api/adminApi'
import { EmptyPanel, LoadingPanel, Modal, PageHeader, TextField, StatusBadge, formatMoney, normalizeList, primaryButtonClass, secondaryButtonClass, showError, showSuccess } from './AdminUI'

const emptyPlan = { nombre: '', descripcion: '', duracionMeses: 1, precio: 0, tipo: 'mensual', beneficios: '' }

export default function AdminPlanesPage() {
  const queryClient = useQueryClient()
  const [formOpen, setFormOpen] = useState(false)
  const [form, setForm] = useState(emptyPlan)
  const planes = useQuery({ queryKey: ['admin', 'planes'], queryFn: () => adminApi.planes().then((r) => r.data), refetchInterval: 12000 })
  const safePlanes = normalizeList<AdminPlan>(planes.data)
  const createPlan = useMutation({
    mutationFn: adminApi.crearPlan,
    onSuccess: () => {
      showSuccess('Plan creado')
      setForm(emptyPlan)
      setFormOpen(false)
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  return (
    <div className="space-y-6">
      <PageHeader eyebrow="Modulo planes" title="Planes" description="Catalogo conectado a GET /api/admin/planes con precio, duracion y beneficios en tarjetas modernas." action={<button type="button" className={primaryButtonClass} onClick={() => setFormOpen(true)}><Plus size={17} />Crear plan</button>} />

      {planes.isLoading ? <LoadingPanel /> : null}

      {safePlanes.length ? (
        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          {safePlanes.map((plan) => (
            <article key={plan.id} className="group rounded-3xl border border-white/70 bg-white/80 p-6 shadow-[0_18px_60px_rgba(15,23,42,0.08)] backdrop-blur-xl transition hover:-translate-y-1 hover:shadow-[0_22px_80px_rgba(124,58,237,0.16)]">
              <div className="flex items-start justify-between gap-3">
                <div className="rounded-2xl bg-gradient-to-br from-cyan-500 to-violet-500 p-3 text-white shadow-lg">
                  <Crown size={22} />
                </div>
                <StatusBadge value={plan.activo} />
              </div>
              <h2 className="mt-5 text-2xl font-black text-slate-950">{plan.nombre}</h2>
              <p className="mt-2 min-h-12 text-sm leading-6 text-slate-500">{plan.descripcion ?? 'Plan de entrenamiento y acceso premium.'}</p>
              <div className="mt-5 flex items-end gap-2">
                <span className="text-3xl font-black text-slate-950">{formatMoney(plan.precio)}</span>
                <span className="pb-1 text-sm font-semibold text-slate-500">/{plan.duracionMeses} mes(es)</span>
              </div>
              <div className="mt-5 space-y-2">
                {splitBenefits(plan.beneficios).map((benefit) => (
                  <div key={benefit} className="flex items-center gap-2 text-sm font-semibold text-slate-700">
                    <span className="rounded-full bg-emerald-50 p-1 text-emerald-600"><Check size={14} /></span>
                    {benefit}
                  </div>
                ))}
              </div>
            </article>
          ))}
        </div>
      ) : (
        <EmptyPanel icon={<Crown size={22} />} title="Sin planes disponibles" description="El backend no devolvio planes activos para mostrar." />
      )}

      {formOpen ? (
        <Modal title="Crear plan" onClose={() => setFormOpen(false)}>
          <form className="grid gap-4 sm:grid-cols-2" onSubmit={(event) => {
            event.preventDefault()
            if (!form.nombre.trim() || form.duracionMeses <= 0 || form.precio < 0) {
              showError('Revisa nombre, duracion y precio.')
              return
            }
            createPlan.mutate(form)
          }}>
            <TextField label="Nombre" value={form.nombre} required onChange={(nombre) => setForm({ ...form, nombre })} />
            <TextField label="Tipo" value={form.tipo} onChange={(tipo) => setForm({ ...form, tipo })} />
            <TextField label="Duracion meses" type="number" value={String(form.duracionMeses)} required onChange={(duracionMeses) => setForm({ ...form, duracionMeses: Number(duracionMeses || 1) })} />
            <TextField label="Precio" type="number" value={String(form.precio)} required onChange={(precio) => setForm({ ...form, precio: Number(precio || 0) })} />
            <TextField label="Descripcion" value={form.descripcion} onChange={(descripcion) => setForm({ ...form, descripcion })} />
            <TextField label="Beneficios" value={form.beneficios} onChange={(beneficios) => setForm({ ...form, beneficios })} />
            <div className="sm:col-span-2 flex justify-end gap-3 pt-2">
              <button type="button" className={secondaryButtonClass} onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className={primaryButtonClass} disabled={createPlan.isPending}>Crear plan</button>
            </div>
          </form>
        </Modal>
      ) : null}
    </div>
  )
}

function splitBenefits(value?: string) {
  if (!value) return ['Acceso al gimnasio', 'Seguimiento de progreso', 'Soporte del equipo']
  return value.split(/[,\n;]/).map((item) => item.trim()).filter(Boolean)
}

function getErrorMessage(error: unknown) {
  const maybe = error as { response?: { data?: { error?: string; message?: string } } }
  return maybe.response?.data?.error ?? maybe.response?.data?.message ?? 'No se pudo crear el plan.'
}
