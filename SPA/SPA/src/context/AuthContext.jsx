import { createContext, useState } from 'react';
import api from '../api/axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(JSON.parse(sessionStorage.getItem('user')));

  const login = async (username, password) => {
    const response = await api.post('/auth/login', { username, password });
    const userData = response.data;
    
    sessionStorage.setItem('token', userData.token);
    sessionStorage.setItem('refreshToken', userData.refreshToken);
    sessionStorage.setItem('user', JSON.stringify(userData));
    
    setUser(userData);
  };

  const logout = () => {
    sessionStorage.clear();
    setUser(null);
    delete api.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};