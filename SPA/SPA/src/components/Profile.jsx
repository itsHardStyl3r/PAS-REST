import { useState, useEffect, useContext } from 'react';
import api from '../api/axios';
import { AuthContext } from '../context/AuthContext';

const Profile = () => {
    const { user: authUser } = useContext(AuthContext);
    const [name, setName] = useState('');
    const [etag, setEtag] = useState('');
    const [message, setMessage] = useState({ text: '', isError: false });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const getProfile = async () => {
            setLoading(true);
            try {
                const response = await api.get(`/user/id/${authUser.id}`);
                setName(response.data.name || '');
                
                const serverEtag = response.headers['etag'];
                if (serverEtag) {
                    setEtag(serverEtag.replace(/"/g, ''));
                }
            } catch (err) {
                console.error("Błąd pobierania profilu:", err);
                setMessage({ text: 'Nie udało się pobrać danych profilu.', isError: true });
            } finally {
                setLoading(false);
            }
        };
        getProfile();
    }, [authUser.id]);

    const handleUpdate = async (e) => {
        e.preventDefault();
        setMessage({ text: '', isError: false });
        
        if (!window.confirm("Czy na pewno chcesz zmienić swoje dane osobowe?")) return;

        try {
            await api.patch(`/user/id/${authUser.id}/rename`, 
                { name }, 
                { 
                    headers: { 
                        'If-Match': etag 
                    } 
                }
            );
            setMessage({ text: 'Imię i nazwisko zostały zaktualizowane pomyślnie!', isError: false });
        } catch (err) {
            console.error("Błąd aktualizacji:", err);
            
            if (err.response?.status === 412) {
                setMessage({ text: 'Błąd: Sygnatura danych jest nieprawidłowa (Precondition Failed).', isError: true });
            } else if (err.response?.status === 428) {
                setMessage({ text: 'Błąd: Serwer wymaga nagłówka If-Match (Precondition Required).', isError: true });
            } else {
                setMessage({ text: err.response?.data?.message || 'Wystąpił błąd podczas aktualizacji.', isError: true });
            }
        }
    };

    //KOD pod pokazanie ze fake ID nie zadziała lololololol
    /*
    const handleUpdate = async (e) => {
    e.preventDefault();
    
    const fakeId = "60c72b2f9b1e8a3f3c8e4b1c"; 

    try {
        await api.patch(`/user/id/${fakeId}/rename`,
            { name }, 
            { headers: { 'If-Match': etag } }
        );
        setMessage({ text: 'Dziala????!', isError: false });
    } catch (err) {
        console.log("Status błędu:", err.response?.status);
        setMessage({ text: `Blokada JWS: ${err.response?.status} - Wykryto zmiane klucza!`, isError: true });
    }
    };
    */

    if (loading) {
        return (
            <div className="text-center mt-5">
                <div className="spinner-border text-info" role="status"></div>
                <p className="mt-2 text-secondary">Ładowanie danych profilu...</p>
            </div>
        );
    }

    return (
        <div className="d-flex justify-content-center mt-5">
            <div className="card shadow-lg border-0 bg-dark" style={{ maxWidth: '500px', width: '100%' }}>
                <div className="card-header border-secondary py-4 text-center">
                    <h3 className="text-info fw-bold mb-0">
                        <i className="bi bi-person-bounding-box me-3"></i>
                        MÓJ PROFIL
                    </h3>
                    <small className="text-secondary text-uppercase tracking-widest">
                        Zarządzanie danymi podstawowymi
                    </small>
                </div>
                
                <div className="card-body p-4 p-md-5">
                    <form onSubmit={handleUpdate}>
                        <div className="mb-4">
                            <label className="form-label small text-secondary fw-bold text-uppercase">
                                Identyfikator (Klucz obiektu)
                            </label>
                            <div className="input-group">
                                <span className="input-group-text bg-dark border-secondary text-secondary">
                                    <i className="bi bi-fingerprint"></i>
                                </span>
                                <input 
                                    className="form-control bg-dark border-secondary text-muted font-monospace" 
                                    value={authUser.username} 
                                    disabled 
                                />
                            </div>
                            <div className="form-text text-muted small">
                                Klucz identyfikacyjny jest chroniony sygnaturą JWS.
                            </div>
                        </div>

                        <div className="mb-4">
                            <label className="form-label small text-secondary fw-bold text-uppercase">
                                Imię i Nazwisko
                            </label>
                            <div className="input-group">
                                <span className="input-group-text bg-dark border-secondary text-info">
                                    <i className="bi bi-person-vcard"></i>
                                </span>
                                <input 
                                    className="form-control bg-dark border-secondary text-light" 
                                    placeholder="Wpisz nowe imię i nazwisko"
                                    value={name} 
                                    onChange={(e) => setName(e.target.value)} 
                                    required 
                                />
                            </div>
                        </div>

                        <div className="mb-4 d-flex align-items-center">
                            <div className={`badge ${etag ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} px-3 py-2 w-100`}>
                                <i className={`bi ${etag ? 'bi-patch-check-fill' : 'bi-patch-exclamation-fill'} me-2`}></i>
                                {etag ? 'Sygnatura JWS załadowana' : 'Brak sygnatury JWS'}
                            </div>
                        </div>

                        <button type="submit" className="btn btn-info w-100 fw-bold py-2 shadow-sm">
                            <i className="bi bi-save2 me-2"></i>
                            ZAKTUALIZUJ DANE
                        </button>
                    </form>

                    {message.text && (
                        <div className={`alert mt-4 py-2 small text-center border-0 bg-opacity-10 ${message.isError ? 'bg-danger text-danger' : 'bg-success text-success'}`}>
                            <i className={`bi ${message.isError ? 'bi-exclamation-octagon-fill' : 'bi-check-circle-fill'} me-2`}></i>
                            {message.text}
                        </div>
                    )}
                </div>
                
                <div className="card-footer bg-transparent border-secondary text-center py-3">
                    <p className="text-muted x-small mb-0">
                        Ochrona integralności klucza zapewniona przez standard JWS
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Profile;