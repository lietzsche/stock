import { type FormEvent, useState } from 'react'

function LoginForm() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const res = await fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      })

      if (res.ok) {
        const data = await res.json()
        if (data && typeof data.token === 'string') {
          localStorage.setItem('token', data.token)
          setMessage('Login successful')
          setUsername('')
          setPassword('')
        } else {
          setMessage('Invalid response from server')
        }
      } else if (res.status === 401) {
        setMessage('Invalid credentials')
      } else {
        setMessage('Login failed')
      }
    } catch {
      setMessage('Network error: unable to connect')
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-2 max-w-xs">
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="Username"
        className="border rounded p-2"
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        className="border rounded p-2"
      />
      <button type="submit" className="bg-green-500 text-white rounded p-2">
        Login
      </button>
      {message && <p>{message}</p>}
    </form>
  )
}

export default LoginForm

