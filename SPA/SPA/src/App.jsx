import React, { useState, useContext, useEffect } from 'react';
import { AuthContext } from './context/AuthContext.jsx';
import UserList from './components/UserList.jsx';
import Login from './components/Login.jsx';
import UserDetails from './components/UserDetails.jsx';
import AllocationManager from './components/AllocationManager.jsx';
import ChangePassword from './components/ChangePassword.jsx';

function App() {
  const { user, logout } = useContext(AuthContext);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [activeTab, setActiveTab] = useState('users');

  const isLoggedIn = !!user;

  useEffect(() => {
    if (user?.role === 'RESOURCE_MANAGER' || user?.role === 'CLIENT') {
      setActiveTab('allocations');
    }
  }, [user]);

  if (!isLoggedIn) {
    return <Login onLoginSuccess={() => console.log("Zalogowano!")} />;
  }

  return (
    <div className="min-vh-100">
      <nav className="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm py-2">
        <div className="container">
          <span className="navbar-brand fw-bold text-info">
            <i className="bi bi-shield-lock-fill me-2"></i>System PAS
          </span>
          
          <div className="collapse navbar-collapse">
            <div className="navbar-nav me-auto">
              {user.role === 'ADMIN' && (
                <button 
                  className={`nav-link btn btn-link border-0 me-2 ${activeTab === 'users' ? 'active fw-bold' : ''}`}
                  onClick={() => { setActiveTab('users'); setSelectedUserId(null); }}
                >
                  Użytkownicy
                </button>
              )}
              
              {(user.role === 'ADMIN' || user.role === 'RESOURCE_MANAGER' || user.role === 'CLIENT') && (
                <button 
                  className={`nav-link btn btn-link border-0 me-2 ${activeTab === 'allocations' ? 'active fw-bold' : ''}`}
                  onClick={() => { setActiveTab('allocations'); setSelectedUserId(null); }}
                >
                  {user.role === 'CLIENT' ? 'Moje Alokacje' : 'Alokacje'}
                </button>
              )}

              <button 
                className={`nav-link btn btn-link border-0 ${activeTab === 'changePassword' ? 'active fw-bold' : ''}`}
                onClick={() => { setActiveTab('changePassword'); setSelectedUserId(null); }}
              >
                Hasło
              </button>
            </div>

            <div className="d-flex align-items-center">
              <div className="text-light me-3 small">
                <i className="bi bi-person-circle me-1"></i>
                {user.username} <span className="badge bg-secondary ms-1">{user.role}</span>
              </div>
              <button className="btn btn-danger btn-sm px-3" onClick={logout}>
                Wyloguj
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="container py-4">
        <div className="row justify-content-center">
          <div className="col-12 col-xl-10">
            {activeTab === 'changePassword' ? (
              <ChangePassword />
            ) : 

            activeTab === 'users' && user.role === 'ADMIN' ? (
              selectedUserId ? (
                <UserDetails userId={selectedUserId} onBack={() => setSelectedUserId(null)} />
              ) : (
                <UserList onSelectUser={(id) => setSelectedUserId(id)} />
              )
            ) : 

            activeTab === 'allocations' && (user.role === 'ADMIN' || user.role === 'RESOURCE_MANAGER' || user.role === 'CLIENT') ? (
              <AllocationManager />
            ) : (
              <div className="text-center mt-5 card p-5 shadow-sm bg-dark">
                <i className="bi bi-exclamation-triangle text-warning display-1 mb-3"></i>
                <p className="h4">Nie masz uprawnień do tego widoku.</p>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;