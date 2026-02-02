import { useState, useEffect, useMemo } from 'react';
import api from '../api/axios';

const UserList = ({ onSelectUser }) => {
    const [users, setUsers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(false);
    const [newUser, setNewUser] = useState({ username: '', password: '', name: '' });
    const [message, setMessage] = useState({ text: '', isError: false });
    
    const fetchUsers = async () => {
        setLoading(true);
        try {
            const response = await api.get('/users');
            setUsers(response.data);
        } catch (error) {
            console.error("Błąd podczas pobierania użytkowników:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const filteredUsers = useMemo(() => {
        return users.filter((user) =>
            user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.name?.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [users, searchTerm]);

    const handleRegister = async (e) => {
        e.preventDefault();

        const userExists = users.some(
            (u) => u.username.toLowerCase() === newUser.username.toLowerCase()
        );

        if (userExists) {
            setMessage({ text: 'Taki użytkownik już istnieje na liście.', isError: true });
            return;
        }

        if (newUser.username.length < 3) {
            setMessage({ text: 'Username musi mieć minimum 3 znaki.', isError: true });
            return;
        }
        if (newUser.password.length < 6) {
            setMessage({ text: 'Hasło musi mieć minimum 6 znaków.', isError: true });
            return;
        }
        if (newUser.name.length < 3) {
            setMessage({ text: 'Imię i Nazwisko muszą mieć minimum 3 znaki.', isError: true });
            return;
        }

        if (!window.confirm(`Czy na pewno chcesz zarejestrować użytkownika "${newUser.username}"?`)) return;

        setMessage({ text: '', isError: false });
        try {
            await api.post('/auth/register', newUser);
            setMessage({ text: 'Użytkownik zarejestrowany pomyślnie!', isError: false });
            setNewUser({ username: '', password: '', name: '' });
            fetchUsers();
        } catch (error) {
            const errorMsg = error.response?.data?.message || 'Błąd rejestracji.';
            setMessage({ text: errorMsg, isError: true });
        }
    };

    const handleToggleActive = async (user) => {
        const actionLabel = user.active ? 'dezaktywować' : 'aktywować';

        if (!window.confirm(`Czy na pewno chcesz ${actionLabel} użytkownika ${user.username}?`)) return;

        const action = user.active ? 'deactivate' : 'activate';
        try {
            await api.patch(`/user/id/${user.id}/${action}`);
            fetchUsers();
        } catch (error) {
            alert("Błąd podczas zmiany statusu.");
        }
    };

    const handleChangeRole = async (id, currentUsername, newRole) => {
        if (!window.confirm(`Czy na pewno chcesz zmienić rolę użytkownika ${currentUsername} na ${newRole}?`)) {
            fetchUsers();
            return;
        }

        try {
            await api.put(`/user/${id}/role?role=${newRole}`);
            fetchUsers();
        } catch (error) {
            alert("Nie udało się zmienić roli.");
            fetchUsers();
        }
    };

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="text-primary"><i className="bi bi-people-fill me-2"></i>Zarządzanie Użytkownikami</h2>
            </div>

            <div className="card shadow-sm mb-4 border-0">
                <div className="card-header bg-dark text-white fw-bold">
                    <i className="bi bi-person-plus-fill me-2"></i>Dodaj nowego użytkownika
                </div>
                <div className="card-body">
                    <form onSubmit={handleRegister} className="row g-3">
                        <div className="col-md-3">
                            <input
                                className="form-control"
                                placeholder="Username"
                                value={newUser.username}
                                onChange={(e) => setNewUser({...newUser, username: e.target.value})}
                                required
                            />
                        </div>
                        <div className="col-md-3">
                            <input
                                type="password"
                                className="form-control"
                                placeholder="Hasło"
                                value={newUser.password}
                                onChange={(e) => setNewUser({...newUser, password: e.target.value})}
                                required
                            />
                        </div>
                        <div className="col-md-4">
                            <input
                                className="form-control"
                                placeholder="Imię i Nazwisko"
                                value={newUser.name}
                                onChange={(e) => setNewUser({...newUser, name: e.target.value})}
                                required
                            />
                        </div>
                        <div className="col-md-2">
                            <button type="submit" className="btn btn-success w-100 fw-bold">
                                Zarejestruj
                            </button>
                        </div>
                    </form>
                    {message.text && (
                        <div className={`alert mt-3 mb-0 py-2 ${message.isError ? 'alert-danger' : 'alert-success'}`}>
                            {message.text}
                        </div>
                    )}
                </div>
            </div>

            <div className="row mb-3">
                <div className="col-md-4">
                    <div className="input-group">
                        <span className="input-group-text"><i className="bi bi-search"></i></span>
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Szukaj użytkownika..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
            </div>

            <div className="card shadow-sm border-0">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-primary">
                        <tr>
                            <th>Username</th>
                            <th>Rola</th>
                            <th>Status</th>
                            <th className="text-end">Akcje</th>
                        </tr>
                        </thead>
                        <tbody>
                        {loading && users.length === 0 ? (
                            <tr><td colSpan="4" className="text-center py-4">Ładowanie...</td></tr>
                        ) : (
                            filteredUsers.map((user) => (
                                <tr key={user.id}>
                                    <td className="fw-bold">{user.username}</td>
                                    <td>
                                        <select
                                            className="form-select form-select-sm w-auto"
                                            value={user.role}
                                            onChange={(e) => handleChangeRole(user.id, user.username, e.target.value)}
                                        >
                                            <option value="CLIENT">CLIENT</option>
                                            <option value="RESOURCE_MANAGER">RESOURCE_MANAGER</option>
                                            <option value="ADMIN">ADMIN</option>
                                        </select>
                                    </td>
                                    <td>
                      <span className={`badge rounded-pill ${user.active ? 'bg-success' : 'bg-danger'}`}>
                        {user.active ? 'Aktywny' : 'Nieaktywny'}
                      </span>
                                    </td>
                                    <td className="text-end">
                                        <button
                                            className={`btn btn-sm me-2 ${user.active ? 'btn-outline-danger' : 'btn-outline-success'}`}
                                            onClick={() => handleToggleActive(user)}
                                        >
                                            {user.active ? <i className="bi bi-person-x"></i> : <i className="bi bi-person-check"></i>}
                                            {user.active ? ' Dezaktywuj' : ' Aktywuj'}
                                        </button>
                                        <button
                                            className="btn btn-sm btn-primary"
                                            onClick={() => onSelectUser(user.id)}
                                        >
                                            <i className="bi bi-eye"></i> Podgląd
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        {!loading && filteredUsers.length === 0 && (
                            <tr><td colSpan="4" className="text-center py-4 text-muted">Brak wyników dla "{searchTerm}"</td></tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default UserList;