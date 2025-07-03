import { type FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from './api'

function RegisterForm() {
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [message, setMessage] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!username || !password || !confirmPassword || !email || !phone) {
      setMessage('All fields are required')
      return
    }
    if (password !== confirmPassword) {
      setMessage('Passwords do not match')
      return
    }
    try {
      await api.post('/api/users/register', {
        username,
        password,
        confirmPassword,
        email,
        phone
      })
      setUsername('')
      setPassword('')
      setConfirmPassword('')
      setEmail('')
      setPhone('')
      navigate('/login')
    } catch (err: any) {
      let errorMsg = 'Registration failed'
      if (err.response?.data && typeof err.response.data.message === 'string') {
        errorMsg = err.response.data.message
      }
      setMessage(errorMsg)
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
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        className="form-field"
        required
      />
      <input
        type="password"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        placeholder="Confirm Password"
        className="form-field"
        required
      />
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        className="form-field"
        required
      />
      <input
        type="tel"
        value={phone}
        onChange={(e) => setPhone(e.target.value)}
        placeholder="Phone"
        className="form-field"
        required
      />
      <button type="submit" className="button-primary">Register</button>
      {message && <p>{message}</p>}
    </form>
  )
}

export default RegisterForm
