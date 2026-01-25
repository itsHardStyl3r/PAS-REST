import { useState, useEffect } from 'react';
import api from '../api/axios';

const UserDetails = ({ userId, onBack }) => {
  const [user, setUser] = useState(null);
  const [currentAllocations, setCurrentAllocations] = useState([]);
  const [pastAllocations, setPastAllocations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const userRes = await api.get(`/user/id/${userId}`);
        setUser(userRes.data);

        const currentRes = await api.get(`/allocations/user/${userId}/current`);
        const pastRes = await api.get(`/allocations/user/${userId}/past`);

        setCurrentAllocations(currentRes.data);
        setPastAllocations(pastRes.data);
      } catch (error) {
        console.error("Błąd podczas pobierania danych szczegółowych:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [userId]);

  if (loading) return (
    <div className="text-center mt-5">
      <div className="spinner-border text-info" role="status"></div>
      <p className="mt-3 text-secondary text-uppercase small fw-bold">Pobieranie szczegółów...</p>
    </div>
  );

  if (!user) return (
    <div className="alert alert-danger mt-5 mx-3">Nie znaleziono użytkownika o podanym ID.</div>
  );

  return (
    <div className="container-fluid mt-4">
      <div className="d-flex align-items-center mb-4 ps-2">
        <button 
          onClick={onBack} 
          className="btn btn-outline-secondary btn-sm me-4 px-3"
        >
          <i className="bi bi-arrow-left me-2"></i>Powrót
        </button>
        <h2 className="text-primary fw-bold mb-0">
          <i className="bi bi-person-badge-fill me-3"></i>
          Profil użytkownika: {user.username}
        </h2>
      </div>

      <div className="row g-4">
        <div className="col-12">
          <div className="card border-0 shadow-lg">
            <div className="card-header bg-dark text-info fw-bold py-3">
              <i className="bi bi-info-circle me-2"></i>Informacje o koncie
            </div>
            <div className="card-body p-4">
              <div className="row">
                <div className="col-md-4 border-end border-secondary">
                  <label className="text-secondary small fw-bold text-uppercase d-block mb-1">Pełne ID Systemowe</label>
                  <code className="fs-6">{user.id}</code>
                </div>
                <div className="col-md-4 border-end border-secondary ps-4">
                  <label className="text-secondary small fw-bold text-uppercase d-block mb-1">Uprawnienia / Rola</label>
                  <span className="badge bg-primary text-white px-3 py-2 fs-6 text-uppercase">{user.role}</span>
                </div>
                <div className="col-md-4 ps-4">
                  <label className="text-secondary small fw-bold text-uppercase d-block mb-1">Status konta</label>
                  <span className={`badge rounded-pill px-4 py-2 fs-6 ${user.active ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}`}>
                    {user.active ? 'Aktywny' : 'Nieaktywny'}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="col-lg-6">
          <div className="card border-0 shadow-lg h-100">
            <div className="card-header bg-dark text-white fw-bold py-3">
              <i className="bi bi-box-seam me-2 text-success"></i>Bieżące alokacje
            </div>
            <div className="card-body p-0">
              {currentAllocations.length > 0 ? (
                <div className="table-responsive">
                  <table className="table table-dark table-hover mb-0">
                    <thead className="table-primary">
                      <tr>
                        <th className="ps-4">Zasób (ID)</th>
                        <th>Data rozpoczęcia</th>
                      </tr>
                    </thead>
                    <tbody>
                      {currentAllocations.map((alt) => (
                        <tr key={alt.id} className="border-secondary">
                          <td className="ps-4">
                            <code className="text-warning">{alt.resourceId}</code>
                          </td>
                          <td className="small text-secondary fw-bold">
                            {alt.startTime ? new Date(alt.startTime).toLocaleString('pl-PL') : '---'}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="p-5 text-center text-secondary italic border-top border-secondary">
                  Brak aktywnych wypożyczeń.
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="col-lg-6">
          <div className="card border-0 shadow-lg h-100">
            <div className="card-header bg-dark text-white fw-bold py-3">
              <i className="bi bi-clock-history me-2 text-secondary"></i>Historia alokacji
            </div>
            <div className="card-body p-0">
              {pastAllocations.length > 0 ? (
                <div className="table-responsive">
                  <table className="table table-dark table-hover mb-0 text-nowrap">
                    <thead className="table-primary">
                      <tr>
                        <th className="ps-4">Zasób (ID)</th>
                        <th>Okres wypożyczenia</th>
                      </tr>
                    </thead>
                    <tbody>
                      {pastAllocations.map((alt) => (
                        <tr key={alt.id} className="border-secondary opacity-75">
                          <td className="ps-4"><code className="text-muted">{alt.resourceId}</code></td>
                          <td className="small text-muted">
                            {alt.startTime ? new Date(alt.startTime).toLocaleDateString('pl-PL') : '---'} 
                            {' — '} 
                            {alt.endTime && alt.endTime !== "<unset>" ? new Date(alt.endTime).toLocaleDateString('pl-PL') : '---'}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div className="p-5 text-center text-secondary italic border-top border-secondary">
                  Historia jest pusta.
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserDetails;