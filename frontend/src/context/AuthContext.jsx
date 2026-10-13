import React, { createContext, useContext, useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('securevault_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('securevault_token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      if (token) {
        try {
          const response = await axiosInstance.get('/user/me');
          setUser(response.data);
          localStorage.setItem('securevault_user', JSON.stringify(response.data));
        } catch (error) {
          logout();
        }
      }
      setLoading(false);
    };
    checkAuth();
  }, [token]);

  const login = async (email, password) => {
    const response = await axiosInstance.post('/auth/login', { email, password });
    if (response.data.mfaRequired) {
      return response.data;
    }
    const { token: jwtToken, user: userData } = response.data;
    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('securevault_token', jwtToken);
    localStorage.setItem('securevault_user', JSON.stringify(userData));
    return response.data;
  };

  const verifyMfa = async (mfaToken, code) => {
    const response = await axiosInstance.post('/auth/verify-mfa', { mfaToken, code });
    const { token: jwtToken, user: userData } = response.data;
    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('securevault_token', jwtToken);
    localStorage.setItem('securevault_user', JSON.stringify(userData));
    return response.data;
  };

  const oauthLogin = async (provider, oauthId, email, name) => {
    const response = await axiosInstance.post('/auth/oauth2/login', { provider, oauthId, email, name });
    const { token: jwtToken, user: userData } = response.data;
    setToken(jwtToken);
    setUser(userData);
    localStorage.setItem('securevault_token', jwtToken);
    localStorage.setItem('securevault_user', JSON.stringify(userData));
    return response.data;
  };

  const register = async (name, email, password, confirmPassword) => {
    const response = await axiosInstance.post('/auth/register', {
      name,
      email,
      password,
      confirmPassword,
    });
    return response.data;
  };

  const logout = async () => {
    try {
      await axiosInstance.post('/auth/logout');
    } catch (e) {
      // ignore logout network errors
    }
    setToken(null);
    setUser(null);
    localStorage.removeItem('securevault_token');
    localStorage.removeItem('securevault_user');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, verifyMfa, oauthLogin, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
