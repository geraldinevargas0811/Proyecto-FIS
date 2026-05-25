export type Role = 'ADMIN' | 'INSTRUCTOR' | 'CLIENTE'

export type AuthTokens = {
  accessToken: string
  refreshToken: string
}

// Ajusta si tu backend devuelve un shape distinto
export type AuthMe = {
  id?: number | string
  username?: string
  email?: string
  role: Role
}

export type LoginRequest = {
  username: string
  password: string
}

export type LoginResponse = AuthTokens & {
  me?: AuthMe
}

export type RefreshResponse = AuthTokens

export type LogoutResponse = {
  message?: string
}

