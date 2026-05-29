import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { CheckCircle2, CreditCard, Plus, Search, XCircle } from 'lucide-react'
import { adminApi } from '../../api/adminApi'
import type { AdminClient, AdminInstructor, AdminMembership, AdminPayment } from '../../api/adminApi'
import { EmptyPanel, LoadingPanel, Modal, PageHeader, SelectField, StatusBadge, TextField, confirmAction, dangerButtonClass, formatDate, formatMoney, normalizeList, panelClass, primaryButtonClass, secondaryButtonClass, showError, showSuccess } from './AdminUI'

export default function AdminPagosPage() {
  const queryClient = useQueryClient()
  const [search, setSearch] = useState('')
  const [mode, setMode] = useState<'CLIENTES' | 'INSTRUCTORES'>('CLIENTES')
  const [paymentFilter, setPaymentFilter] = useState<'PENDIENTES' | 'TODOS'>('PENDIENTES')
  const [formOpen, setFormOpen] = useState(false)
  const [paymentForm, setPaymentForm] = useState({ clienteId: '', membresiaId: '', metodoPago: 'CASH', monto: '', observaciones: '' })

  const query = useQuery({
    queryKey: ['admin', 'pagos', paymentFilter],
    queryFn: () => (paymentFilter === 'PENDIENTES' ? adminApi.pagosPendientes() : adminApi.pagos()).then((r) => r.data),
    refetchInterval: 12000,
  })
  const clientes = useQuery({ queryKey: ['admin', 'clientes'], queryFn: () => adminApi.clientes().then((r) => r.data), refetchInterval: 12000 })
  const instructores = useQuery({ queryKey: ['admin', 'instructores'], queryFn: () => adminApi.instructores().then((r) => r.data), refetchInterval: 12000 })
  const membresias = useQuery({ queryKey: ['admin', 'membresias'], queryFn: () => adminApi.membresias().then((r) => r.data), refetchInterval: 12000 })

  const pagos = normalizeList<AdminPayment>(query.data)
  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase()
    return pagos.filter((pago) => {
      const cliente = pago.cliente ? `${pago.cliente.nombre} ${pago.cliente.apellido} ${pago.cliente.correo}` : ''
      return `${pago.id} ${pago.referencia ?? ''} ${pago.estado} ${cliente}`.toLowerCase().includes(term)
    })
  }, [pagos, search])
  const safeClientes = normalizeList<AdminClient>(clientes.data)
  const safeInstructores = normalizeList<AdminInstructor>(instructores.data)
  const membershipRows: AdminMembership[] = [
    ...normalizeList<AdminMembership>(membresias.data?.activas),
    ...normalizeList<AdminMembership>(membresias.data?.pendientes),
    ...normalizeList<AdminMembership>(membresias.data?.vencidas),
    ...normalizeList<AdminMembership>(membresias.data?.suspendidas),
  ]
  const instructorPayments = safeInstructores.map((instructor) => ({
    id: instructor.id,
    nombre: `${instructor.nombre} ${instructor.apellido}`,
    especialidad: instructor.especialidad ?? 'General',
    contrato: instructor.contractType ?? 'PART_TIME',
    monto: Number(instructor.salario ?? calculateInstructorPay(instructor.contractType, instructor.especialidad)),
    estado: instructor.disponible === false ? 'SUSPENDIDO' : 'PENDIENTE',
  }))

  const validatePayment = useMutation({
    mutationFn: adminApi.validarPago,
    onSuccess: () => {
      showSuccess('Pago validado')
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: () => showError('No se pudo validar el pago.'),
  })

  const cancelPayment = useMutation({
    mutationFn: adminApi.anularPago,
    onSuccess: () => {
      showSuccess('Pago anulado')
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: () => showError('No se pudo anular el pago.'),
  })

  const createPayment = useMutation({
    mutationFn: adminApi.crearPago,
    onSuccess: () => {
      showSuccess('Pago creado')
      setFormOpen(false)
      setPaymentForm({ clienteId: '', membresiaId: '', metodoPago: 'CASH', monto: '', observaciones: '' })
      void queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
    onError: (error) => showError(getErrorMessage(error)),
  })

  return (
    <div className="space-y-6">
      <PageHeader eyebrow="Modulo pagos" title="Pagos" description="Validacion y anulacion de pagos con estados claros y confirmaciones antes de acciones sensibles." action={<button type="button" className={primaryButtonClass} onClick={() => setFormOpen(true)}><Plus size={17} />Registrar pago</button>} />

      <section className={panelClass}>
        <div className="mb-4 flex flex-wrap gap-2">
          <button type="button" className={mode === 'CLIENTES' ? primaryButtonClass : secondaryButtonClass} onClick={() => setMode('CLIENTES')}>Pagos clientes</button>
          <button type="button" className={mode === 'INSTRUCTORES' ? primaryButtonClass : secondaryButtonClass} onClick={() => setMode('INSTRUCTORES')}>Pagos instructores</button>
        </div>

        {mode === 'CLIENTES' ? (
        <>
        <div className="grid gap-3 md:grid-cols-[1fr_220px]">
          <label className="relative">
            <Search className="absolute left-3 top-3 text-slate-400" size={18} />
            <input className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100" value={search} placeholder="Buscar pago, referencia o cliente" onChange={(event) => setSearch(event.target.value)} />
          </label>
          <select className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none focus:border-violet-400 focus:ring-4 focus:ring-violet-100" value={paymentFilter} onChange={(event) => setPaymentFilter(event.target.value as typeof paymentFilter)}>
            <option value="PENDIENTES">Pendientes</option>
            <option value="TODOS">Todos</option>
          </select>
        </div>

        {query.isLoading ? <div className="mt-4"><LoadingPanel /></div> : null}

        <div className="mt-5 overflow-x-auto">
          {filtered.length ? (
            <table className="w-full min-w-[840px] text-left text-sm">
              <thead className="text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="py-3">Pago</th>
                  <th>Cliente</th>
                  <th>Monto</th>
                  <th>Fecha</th>
                  <th>Estado</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.map((pago) => (
                  <tr key={pago.id} className="text-slate-700">
                    <td className="py-3 font-bold text-slate-950">#{pago.id}<div className="text-xs font-medium text-slate-400">{pago.referencia ?? 'Sin referencia'}</div></td>
                    <td>{pago.cliente ? `${pago.cliente.nombre} ${pago.cliente.apellido}` : '-'}</td>
                    <td className="font-bold text-slate-950">{formatMoney(pago.monto)}</td>
                    <td>{formatDate(pago.fechaPago)}</td>
                    <td><StatusBadge value={pago.estado} /></td>
                    <td>
                      <div className="flex justify-end gap-2">
                        <button className={primaryButtonClass} type="button" disabled={pago.estado !== 'PENDIENTE' || validatePayment.isPending} onClick={async () => {
                          if (await confirmAction('Validar pago', 'Se activara la membresia asociada si aplica.')) validatePayment.mutate(pago.id)
                        }}>
                          <CheckCircle2 size={16} />
                          Validar
                        </button>
                        <button className={dangerButtonClass} type="button" disabled={pago.estado !== 'PENDIENTE' || cancelPayment.isPending} onClick={async () => {
                          if (await confirmAction('Anular pago', 'Esta accion cambiara el estado del pago.')) cancelPayment.mutate(pago.id)
                        }}>
                          <XCircle size={16} />
                          Anular
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <EmptyPanel icon={<CreditCard size={22} />} title="No hay pagos para mostrar" description="Cuando existan pagos pendientes apareceran en esta vista." />
          )}
        </div>
        </>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left text-sm">
              <thead className="text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="py-3">Instructor</th>
                  <th>Especialidad</th>
                  <th>Contrato</th>
                  <th>Periodicidad</th>
                  <th>Monto</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {instructorPayments.map((payment) => (
                  <tr key={payment.id} className="text-slate-700">
                    <td className="py-3 font-bold text-slate-950">{payment.nombre}</td>
                    <td>{payment.especialidad}</td>
                    <td>{payment.contrato}</td>
                    <td>Mensual</td>
                    <td className="font-bold text-slate-950">{formatMoney(payment.monto)}</td>
                    <td><StatusBadge value={payment.estado} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
      {formOpen ? (
        <Modal title="Registrar pago de cliente" onClose={() => setFormOpen(false)}>
          <form className="grid gap-4 sm:grid-cols-2" onSubmit={(event) => {
            event.preventDefault()
            if (!paymentForm.clienteId) {
              showError('Selecciona un cliente.')
              return
            }
            createPayment.mutate({
              clienteId: Number(paymentForm.clienteId),
              membresiaId: paymentForm.membresiaId ? Number(paymentForm.membresiaId) : undefined,
              metodoPago: paymentForm.metodoPago as 'CASH',
              monto: paymentForm.monto ? Number(paymentForm.monto) : undefined,
              observaciones: paymentForm.observaciones,
            })
          }}>
            <SelectField label="Cliente" value={paymentForm.clienteId} onChange={(clienteId) => setPaymentForm({ ...paymentForm, clienteId })} options={[
              { value: '', label: 'Selecciona cliente' },
              ...safeClientes.map((cliente) => ({ value: String(cliente.id), label: `${cliente.nombre} ${cliente.apellido}` })),
            ]} />
            <SelectField label="Membresia" value={paymentForm.membresiaId} onChange={(membresiaId) => setPaymentForm({ ...paymentForm, membresiaId })} options={[
              { value: '', label: 'Sin membresia asociada' },
              ...membershipRows.filter((m) => !paymentForm.clienteId || m.cliente?.id === Number(paymentForm.clienteId)).map((m) => ({ value: String(m.id), label: `${m.plan?.nombre ?? 'Membresia'} - ${m.estado}` })),
            ]} />
            <SelectField label="Metodo" value={paymentForm.metodoPago} onChange={(metodoPago) => setPaymentForm({ ...paymentForm, metodoPago })} options={[
              { value: 'CASH', label: 'Efectivo' },
              { value: 'TRANSFER', label: 'Transferencia' },
              { value: 'CARD', label: 'Tarjeta' },
              { value: 'NEQUI', label: 'Nequi' },
              { value: 'DAVIPLATA', label: 'Daviplata' },
            ]} />
            <TextField label="Monto" type="number" value={paymentForm.monto} onChange={(monto) => setPaymentForm({ ...paymentForm, monto })} />
            <TextField label="Observaciones" value={paymentForm.observaciones} onChange={(observaciones) => setPaymentForm({ ...paymentForm, observaciones })} />
            <div className="sm:col-span-2 flex justify-end gap-3 pt-2">
              <button type="button" className={secondaryButtonClass} onClick={() => setFormOpen(false)}>Cancelar</button>
              <button type="submit" className={primaryButtonClass} disabled={createPayment.isPending}>Crear pago</button>
            </div>
          </form>
        </Modal>
      ) : null}
    </div>
  )
}

function calculateInstructorPay(contractType?: string, specialty?: string) {
  const base = contractType === 'FULL_TIME' ? 2400000 : contractType === 'TEMPORARY' ? 900000 : 1400000
  const bonus = specialty?.toLowerCase().includes('rehabilit') || specialty?.toLowerCase().includes('power') ? 250000 : 0
  return base + bonus
}

function getErrorMessage(error: unknown) {
  const maybe = error as { response?: { data?: { error?: string; message?: string } } }
  return maybe.response?.data?.error ?? maybe.response?.data?.message ?? 'No se pudo crear el pago.'
}
