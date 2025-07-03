import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || ''
})

api.interceptors.request.use((config) => {
  if (config.url && (config.url.includes('/api/auth/login') || config.url.includes('/api/users/register'))) {
    return config
  }
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${token}`
    }
  }
  return config
})

export default api
