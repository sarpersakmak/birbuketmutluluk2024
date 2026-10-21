import { createContext, useCallback, useContext, useEffect, useState } from "react";

import { getMe, loginRequest, registerRequest } from "../services/authService";
import { ROLES, TOKEN_KEY } from "../utils/constants";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [loading, setLoading] = useState(true);

  // Sayfa yenilendiğinde token varsa kullanıcıyı /me ile geri yükle.
  useEffect(() => {
    let mounted = true;

    async function bootstrap() {
      const stored = localStorage.getItem(TOKEN_KEY);
      if (stored) {
        try {
          const res = await getMe();
          if (mounted) setUser(res.data);
        } catch {
          localStorage.removeItem(TOKEN_KEY);
          if (mounted) {
            setToken(null);
            setUser(null);
          }
        }
      }
      if (mounted) setLoading(false);
    }

    bootstrap();
    return () => {
      mounted = false;
    };
  }, []);

  const handleAuthSuccess = (data) => {
    localStorage.setItem(TOKEN_KEY, data.token);
    setToken(data.token);
    setUser(data.user);
  };

  const login = async (credentials) => {
    const res = await loginRequest(credentials);
    handleAuthSuccess(res.data);
    return res.data;
  };

  const register = async (payload) => {
    const res = await registerRequest(payload);
    handleAuthSuccess(res.data);
    return res.data;
  };

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    setToken(null);
    setUser(null);
  };

  // Profil güncellendikten sonra kullanıcı bilgisini (navbar adı vb.) tazeler.
  const refreshUser = useCallback(async () => {
    try {
      const res = await getMe();
      setUser(res.data);
    } catch {
      // yoksay
    }
  }, []);

  const value = {
    user,
    token,
    loading,
    isAuthenticated: Boolean(token) && Boolean(user),
    isAdmin: user?.role === ROLES.ADMIN,
    login,
    register,
    logout,
    refreshUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth, AuthProvider içinde kullanılmalıdır.");
  }
  return ctx;
}
