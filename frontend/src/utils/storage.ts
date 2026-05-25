const KEYS = {
  accessToken: 'gym_auth_access_token',
  refreshToken: 'gym_auth_refresh_token',
  me: 'gym_auth_me',
} as const

export function getAccessToken(): string | null {
  return localStorage.getItem(KEYS.accessToken)
}

export function getRefreshToken(): string | null {
  return localStorage.getItem(KEYS.refreshToken)
}

export function setTokens(tokens: { accessToken: string; refreshToken: string }) {
  localStorage.setItem(KEYS.accessToken, tokens.accessToken)
  localStorage.setItem(KEYS.refreshToken, tokens.refreshToken)
}

export function clearTokens() {
  localStorage.removeItem(KEYS.accessToken)
  localStorage.removeItem(KEYS.refreshToken)
}

export function getMe<T = unknown>(): T | null {
  const raw = localStorage.getItem(KEYS.me)
  if (!raw) return null
  try {
    return JSON.parse(raw) as T
  } catch {
    return null
  }
}

export function setMe(me: unknown) {
  localStorage.setItem(KEYS.me, JSON.stringify(me))
}

export function clearMe() {
  localStorage.removeItem(KEYS.me)
}

