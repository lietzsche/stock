import { FormEvent, useState } from 'react'

function RegisterForm() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      const res = await fetch('/api/users/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      })

      if (res.ok) {
        setMessage('Registered successfully')
        setUsername('')
        setPassword('')
      } else {
        let errorMsg = 'Registration failed'
        try {
          const data = await res.json()
          if (data && typeof data.message === 'string') {
            errorMsg = data.message
          }
        } catch {
          // ignore JSON parsing errors
        }
        setMessage(errorMsg)
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
      <button type="submit" className="bg-blue-500 text-white rounded p-2">
        Register
      </button>
      {message && <p>{message}</p>}
    </form>
  )
}

export default RegisterForm
