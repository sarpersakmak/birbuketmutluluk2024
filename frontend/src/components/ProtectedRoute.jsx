import { Navigate, Outlet, useLocation } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

/**
 * Giriş yapmamış kullanıcıyı /login'e yönlendirir (bölüm 39).
 * Yönlendirmeden önce gelmek istediği sayfayı state ile taşır.
 */
export default function ProtectedRoute() {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="bbm-status">Yükleniyor...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
