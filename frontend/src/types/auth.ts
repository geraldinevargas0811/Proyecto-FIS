export type Role = 'ADMIN' | 'INSTRUCTOR' | 'CLIENTE'

export type AuthTokens = {
  accessToken: string
  refreshToken: string
}

// Ajusta si tu backend devuelve un shape distinto
export type AuthMe = {
  id: number
  nombre?: string
  apellido?: string
  nombreCompleto?: string
  correo: string
  rol?: Role
  role: Role
  activo?: boolean
  status?: string
}


export type LoginRequest = {
  correo: string
  contrasena: string
}


export type LoginResponse = AuthTokens & {
  me?: AuthMe
  usuario?: AuthMe
}

export type RefreshResponse = LoginResponse

export type LogoutResponse = {
  message?: string
}

