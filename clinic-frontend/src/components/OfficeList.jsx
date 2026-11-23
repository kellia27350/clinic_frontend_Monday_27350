import { useEffect, useState } from 'react'
import './OfficeList.css'

const API = 'http://localhost:1014/api/offices'

export default function OfficeList() {
  const [offices, setOffices]       = useState([])
  const [query, setQuery]           = useState('')
  const [loading, setLoading]       = useState(true)
  const [error, setError]           = useState(null)
  const [deletingId, setDeletingId] = useState(null)
  const [toast, setToast]           = useState(null)
  const [selected, setSelected]     = useState(null)

  function loadOffices() {
    setLoading(true)
    fetch(`${API}/all`)
      .then(res => {
        if (!res.ok) throw new Error('Could not reach the server.')
        return res.json()
      })
      .then(data => {
        setOffices(data)
        setError(null)
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { loadOffices() }, [])

  const visible = offices.filter(o => {
    const term = query.toLowerCase().trim()
    if (!term) return true
    return (
      o.name.toLowerCase().includes(term) ||
      String(o.officeNumber).includes(term)
    )
  })

  function handleRowClick(office) {
    setSelected(office)
  }

  function closeDetail() {
    setSelected(null)
  }

  function handleDelete(id, name) {
    const confirmed = window.confirm(`Are you sure you want to delete office "${name}"?`)
    if (!confirmed) return

    setDeletingId(id)
    fetch(`${API}/delete/${id}`, { method: 'DELETE' })
      .then(res => res.text())
      .then(msg => {
        setOffices(prev => prev.filter(o => o.id !== id))
        if (selected?.id === id) setSelected(null)
        showToast(msg, 'success')
      })
      .catch(() => showToast('Delete failed. Please try again.', 'error'))
      .finally(() => setDeletingId(null))
  }

  function showToast(msg, type) {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3000)
  }

  return (
    <div className="page-container">

      {toast && (
        <div className={`toast toast--${toast.type}`}>{toast.msg}</div>
      )}

      {/* ── Detail modal ── */}
      {selected && (
        <div className="modal-overlay" onClick={closeDetail}>
          <div className="modal-card" onClick={e => e.stopPropagation()}>
            <button className="modal-close" onClick={closeDetail}>✕</button>
            <h3 className="modal-title">Office Details</h3>
            <div className="detail-grid">
              <span className="detail-label">Name</span>
              <span className="detail-value">{selected.name}</span>

              <span className="detail-label">Office No.</span>
              <span className="detail-value">{selected.officeNumber}</span>

              <span className="detail-label">ID</span>
              <span className="detail-value detail-id">{selected.id}</span>
            </div>
            <div className="modal-actions">
              <button
                className="btn btn--delete"
                onClick={() => handleDelete(selected.id, selected.name)}
                disabled={deletingId === selected.id}
              >
                {deletingId === selected.id ? 'Deleting...' : 'Delete'}
              </button>
              <button className="btn btn--cancel" onClick={closeDetail}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="card">
        <h2 className="page-title">Available Offices</h2>

        <div className="search-row">
          <label htmlFor="search" className="search-label">Search:</label>
          <input
            id="search"
            type="text"
            className="search-input"
            placeholder="Search by name or office number..."
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
        </div>

        {loading && <p className="state-msg">Loading offices...</p>}

        {error && (
          <p className="state-msg state-msg--error">{error}</p>
        )}

        {!loading && !error && (
          <div className="table-wrapper">
            <table className="offices-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Name</th>
                  <th>Office No.</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {visible.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="empty-row">
                      No offices match your search.
                    </td>
                  </tr>
                ) : (
                  visible.map((office, index) => (
                    <tr
                      key={office.id}
                      className="clickable-row"
                      onClick={() => handleRowClick(office)}
                    >
                      <td>{index + 1}</td>
                      <td>{office.name}</td>
                      <td>{office.officeNumber}</td>
                      <td onClick={e => e.stopPropagation()}>
                        <div className="btn-group">
                          <button
                            className="btn btn--update"
                            disabled
                            title="Update — coming soon"
                          >
                            Update
                          </button>
                          <button
                            className="btn btn--delete"
                            onClick={() => handleDelete(office.id, office.name)}
                            disabled={deletingId === office.id}
                          >
                            {deletingId === office.id ? 'Deleting...' : 'Delete'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}

        <p className="record-count">
          {!loading && !error && `${visible.length} record${visible.length !== 1 ? 's' : ''} found`}
        </p>
      </div>
    </div>
  )
}
