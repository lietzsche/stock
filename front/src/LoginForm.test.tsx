import { render, screen, fireEvent } from '@testing-library/react'
import LoginForm from './LoginForm'
import { vi } from 'vitest'

vi.mock('react-router-dom', () => ({
  useNavigate: () => vi.fn(),
}))

describe('LoginForm', () => {
  it('shows error message for invalid credentials', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false, status: 401 }) as any

    render(<LoginForm />)
    fireEvent.change(screen.getByPlaceholderText('Username'), { target: { value: 'u' } })
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'p' } })
    fireEvent.submit(screen.getByRole('button', { name: /login/i }))

    expect(await screen.findByText('Invalid credentials')).toBeInTheDocument()
  })
})
