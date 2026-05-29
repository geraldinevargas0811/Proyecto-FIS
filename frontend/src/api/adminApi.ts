import { http } from './http'

export type AdminDashboardStats = {
  totalClientes: number
  totalInstructores: number
  totalPagosPendientes: number
  totalMembresiasActivas: number
  ingresosMes?: number
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
  status?: string
  fechaRegistro?: string
}

export type AdminClient = AdminUser & {
  peso?: number
  altura?: number
  imc?: number
  objetivo?: FitnessGoal
  frecuenciaEntrenamiento?: string
  genero?: string
  fechaNacimiento?: string
  quiereInstructor?: boolean
  membresia?: AdminMembership
  instructor?: AdminInstructor
}

export type AdminInstructor = AdminUser & {
  especialidad?: string
  certificaciones?: string
  anosExperiencia?: number
  disponible?: boolean
  salario?: number
  horarioTrabajo?: string
  contractType?: 'FULL_TIME' | 'PART_TIME' | 'TEMPORARY'
  cantidadClientes?: number
}

export type AdminPayment = {
  id: number
  monto: number
  estado: string
  referencia?: string
  fechaPago?: string
  metodoPago?: string
  observaciones?: string
  cliente?: AdminClient
  membresia?: AdminMembership
}

export type AdminPlan = {
  id: number
  nombre: string
  descripcion?: string
  duracionMeses: number
  precio: number
  tipo?: string
  beneficios?: string
  activo?: boolean
}

export type AdminMembership = {
  id: number
  cliente?: AdminClient
  plan?: AdminPlan
  fechaInicio?: string
  fechaVencimiento?: string
  estado: string
  activo?: boolean
  diasRestantes?: number
}

export type AdminMembershipsResponse = {
  activas?: AdminMembership[]
  pendientes?: AdminMembership[]
  vencidas?: AdminMembership[]
  suspendidas?: AdminMembership[]
}

export type FitnessGoal = 'PERDER_PESO' | 'AUMENTAR_MASA' | 'DEFINICION' | 'RECOMPOSICION' | 'MANTENIMIENTO'

export type CreateClientPayload = {
  nombre: string
  apellido: string
  correo: string
  contrasena: string
  documento: string
  telefono?: string
  peso?: number
  altura?: number
  objetivo: FitnessGoal
  frecuenciaEntrenamiento?: string
  genero?: 'MALE' | 'FEMALE' | 'OTHER'
  fechaNacimiento?: string
  quiereInstructor?: boolean
  instructorId?: number
  planId?: number
}

export type UpdateClientPayload = Partial<Omit<CreateClientPayload, 'contrasena'>> & {
  activo?: boolean
  active?: boolean
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

export type UpdateInstructorPayload = Partial<Omit<CreateInstructorPayload, 'contrasena'>> & {
  activo?: boolean
  disponible?: boolean
}

export type CreateMembershipPayload = { clienteId: number; planId: number }

export type CreatePaymentPayload = {
  clienteId: number
  membresiaId?: number
  monto?: number
  metodoPago: 'CASH' | 'TRANSFER' | 'CARD' | 'NEQUI' | 'DAVIPLATA'
  observaciones?: string
}

export type CreatePlanPayload = {
  nombre: string
  descripcion?: string
  duracionMeses: number
  precio: number
  tipo?: string
  beneficios?: string
}

export const adminApi = {
  dashboard: () => http.get<AdminDashboardStats>('/api/admin/dashboard'),
  clientes: () => http.get<AdminClient[]>('/api/admin/clientes'),
  cliente: (id: number) => http.get<AdminClient>(`/api/admin/clientes/${id}`),
  instructores: () => http.get<AdminInstructor[]>('/api/admin/instructores'),
  instructor: (id: number) => http.get<AdminInstructor>(`/api/admin/instructores/${id}`),
  pagos: () => http.get<AdminPayment[]>('/api/admin/pagos'),
  pagosPendientes: () => http.get<AdminPayment[]>('/api/admin/pagos/pendientes'),
  membresias: () => http.get<AdminMembershipsResponse>('/api/admin/membresias'),
  planes: () => http.get<AdminPlan[]>('/api/admin/planes'),
  crearCliente: (payload: CreateClientPayload) => http.post('/api/admin/clientes', payload),
  actualizarCliente: (id: number, payload: UpdateClientPayload) => http.put(`/api/admin/clientes/${id}`, payload),
  crearInstructor: (payload: CreateInstructorPayload) => http.post('/api/admin/instructores', payload),
  actualizarInstructor: (id: number, payload: UpdateInstructorPayload) => http.put(`/api/admin/instructores/${id}`, payload),
  crearMembresia: (payload: CreateMembershipPayload) => http.post('/api/admin/membresias', payload),
  renovarMembresia: (id: number) => http.post(`/api/admin/membresias/${id}/renovar`),
  crearPago: (payload: CreatePaymentPayload) => http.post('/api/admin/pagos', payload),
  crearPlan: (payload: CreatePlanPayload) => http.post('/api/admin/planes', payload),
  actualizarPlan: (id: number, payload: Partial<CreatePlanPayload> & { activo?: boolean }) => http.put(`/api/admin/planes/${id}`, payload),
  desactivarPlan: (id: number) => http.delete(`/api/admin/planes/${id}`),
  desactivarCliente: (id: number) => http.delete(`/api/admin/clientes/${id}`),
  desactivarInstructor: (id: number) => http.delete(`/api/admin/instructores/${id}`),
  validarPago: (id: number) => http.post(`/api/admin/pagos/${id}/validar`),
  anularPago: (id: number) => http.post(`/api/admin/pagos/${id}/anular`),
  suspenderMembresia: (id: number) => http.post(`/api/admin/membresias/${id}/suspender`),
}
