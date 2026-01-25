import { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext'; 

const Login = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  
  const { login } = useContext(AuthContext);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await login(username, password);
      onLoginSuccess(); 
    } catch (err) {
      console.error(err);
      setError('Nieprawidłowy login lub hasło.');
    }
  };

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 bg-dark">
      <div className="card shadow-lg border-0" style={{ maxWidth: '400px', width: '100%' }}>
        <div className="card-header bg-dark text-center py-4 border-secondary">
          <h3 className="text-info mb-0">
            <i className="bi bi-shield-lock-fill me-2"></i>
            System PAS
          </h3>
          <small className="text-secondary">Zaloguj się do panelu zarządzania</small>
        </div>
        <div className="card-body p-4 p-md-5">
          <form onSubmit={handleLogin}>
            <div className="mb-4">
              <label className="form-label small text-secondary fw-bold">Użytkownik</label>
              <div className="input-group">
                <span className="input-group-text bg-dark border-secondary text-secondary">
                  <i className="bi bi-person-fill"></i>
                </span>
                <input 
                  type="text" 
                  className="form-control bg-dark border-secondary text-light"
                  placeholder="Wpisz login" 
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
            </div>
            
            <div className="mb-4">
              <label className="form-label small text-secondary fw-bold">Hasło</label>
              <div className="input-group">
                <span className="input-group-text bg-dark border-secondary text-secondary">
                  <i className="bi bi-key-fill"></i>
                </span>
                <input 
                  type="password" 
                  className="form-control bg-dark border-secondary text-light"
                  placeholder="Wpisz hasło" 
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            {error && (
              <div className="alert alert-danger py-2 small text-center mb-4 border-0 bg-danger bg-opacity-10 text-danger">
                <i className="bi bi-exclamation-triangle-fill me-2"></i>
                {error}
              </div>
            )}

            <button type="submit" className="btn btn-info w-100 fw-bold py-2 shadow-sm text-uppercase">
              Zaloguj się <i className="bi bi-arrow-right-short ms-1"></i>
            </button>
          </form>
        </div>
        <div className="card-footer bg-transparent border-0 text-center pb-4">
          <p className="text-muted x-small mb-0">Aplikacja SPA</p>
        </div>
      </div>
    </div>
  );
};

export default Login;