import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'

interface StockInfo {
  code: string
  price: number
}

function StockPage() {
  const { code } = useParams<{ code: string }>()
  const [data, setData] = useState<StockInfo | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchStock = async () => {
      try {
        const res = await fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/stocks/${code}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token') || ''}`,
          },
        })
        if (!res.ok) throw new Error('Request failed')
        const json = await res.json()
        setData(json)
      } catch {
        setError('Failed to load stock data')
      } finally {
        setLoading(false)
      }
    }
    fetchStock()
  }, [code])

  return (
    <div className="main-container text-center">
      {loading && <p>Loading...</p>}
      {error && <p>{error}</p>}
      {data && (
        <>
          <h2 className="text-2xl font-medium mb-4">{data.code}</h2>
          <p className="text-xl">Price: {data.price}</p>
        </>
      )}
    </div>
  )
}

export default StockPage
