import { http } from './http'
import type { LoginRequest, LoginResponse, RefreshResponse, LogoutResponse } from '../types/auth'


export const authApi = {
  login: (payload: LoginRequest) => http.post<LoginResponse>('/api/auth/login', payload),
  refresh: (payload: { refreshToken: string }) =>
    http.post<RefreshResponse>('/api/auth/refresh', payload),
  logout: (payload?: { refreshToken?: string }) =>
    http.post<LogoutResponse>('/api/auth/logout', payload ?? {}),
  me: () => http.get('/api/auth/me'),
}

