import { useEffect, useState } from 'react'
import api from '../api'

function StrategiesPage() {
  const [trailing, setTrailing] = useState<any[]>([])
  const [momentum, setMomentum] = useState<any[]>([])

  useEffect(() => {
    api
      .get('/api/strategies/trailing-stop/short')
      .then((res) => setTrailing(res.data))
      .catch(() => {})

    api
      .get('/api/strategies/momentum/short')
      .then((res) => setMomentum(res.data))
      .catch(() => {})
  }, [])

  return (
    <div className="main-container">
      <h2 className="text-2xl font-medium mb-4 text-center">Strategies</h2>
      <div>
        <h3 className="font-semibold">Trailing Stop</h3>
        <ul>
          {trailing.map((s) => (
            <li key={s.code}>
              {s.name} ({s.code})
            </li>
          ))}
        </ul>
        <h3 className="font-semibold mt-4">Momentum</h3>
        <ul>
          {momentum.map((s) => (
            <li key={s.code}>
              {s.name} ({s.code})
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}

export default StrategiesPage
