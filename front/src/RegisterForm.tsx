import { type FormEvent, useState } from 'react'

function RegisterForm() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [message, setMessage] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (password !== confirmPassword) {
      setMessage('Passwords do not match')
      return
    }
    try {
      const res = await fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, confirmPassword, email, phone }),
      })

      if (res.ok) {
        setMessage('Registered successfully')
        setUsername('')
        setPassword('')
        setConfirmPassword('')
        setEmail('')
        setPhone('')
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
      <input
        type="password"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        placeholder="Confirm Password"
        className="border rounded p-2"
      />
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        className="border rounded p-2"
      />
      <input
        type="tel"
        value={phone}
        onChange={(e) => setPhone(e.target.value)}
        placeholder="Phone"
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
