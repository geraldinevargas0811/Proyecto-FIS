import { create } from 'zustand'
import { authApi } from '../api/authApi'
import type { AuthMe, AuthTokens, LoginRequest, Role } from '../types/auth'
import { clearMe, clearTokens, getMe, getRefreshToken, getAccessToken, setMe, setTokens } from '../utils/storage'

type AuthStatus = 'idle' | 'loading' | 'authenticated' | 'unauthenticated'

type AuthState = {
  status: AuthStatus
  error: string | null

  accessToken: string | null
  refreshToken: string | null

  me: AuthMe | null
  role: Role | null

  // actions
  hydrateFromStorage: () => void
  loadMe: () => Promise<void>
  login: (payload: LoginRequest) => Promise<void>
  refreshTokens: () => Promise<void>
  logout: () => Promise<void>
  clearError: () => void
}

export const useAuthStore = create<AuthState>((set) => ({

  status: 'idle',
  error: null,

  accessToken: getAccessToken(),
  refreshToken: getRefreshToken(),

  me: getMe<AuthMe>(),
  role: getMe<AuthMe>()?.role ?? null,

  hydrateFromStorage: () => {
    const accessToken = getAccessToken()
    const refreshToken = getRefreshToken()
    const me = getMe<AuthMe>()

    set({
      accessToken,
      refreshToken,
      me,
      role: me?.role ?? null,
      status: accessToken ? 'authenticated' : 'unauthenticated',
    })
  },

  loadMe: async () => {
    const accessToken = getAccessToken()
    if (!accessToken) {
      set({ status: 'unauthenticated', me: null, role: null })
      return
    }

    set({ status: 'loading', error: null })
    try {
      const resp = await authApi.me()
      const me = (resp.data as any) as AuthMe
      setMe(me)
      set({
        me,
        role: me?.role ?? null,
        status: 'authenticated',
      })
    } catch {
      clearTokens()
      clearMe()
      set({ status: 'unauthenticated', me: null, role: null })
    }
  },

  login: async (payload) => {
    set({ status: 'loading', error: null })
    try {
      const resp = await authApi.login(payload)
      const data = resp.data

      const tokens: AuthTokens = {
        accessToken: data.accessToken,
        refreshToken: data.refreshToken,
      }

      setTokens(tokens)

      const me = data.me ?? null
      if (me) setMe(me)

      set({
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
        me,
        role: (me as any)?.role ?? null,
        status: 'authenticated',
      })
    } catch (e: any) {
      set({ status: 'unauthenticated', error: e?.response?.data?.message ?? 'Login fallido' })
      throw e
    }
  },

  refreshTokens: async () => {
    const refreshToken = getRefreshToken()
    if (!refreshToken) return

    try {
      const resp = await authApi.refresh({ refreshToken })
      const tokens = resp.data
      setTokens(tokens)
      set({ accessToken: tokens.accessToken, refreshToken: tokens.refreshToken, status: 'authenticated' })
    } catch {
      clearTokens()
      clearMe()
      set({ status: 'unauthenticated', me: null, role: null })
    }
  },

  logout: async () => {
    const refreshToken = getRefreshToken()
    try {
      await authApi.logout(refreshToken ? { refreshToken } : {})
    } catch {
      // ignore
    } finally {
      clearTokens()
      clearMe()
      set({
        status: 'unauthenticated',
        error: null,
        accessToken: null,
        refreshToken: null,
        me: null,
        role: null,
      })
    }
  },

  clearError: () => set({ error: null }),
}))

