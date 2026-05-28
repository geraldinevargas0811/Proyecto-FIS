import { http } from './http'

export type AdminDashboardStats = {
  totalClientes: number
  totalInstructores: number
  totalPagosPendientes: number
  totalMembresiasActivas: number
}

export type AdminUser = {
  id: number
  nombre: string
  apellido: string
  correo: string
  documento?: string
  telefono?: string
  activo?: boolean
  active?: boolean
}

export type AdminPayment = {
  id: number
  monto: number
  estado: string
  referencia?: string
  fechaPago?: string
}

export type CreateClientPayload = {
  nombre: string
  apellido: string
  correo: string
  contrasena: string
  documento: string
  telefono?: string
  peso?: number
  altura?: number
  objetivo: 'AUMENTAR_MASA' | 'DEFINICION' | 'PERDER_PESO'
  frecuenciaEntrenamiento?: string
  genero?: 'MALE' | 'FEMALE' | 'OTHER'
}

export type CreateInstructorPayload = {
  nombre: string
  apellido: string
  correo: string
  contrasena: string
  documento: string
  telefono?: string
  especialidad?: string
  certificaciones?: string
  anosExperiencia?: number
  salario?: number
  horarioTrabajo?: string
  contractType?: 'FULL_TIME' | 'PART_TIME' | 'TEMPORARY'
}

export const adminApi = {
  dashboard: () => http.get<AdminDashboardStats>('/api/admin/dashboard'),
  clientes: () => http.get<AdminUser[]>('/api/admin/clientes'),
  instructores: () => http.get<AdminUser[]>('/api/admin/instructores'),
  pagosPendientes: () => http.get<AdminPayment[]>('/api/admin/pagos/pendientes'),
  crearCliente: (payload: CreateClientPayload) => http.post('/api/admin/clientes', payload),
  crearInstructor: (payload: CreateInstructorPayload) => http.post('/api/admin/instructores', payload),
  desactivarCliente: (id: number) => http.delete(`/api/admin/clientes/${id}`),
  desactivarInstructor: (id: number) => http.delete(`/api/admin/instructores/${id}`),
}
