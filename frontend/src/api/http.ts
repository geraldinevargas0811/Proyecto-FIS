import axios from 'axios'

const baseURL = '' // Ajusta si tu backend no vive en el mismo host. Ej: import.meta.env.VITE_API_BASE_URL

export const http = axios.create({
  baseURL,
})

