import { useState } from 'react';
import api from '../api/axios';

const ChangePassword = () => {
  const [formData, setFormData] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [message, setMessage] = useState({ text: '', isError: false });

  const handleChange = async (e) => {
    e.preventDefault();
    setMessage({ text: '', isError: false });

    if (formData.newPassword.length < 8) {
      return setMessage({ 
        text: 'Nowe hasło musi mieć co najmniej 8 znaków.', 
        isError: true 
      });
    }

    if (formData.newPassword !== formData.confirmPassword) {
      return setMessage({ text: 'Nowe hasła nie są identyczne!', isError: true });
    }

    const confirmChange = window.confirm("Czy na pewno chcesz zmienić swoje hasło?");
    if (!confirmChange) return;

    try {
      await api.patch('/user/password', {
        oldPassword: formData.oldPassword,
        newPassword: formData.newPassword
      });
      setMessage({ text: 'Hasło zmienione pomyślnie!', isError: false });
      setFormData({ oldPassword: '', newPassword: '', confirmPassword: '' });
    } catch (error) {
      const errorMsg = error.response?.data?.message || 'Błąd podczas zmiany hasła.';
      setMessage({ text: errorMsg, isError: true });
    }
  };

  return (
    <div className="d-flex align-items-center justify-content-center mt-5">
      <div className="card shadow-lg border-0" style={{ maxWidth: '450px', width: '100%' }}>
        <div className="card-header bg-dark text-center py-4 border-secondary">
          <h3 className="text-info mb-0">
            <i className="bi bi-key-fill me-2"></i>
            Zmiana Hasła
          </h3>
          <small className="text-secondary">Zabezpiecz swoje konto nowym hasłem</small>
        </div>
        
        <div className="card-body p-4 p-md-5">
          <form onSubmit={handleChange}>
            <div className="mb-4">
              <label className="form-label small text-secondary fw-bold">Obecne hasło</label>
              <div className="input-group">
                <span className="input-group-text bg-dark border-secondary text-secondary">
                  <i className="bi bi-lock-fill"></i>
                </span>
                <input 
                  type="password" 
                  className="form-control bg-dark border-secondary text-light"
                  placeholder="Wpisz obecne hasło"
                  value={formData.oldPassword}
                  onChange={(e) => setFormData({...formData, oldPassword: e.target.value})}
                  required
                />
              </div>
            </div>

            <div className="mb-4">
              <label className="form-label small text-secondary fw-bold">Nowe hasło</label>
              <div className="input-group">
                <span className="input-group-text bg-dark border-secondary text-secondary">
                  <i className="bi bi-shield-lock"></i>
                </span>
                <input 
                  type="password" 
                  className="form-control bg-dark border-secondary text-light"
                  placeholder="Min. 8 znaków"
                  value={formData.newPassword}
                  onChange={(e) => setFormData({...formData, newPassword: e.target.value})}
                  required
                />
              </div>
            </div>

            <div className="mb-4">
              <label className="form-label small text-secondary fw-bold">Powtórz nowe hasło</label>
              <div className="input-group">
                <span className="input-group-text bg-dark border-secondary text-secondary">
                  <i className="bi bi-shield-check"></i>
                </span>
                <input 
                  type="password" 
                  className="form-control bg-dark border-secondary text-light"
                  placeholder="Powtórz nowe hasło"
                  value={formData.confirmPassword}
                  onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                  required
                />
              </div>
            </div>

            {message.text && (
              <div className={`alert py-2 small text-center mb-4 border-0 bg-opacity-10 ${message.isError ? 'bg-danger text-danger' : 'bg-success text-success'}`}>
                <i className={`bi ${message.isError ? 'bi-exclamation-triangle-fill' : 'bi-check-circle-fill'} me-2`}></i>
                {message.text}
              </div>
            )}

            <button type="submit" className="btn btn-info w-100 fw-bold py-2 shadow-sm text-uppercase">
              Zatwierdź zmianę <i className="bi bi-check2-all ms-1"></i>
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ChangePassword;