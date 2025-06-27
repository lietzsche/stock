import { render, screen, fireEvent } from '@testing-library/react'
import RegisterForm from './RegisterForm'
import { vi } from 'vitest'

vi.mock('react-router-dom', () => ({
  useNavigate: () => vi.fn(),
}))

describe('RegisterForm', () => {
  it('requires all fields', async () => {
    render(<RegisterForm />)
    fireEvent.submit(screen.getByRole('button', { name: /register/i }))

    expect(await screen.findByText('All fields are required')).toBeInTheDocument()
  })

  it('validates password confirmation', async () => {
    render(<RegisterForm />)

    fireEvent.change(screen.getByPlaceholderText('Username'), { target: { value: 'u' } })
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'p1' } })
    fireEvent.change(screen.getByPlaceholderText('Confirm Password'), { target: { value: 'p2' } })
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'test@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('Phone'), { target: { value: '123' } })

    fireEvent.submit(screen.getByRole('button', { name: /register/i }))

    expect(await screen.findByText('Passwords do not match')).toBeInTheDocument()
  })
})
