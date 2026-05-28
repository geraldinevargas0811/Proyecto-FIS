import type { AxiosError, InternalAxiosRequestConfig } from 'axios'

import { authApi } from './authApi'
import { http } from './http'
import {
  getAccessToken,
  getRefreshToken,
  setMe,
  setTokens,
  clearMe,
  clearTokens,
} from '../utils/storage'
import type { AuthMe, AuthTokens, LoginResponse } from '../types/auth'

let isRefreshing = false
let requestInterceptorId: number | null = null
let responseInterceptorId: number | null = null
let refreshSubscribers: Array<(tokens: AuthTokens) => void> = []
let refreshFailures: Array<(error: unknown) => void> = []

function isAuthEndpoint(url?: string) {
  return Boolean(
    url?.includes('/api/auth/login')
      || url?.includes('/api/auth/refresh')
      || url?.includes('/api/auth/logout'),
  )
}

function subscribeTokenRefresh(resolve: (tokens: AuthTokens) => void, reject: (error: unknown) => void) {
  refreshSubscribers.push(resolve)
  refreshFailures.push(reject)
}

function notifySubscribers(tokens: AuthTokens) {
  refreshSubscribers.forEach((cb) => cb(tokens))
  refreshSubscribers = []
  refreshFailures = []
}

function notifyRefreshFailure(error: unknown) {
  refreshFailures.forEach((cb) => cb(error))
  refreshSubscribers = []
  refreshFailures = []
}

function attachAuthorization(config: InternalAxiosRequestConfig): InternalAxiosRequestConfig {
  const accessToken = getAccessToken()
  if (!accessToken || isAuthEndpoint(config.url)) return config

  config.headers.set('Authorization', `Bearer ${accessToken}`)
  return config
}

function persistAuthPayload(data: LoginResponse): AuthTokens {
  const tokens: AuthTokens = {
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
  }
  setTokens(tokens)

  const me = data.me ?? data.usuario ?? null
  if (me) setMe(me)

  return tokens
}

export function setupInterceptors(onSessionExpired: () => void) {
  if (requestInterceptorId !== null) {
    http.interceptors.request.eject(requestInterceptorId)
  }
  if (responseInterceptorId !== null) {
    http.interceptors.response.eject(responseInterceptorId)
  }

  requestInterceptorId = http.interceptors.request.use((config) => attachAuthorization(config))

  responseInterceptorId = http.interceptors.response.use(
    (res) => res,
    async (err: AxiosError) => {
      const originalRequest = err.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined

      if (!originalRequest || err.response?.status !== 401 || isAuthEndpoint(originalRequest.url)) {
        return Promise.reject(err)
      }

      if (originalRequest._retry) {
        clearTokens()
        clearMe()
        onSessionExpired()
        return Promise.reject(err)
      }

      originalRequest._retry = true

      const refreshToken = getRefreshToken()
      if (!refreshToken) {
        clearTokens()
        clearMe()
        onSessionExpired()
        return Promise.reject(err)
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh(
            (tokens) => {
              originalRequest.headers.set('Authorization', `Bearer ${tokens.accessToken}`)
              resolve(http(originalRequest))
            },
            reject,
          )
        })
      }

      isRefreshing = true
      try {
        const resp = await authApi.refresh({ refreshToken })
        const tokens = persistAuthPayload(resp.data)

        notifySubscribers(tokens)
        originalRequest.headers.set('Authorization', `Bearer ${tokens.accessToken}`)

        return http(originalRequest)
      } catch (refreshErr) {
        notifyRefreshFailure(refreshErr)
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
  const me = (resp.data as AuthMe | null) ?? null
  if (me) setMe(me)
  return me
}
