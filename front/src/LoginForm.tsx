import { type FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from './api'

function LoginForm() {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const res = await api.post('/api/auth/login', { username, password })

      const data = res.data as any
      if (data && typeof data.token === 'string') {
        localStorage.setItem('token', data.token)
        setUsername('')
        setPassword('')
        navigate('/')
      } else {
        setMessage('Invalid response from server')
      }
    } catch (err: any) {
      if (err.response?.status === 401) {
        setMessage('Invalid credentials')
      } else {
        setMessage('Login failed')
      }
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3">
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="Username"
        className="form-field"
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        className="form-field"
      />
      <button type="submit" className="button-primary">Login</button>
      {message && <p>{message}</p>}
    </form>
  )
}

export default LoginForm

