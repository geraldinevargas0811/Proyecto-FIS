import type { AxiosError } from 'axios'

import { authApi } from './authApi'
import { http } from './http'
import { getAccessToken, getRefreshToken, setMe, setTokens, clearMe, clearTokens } from '../utils/storage'
import type { AuthMe, AuthTokens } from '../types/auth'

// (type removed - suprime TS6196/uso innecesario)




let isRefreshing = false
let refreshSubscribers: Array<(tokens: AuthTokens) => void> = []


function subscribeTokenRefresh(cb: (tokens: AuthTokens) => void) {
  refreshSubscribers.push(cb)
}

function notifySubscribers(tokens: AuthTokens) {
  refreshSubscribers.forEach((cb) => cb(tokens))
  refreshSubscribers = []
}

function attachAuthorization(config: any): any {
  const accessToken = getAccessToken()
  if (!accessToken) return config

  // Axios tipa headers como AxiosRequestHeaders (con AxiosHeaders interno). Para evitar errores TS,
  // usamos any aquí: funcionalmente Axios lo acepta.
  config.headers = { ...(config.headers ?? {}), Authorization: `Bearer ${accessToken}` } as any
  return config
}




export function setupInterceptors(onSessionExpired: () => void) {
  http.interceptors.request.use((config) => attachAuthorization(config))

  http.interceptors.response.use(
    (res) => res,
    async (err: AxiosError) => {
      const originalRequest = err.config
      if (!originalRequest) return Promise.reject(err)

      // Si no es 401, propagamos
      if (err.response?.status !== 401) return Promise.reject(err)

      // Evitar bucle de refresh
      const alreadyRetried = (originalRequest as any)._retry
      if (alreadyRetried) {
        clearTokens()
        clearMe()
        onSessionExpired()
        return Promise.reject(err)
      }

      ;(originalRequest as any)._retry = true

      const refreshToken = getRefreshToken()
      if (!refreshToken) {
        clearTokens()
        clearMe()
        onSessionExpired()
        return Promise.reject(err)
      }

  if (isRefreshing) {
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh((tokens) => {
            ;(originalRequest as any).headers = {
              ...((originalRequest as any).headers ?? {}),
              Authorization: `Bearer ${tokens.accessToken}`,
            }
            resolve(http(originalRequest as any) as any)

          })
          setTimeout(() => reject(err), 10_000)
        })
      }


      isRefreshing = true
      try {

        const resp = await authApi.refresh({ refreshToken })
        const tokens = resp.data
        setTokens(tokens)

        // opcional: actualizar me si aplica (pero no forzamos)
        notifySubscribers(tokens)

        // Reintentar request original
        ;(originalRequest as any).headers = {
          ...((originalRequest as any).headers ?? {}),
          Authorization: `Bearer ${tokens.accessToken}`,
        }
        return http(originalRequest as any)

      } catch (refreshErr) {
        clearTokens()
        clearMe()
        onSessionExpired()
        return Promise.reject(refreshErr)
      } finally {
        isRefreshing = false
      }
    },
  )
}

export async function fetchAndStoreMe(): Promise<AuthMe | null> {
  const resp = await authApi.me()
  const me = (resp.data as any) ?? null
  if (me) setMe(me)
  return me as AuthMe | null
}

