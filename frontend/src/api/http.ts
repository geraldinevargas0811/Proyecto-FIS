import axios from 'axios'

const configuredBaseURL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'
const baseURL = configuredBaseURL.replace(/\/api\/?$/, '')

export const http = axios.create({
  baseURL,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})
