import { useState, useEffect, useContext } from 'react';
import api from '../api/axios';
import { AuthContext } from '../context/AuthContext';

const AllocationManager = () => {
  const { user } = useContext(AuthContext);
  const [allocations, setAllocations] = useState([]);
  const [resources, setResources] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const [formData, setFormData] = useState({ 
    userId: user.role === 'CLIENT' ? user.id : '', 
    resourceId: '' 
  });

  const isClient = user.role === 'CLIENT';

  const fetchData = async () => {
    setLoading(true);
    try {
      const [allocRes, resRes, userRes] = await Promise.all([
        isClient ? api.get(`/allocations/user/${user.id}/current`) : api.get('/allocations'),
        api.get('/resources'),
        !isClient ? api.get('/users') : Promise.resolve({ data: [] })
      ]);
      
      setAllocations(allocRes.data);
      setResources(resRes.data);
      setUsers(userRes.data);
    } catch (error) {
      console.error("Błąd pobierania danych:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const availableResources = resources.filter(res => {
    const isOccupied = allocations.some(alt => 
      alt.resourceId === res.id && (!alt.endTime || alt.endTime === "<unset>")
    );
    return !isOccupied;
  });

  const activeUsers = users.filter(u => u.active === true);

  const handleCreate = async (e) => {
    e.preventDefault();
    
    const confirmCreate = window.confirm(`Czy na pewno chcesz utworzyć nową alokację dla użytkownika ID: ${formData.userId} na zasób ID: ${formData.resourceId}?`);
    if (!confirmCreate) return;

    try {
      await api.post('/allocations', formData);
      setFormData({ ...formData, resourceId: '' });
      fetchData();
      alert("Alokacja utworzona pomyślnie!");
    } catch (error) {
      alert("Błąd: " + (error.response?.data?.message || "Nie udało się utworzyć alokacji"));
    }
  };

  const handleEnd = async (id) => {
    if (window.confirm("Czy na pewno chcesz zakończyć tę alokację?")) {
      try {
        await api.post(`/allocations/${id}/end`);
        fetchData();
      } catch (error) {
        alert("Błąd przy kończeniu alokacji.");
      }
    }
  };

return (
    <div className="container-fluid mt-3">
      <div className="d-flex align-items-center mb-4 ps-2">
        <h2 className="text-primary fw-bold display-6">
          <i className="bi bi-calendar-check-fill me-3"></i>
          {isClient ? 'Twoje Wypożyczenia' : 'Panel Zarządzania Alokacjami'}
        </h2>
      </div>

      <div className="row g-3">
        
        {!isClient && (
          <div className="col-lg-2">
            <div className="card border-0 shadow-lg h-100 bg-dark">
              <div className="card-header border-secondary text-info py-3 fw-bold small">
                <i className="bi bi-people-fill me-2"></i> AKTYWNI UŻYTKOWNICY
              </div>
              <div className="list-group list-group-flush" style={{ maxHeight: '80vh', overflowY: 'auto' }}>
                {activeUsers.map(u => (
                  <div key={u.id} className="list-group-item bg-transparent border-secondary py-3">
                    <span className="d-block text-light fw-bold mb-1">{u.username}</span>
                    <code className="d-block mb-3 small opacity-75">{u.id}</code>
                    <button 
                      onClick={() => setFormData({ ...formData, userId: u.id })} 
                      className="btn btn-sm btn-info w-100 fw-bold shadow-sm"
                    >
                      Wybierz użytkownika
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        <div className={isClient ? "col-lg-10" : "col-lg-8"}>
          <div className="card border-0 shadow-lg mb-4">
            <div className="card-body p-4">
              <form onSubmit={handleCreate} className="row g-3 align-items-end">
                <div className="col-md-5">
                  <label className="form-label text-secondary small fw-bold">Użytkownik (ID)</label>
                  <input 
                    className="form-control form-control-lg bg-dark border-secondary text-info font-monospace" 
                    value={formData.userId} 
                    readOnly 
                    placeholder="Wybierz z listy po lewej..."
                  />
                </div>
                <div className="col-md-5">
                  <label className="form-label text-secondary small fw-bold">Zasób (ID)</label>
                  <input 
                    className="form-control form-control-lg bg-dark border-secondary text-warning font-monospace" 
                    value={formData.resourceId} 
                    readOnly 
                    placeholder="Wybierz z listy po prawej..."
                  />
                </div>
                <div className="col-md-2">
                  <button type="submit" className="btn btn-lg btn-success w-100 fw-bold shadow">
                    DODAJ
                  </button>
                </div>
              </form>
            </div>
          </div>

          <div className="card border-0 shadow-lg">
            <div className="card-header bg-dark text-white py-3 fw-bold">
              <i className="bi bi-cpu-fill me-2 text-primary"></i> Rejestr alokacji
            </div>
            <div className="table-responsive">
              <table className="table table-dark table-hover align-middle mb-0">
                <thead className="table-primary">
                  <tr>
                    {!isClient && <th className="py-3 ps-4">Użytkownik (Pełne ID)</th>}
                    <th className="py-3">Zasób (Pełne ID)</th>
                    <th className="py-3">Początek</th>
                    {!isClient && <th className="py-3">Status / Koniec</th>}
                    <th className="py-3 text-end pe-4">Akcja</th>
                  </tr>
                </thead>
                <tbody>
                  {allocations
                    .filter(alt => isClient ? (!alt.endTime || alt.endTime === "<unset>") : true)
                    .map(alt => (
                    <tr key={alt.id} className="border-secondary text-nowrap">
                      {!isClient && <td className="ps-4"><code className="text-info">{alt.userId}</code></td>}
                      <td><code className="text-warning">{alt.resourceId}</code></td>
                      <td className="small text-secondary">
                        {alt.startTime ? new Date(alt.startTime).toLocaleString('pl-PL') : '---'}
                      </td>
                      {!isClient && (
                        <td>
                          {alt.endTime && alt.endTime !== "<unset>" 
                            ? <span className="small text-muted">{new Date(alt.endTime).toLocaleString()}</span>
                            : <span className="badge rounded-pill bg-success-subtle text-success px-3">AKTYWNA</span>
                          }
                        </td>
                      )}
                      <td className="text-end pe-4">
                        {(!alt.endTime || alt.endTime === "<unset>") && (
                          <button 
                            onClick={() => handleEnd(alt.id)} 
                            className="btn btn-sm btn-outline-danger px-4"
                          >
                            Zakończ
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="col-lg-2">
          <div className="card border-0 shadow-lg h-100 bg-dark">
            <div className="card-header bg-dark text-warning py-3 fw-bold small">
              <i className="bi bi-box-seam-fill me-2"></i> DOSTĘPNE ZASOBY
            </div>
            <div className="list-group list-group-flush" style={{ maxHeight: '80vh', overflowY: 'auto' }}>
              {availableResources.map(res => (
                <div key={res.id} className="list-group-item bg-transparent border-secondary py-3">
                  <span className="d-block text-light fw-bold mb-1">{res.name || "Bez nazwy"}</span>
                  <code className="d-block mb-3 small opacity-75 text-warning">{res.id}</code>
                  <button 
                    onClick={() => setFormData({ ...formData, resourceId: res.id })} 
                    className="btn btn-sm btn-primary w-100 fw-bold shadow-sm"
                  >
                    Wybierz zasób
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default AllocationManager;