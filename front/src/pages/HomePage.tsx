import { Link } from 'react-router-dom'

function HomePage() {
  return (
    <div className="p-4">
      <h1 className="text-2xl mb-4">Welcome</h1>
      <div className="flex gap-4">
        <Link to="/login" className="text-blue-500 underline">Login</Link>
        <Link to="/register" className="text-blue-500 underline">Register</Link>
      </div>
    </div>
  )
}

export default HomePage
