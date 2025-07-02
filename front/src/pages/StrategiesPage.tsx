import { useEffect, useState } from 'react'

function StrategiesPage() {
  const [trailing, setTrailing] = useState<any[]>([])
  const [momentum, setMomentum] = useState<any[]>([])

  useEffect(() => {
    fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/strategies/trailing-stop/short`)
      .then((res) => res.json())
      .then(setTrailing)
      .catch(() => {})

    fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/strategies/momentum/short`)
      .then((res) => res.json())
      .then(setMomentum)
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
