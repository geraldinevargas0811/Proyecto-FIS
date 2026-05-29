import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { PauseCircle, Plus, RefreshCw, ShieldCheck } from 'lucide-react'
import { adminApi } from '../../api/adminApi'
import type { AdminClient, AdminMembership, AdminPlan } from '../../api/adminApi'
import { EmptyPanel, LoadingPanel, Modal, PageHeader, SelectField, StatusBadge, confirmAction, dangerButtonClass, formatDate, normalizeList, panelClass, primaryButtonClass, secondaryButtonClass, showError, showSuccess } from './AdminUI'
import { useState } from 'react'

export default function AdminMembresiasPage() {
  const queryClient = useQueryClient()
  const [formOpen, setFormOpen] = useState(false)
  const [clienteId, setClienteId] = useState('')
  const [planId, setPlanId] = useState('')
  const membresias = useQuery({ queryKey: ['admin', 'membresias'], queryFn: () => adminApi.membresias().then((r) => r.data), refetchInterval: 12000 })
  const clientes = useQuery({ queryKey: ['admin', 'clientes'], queryFn: () => adminApi.clientes().then((r) => r.data), refetchInterval: 12000 })
  const planes = useQuery({ queryKey: ['admin', 'planes'], queryFn: () => adminApi.planes().then((r) => r.data), refetchInterval: 12000 })
  const safeClientes = normalizeList<AdminClient>(clientes.data)
  const safePlanes = normalizeList<AdminPlan>(planes.data)
  const rows: AdminMembership[] = [
    ...normalizeList<AdminMembership>(membresias.data?.activas),
    ...normalizeList<AdminMembership>(membresias.data?.pendientes),
    ...normalizeList<AdminMembership>(membresias.data?.vencidas),
    ...normalizeList<AdminMembership>(membresias.data?.suspendidas),
  ]

  const suspend = useMutation({
    mutationFn: adminApi.suspenderMembresia,
    onSuccess: () => {
      showSuccess('Membresia suspendida')
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: () => showError('No se pudo suspender la membresia.'),
  })

  const create = useMutation({
    mutationFn: adminApi.crearMembresia,
    onSuccess: () => {
      showSuccess('Membresia creada')
      setFormOpen(false)
      setClienteId('')
      setPlanId('')
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: () => showError('No se pudo crear la membresia.'),
  })

  const renew = useMutation({
    mutationFn: adminApi.renovarMembresia,
    onSuccess: () => {
      showSuccess('Membresia renovada')
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: () => showError('No se pudo renovar la membresia.'),
  })

  return (
    <div className="space-y-6">
      <PageHeader eyebrow="Modulo membresias" title="Membresias" description="Vista limpia de membresias activas, pendientes y vencidas con estado y acciones de suspension." action={<button type="button" className={primaryButtonClass} onClick={() => setFormOpen(true)}><Plus size={17} />Nueva membresia</button>} />

      <section className={panelClass}>
        {membresias.isLoading ? <LoadingPanel /> : null}
        <div className="grid gap-4 md:grid-cols-3">
          <Summary title="Activas" value={normalizeList<AdminMembership>(membresias.data?.activas).length} />
          <Summary title="Pendientes" value={normalizeList<AdminMembership>(membresias.data?.pendientes).length} />
          <Summary title="Vencidas" value={normalizeList<AdminMembership>(membresias.data?.vencidas).length} />
        </div>

        <div className="mt-5 overflow-x-auto">
          {rows.length ? (
            <table className="w-full min-w-[820px] text-left text-sm">
              <thead className="text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="py-3">Cliente</th>
                  <th>Plan</th>
                  <th>Inicio</th>
                  <th>Vence</th>
                  <th>Dias</th>
                  <th>Estado</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {rows.map((membresia) => (
                  <tr key={membresia.id} className="text-slate-700">
                    <td className="py-3 font-bold text-slate-950">{membresia.cliente ? `${membresia.cliente.nombre} ${membresia.cliente.apellido}` : '-'}</td>
                    <td>{membresia.plan?.nombre ?? '-'}</td>
                    <td>{formatDate(membresia.fechaInicio)}</td>
                    <td>{formatDate(membresia.fechaVencimiento)}</td>
                    <td>{membresia.diasRestantes ?? '-'}</td>
                    <td><StatusBadge value={membresia.estado} /></td>
                    <td className="text-right">
                      <div className="flex justify-end gap-2">
                        <button className={secondaryButtonClass} type="button" disabled={renew.isPending} onClick={async () => {
                          if (await confirmAction('Renovar membresia', 'Se calculara una nueva fecha de vencimiento segun el plan.')) renew.mutate(membresia.id)
                        }}>
                          <RefreshCw size={16} />
                          Renovar
                        </button>
                        <button className={dangerButtonClass} type="button" disabled={membresia.estado !== 'ACTIVA' || suspend.isPending} onClick={async () => {
                          if (await confirmAction('Suspender membresia', 'La membresia quedara suspendida.')) suspend.mutate(membresia.id)
                        }}>
                          <PauseCircle size={16} />
                          Suspender
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyPanel icon={<ShieldCheck size={22} />} title="Sin membresias" description="No hay membresias registradas por el momento." />
          )}
        </div>
      </section>
      {formOpen ? (
        <Modal title="Crear membresia" onClose={() => setFormOpen(false)}>
          <form className="grid gap-4 sm:grid-cols-2" onSubmit={(event) => {
            event.preventDefault()
            if (!clienteId || !planId) {
              showError('Selecciona cliente y plan.')
              return
            }
            create.mutate({ clienteId: Number(clienteId), planId: Number(planId) })
          }}>
            <SelectField label="Cliente" value={clienteId} onChange={setClienteId} options={[
              { value: '', label: 'Selecciona cliente' },
              ...safeClientes.map((cliente) => ({ value: String(cliente.id), label: `${cliente.nombre} ${cliente.apellido}` })),
            ]} />
            <SelectField label="Plan" value={planId} onChange={setPlanId} options={[
              { value: '', label: 'Selecciona plan' },
              ...safePlanes.map((plan) => ({ value: String(plan.id), label: `${plan.nombre} - ${plan.duracionMeses} mes(es)` })),
            ]} />
            <div className="sm:col-span-2 flex justify-end gap-3 pt-2">
              <button type="button" className={secondaryButtonClass} onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className={primaryButtonClass} disabled={create.isPending}>Crear membresia</button>
            </div>
          </form>
        </Modal>
      ) : null}
    </div>
  )
}

function Summary({ title, value }: { title: string; value: number }) {
  return (
    <div className="rounded-2xl bg-gradient-to-br from-slate-50 to-cyan-50 p-4">
      <div className="text-sm font-bold text-slate-500">{title}</div>
      <div className="mt-2 text-3xl font-black text-slate-950">{value}</div>
    </div>
  )
}
