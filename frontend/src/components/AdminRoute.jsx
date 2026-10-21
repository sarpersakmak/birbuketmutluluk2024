import { Navigate, Outlet, useLocation } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

/**
 * Yalnızca ADMIN erişimi (bölüm 39). Yetki backend'de de kontrol edilir (Kural 15);
 * bu guard yalnızca kullanıcı deneyimi içindir.
 */
export default function AdminRoute() {
  const { isAuthenticated, isAdmin, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div className="bbm-status">Yükleniyor...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
