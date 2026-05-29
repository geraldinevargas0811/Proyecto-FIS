import type { ReactNode } from 'react'
import Swal from 'sweetalert2'

export const panelClass =
  'rounded-2xl border border-white/60 bg-white/75 p-5 shadow-[0_18px_60px_rgba(15,23,42,0.08)] backdrop-blur-xl'

export const inputClass =
  'w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100'

export const selectClass =
  'w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-violet-400 focus:ring-4 focus:ring-violet-100'

export const primaryButtonClass =
  'inline-flex items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-violet-500 px-4 py-2.5 text-sm font-semibold text-white shadow-lg shadow-cyan-500/20 transition hover:-translate-y-0.5 hover:shadow-violet-500/25 disabled:translate-y-0 disabled:cursor-not-allowed disabled:opacity-60'

export const secondaryButtonClass =
  'inline-flex items-center justify-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:border-cyan-200 hover:bg-cyan-50'

export const dangerButtonClass =
  'inline-flex items-center justify-center gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-2.5 text-sm font-semibold text-rose-700 transition hover:bg-rose-100 disabled:cursor-not-allowed disabled:opacity-60'

export function PageHeader({
  eyebrow,
  title,
  description,
  action,
}: {
  eyebrow: string
  title: string
  description?: string
  action?: ReactNode
}) {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <div className="text-xs font-bold uppercase tracking-[0.18em] text-cyan-600">{eyebrow}</div>
        <h1 className="mt-2 text-3xl font-black text-slate-950 md:text-4xl">{title}</h1>
        {description ? <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">{description}</p> : null}
      </div>
      {action}
    </div>
  )
}

export function LoadingPanel({ label = 'Cargando datos...' }: { label?: string }) {
  return (
    <div className={panelClass}>
      <div className="flex items-center gap-3 text-sm font-semibold text-slate-600">
        <span className="h-3 w-3 animate-pulse rounded-full bg-cyan-500" />
        {label}
      </div>
    </div>
  )
}

export function EmptyPanel({ title, description, icon }: { title: string; description?: string; icon?: ReactNode }) {
  return (
    <div className="rounded-2xl border border-dashed border-slate-300 bg-white/65 p-8 text-center">
      {icon ? <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-2xl bg-cyan-50 text-cyan-600">{icon}</div> : null}
      <div className="font-bold text-slate-900">{title}</div>
      {description ? <p className="mt-1 text-sm text-slate-500">{description}</p> : null}
    </div>
  )
}

export function StatusBadge({ value }: { value?: string | boolean }) {
  const text = typeof value === 'boolean' ? (value ? 'Activo' : 'Inactivo') : value || 'Sin estado'
  const normalized = text.toString().toUpperCase()
  const style =
    normalized.includes('ACTIVA') || normalized.includes('ACTIVO') || normalized.includes('APROBADO') || normalized.includes('VALIDADO')
      ? 'border-emerald-200 bg-emerald-50 text-emerald-700'
      : normalized.includes('PENDIENTE')
        ? 'border-amber-200 bg-amber-50 text-amber-700'
        : normalized.includes('INACTIVO') || normalized.includes('ANULADO') || normalized.includes('VENCIDA') || normalized.includes('SUSPENDIDA')
          ? 'border-rose-200 bg-rose-50 text-rose-700'
          : 'border-slate-200 bg-slate-50 text-slate-600'

  return <span className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-bold ${style}`}>{text}</span>
}

export function TextField({
  label,
  value,
  onChange,
  type = 'text',
  placeholder,
  required = false,
  error,
}: {
  label: string
  value: string
  onChange: (value: string) => void
  type?: string
  placeholder?: string
  required?: boolean
  error?: string
}) {
  return (
    <label className="space-y-1.5 text-sm font-semibold text-slate-700">
      <span>{label}</span>
      <input className={`${inputClass} ${error ? 'border-rose-300 bg-rose-50/40 focus:border-rose-400 focus:ring-rose-100' : ''}`} type={type} value={value} placeholder={placeholder} required={required} onChange={(event) => onChange(event.target.value)} />
      {error ? <span className="block text-xs font-bold text-rose-600">{error}</span> : null}
    </label>
  )
}

export function SelectField<T extends string>({
  label,
  value,
  onChange,
  options,
}: {
  label: string
  value: T
  onChange: (value: T) => void
  options: Array<{ value: T; label: string }>
}) {
  return (
    <label className="space-y-1.5 text-sm font-semibold text-slate-700">
      <span>{label}</span>
      <select className={selectClass} value={value} onChange={(event) => onChange(event.target.value as T)}>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </label>
  )
}

export function Modal({ title, children, onClose }: { title: string; children: ReactNode; onClose: () => void }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/45 p-4 backdrop-blur-sm">
      <div className="max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-3xl border border-white/70 bg-white p-6 shadow-2xl">
        <div className="mb-5 flex items-center justify-between gap-3">
          <h2 className="text-xl font-black text-slate-950">{title}</h2>
          <button type="button" className="rounded-xl border border-slate-200 px-3 py-1.5 text-sm font-semibold text-slate-600 hover:bg-slate-50" onClick={onClose}>
            Cerrar
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}

export function formatMoney(value?: number) {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(Number(value ?? 0))
}

export function formatDate(value?: string) {
  if (!value) return '-'
  return new Intl.DateTimeFormat('es-CO', { dateStyle: 'medium' }).format(new Date(value))
}

export function isUserActive(user: { activo?: boolean; active?: boolean; status?: string }) {
  if (user.activo === false || user.active === false) return false
  if (user.status?.toUpperCase() === 'INACTIVE') return false
  return true
}

export function normalizeList<T>(data: unknown, keys: string[] = []): T[] {
  if (Array.isArray(data)) return data as T[]
  if (data && typeof data === 'object') {
    const record = data as Record<string, unknown>
    for (const key of keys) {
      if (Array.isArray(record[key])) return record[key] as T[]
    }
    if (Array.isArray(record.content)) return record.content as T[]
    if (Array.isArray(record.data)) return record.data as T[]
  }
  return []
}

export function showSuccess(message: string) {
  void Swal.fire({ icon: 'success', title: message, timer: 1500, showConfirmButton: false })
}

export function showError(message: string) {
  void Swal.fire({ icon: 'error', title: 'No se pudo completar', text: message })
}

export async function confirmAction(title: string, text: string) {
  const result = await Swal.fire({
    icon: 'warning',
    title,
    text,
    showCancelButton: true,
    confirmButtonText: 'Confirmar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#7c3aed',
  })
  return result.isConfirmed
}
