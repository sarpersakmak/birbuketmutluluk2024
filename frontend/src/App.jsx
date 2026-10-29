import { Link, Navigate, Route, Routes, useParams } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import AdminRoute from "./components/AdminRoute";
import ProtectedRoute from "./components/ProtectedRoute";
import { AuthProvider } from "./context/AuthContext";
import { CartProvider } from "./context/CartContext";
import AdminLayout from "./layouts/AdminLayout";
import MainLayout from "./layouts/MainLayout";
import ProfileLayout from "./layouts/ProfileLayout";
import AboutPage from "./pages/AboutPage";
import ContactPage from "./pages/ContactPage";
import HomePage from "./pages/HomePage";
import CartPage from "./pages/cart/CartPage";
import CheckoutPage from "./pages/checkout/CheckoutPage";
import OrderDetailPage from "./pages/orders/OrderDetailPage";
import OrdersPage from "./pages/orders/OrdersPage";
import AdminOrderDetailPage from "./pages/admin/AdminOrderDetailPage";
import AdminOrdersPage from "./pages/admin/AdminOrdersPage";
import AdminUsersPage from "./pages/admin/AdminUsersPage";
import AddressBookTab from "./pages/profile/AddressBookTab";
import ProfileInfoTab from "./pages/profile/ProfileInfoTab";
import AdminCategoriesPage from "./pages/admin/AdminCategoriesPage";
import AdminDashboardPage from "./pages/admin/AdminDashboardPage";
import AdminProductsPage from "./pages/admin/AdminProductsPage";
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import ProductDetailPage from "./pages/products/ProductDetailPage";
import ProductListPage from "./pages/products/ProductListPage";

function OldOrderDetailRedirect() {
  const { id } = useParams();
  return <Navigate to={`/profile/siparislerim/${id}`} replace />;
}

function NotFoundPage() {
  return (
    <section className="bbm-page">
      <h1>404</h1>
      <p className="bbm-muted">Aradığın sayfa bulunamadı.</p>
      <Link to="/" className="bbm-btn">Ana sayfaya dön</Link>
    </section>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Routes>
        <Route element={<MainLayout />}>
          {/* Public */}
          <Route path="/" element={<HomePage />} />
          <Route path="/urunler" element={<ProductListPage />} />
          <Route path="/urunler/:slug" element={<ProductDetailPage />} />
          <Route path="/hakkimizda" element={<AboutPage />} />
          <Route path="/iletisim" element={<ContactPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Giriş yapmış kullanıcı */}
          <Route element={<ProtectedRoute />}>
            <Route path="/cart" element={<CartPage />} />
            <Route path="/checkout" element={<CheckoutPage />} />

            {/* Kullanıcı Paneli (sekmeli) */}
            <Route path="/profile" element={<ProfileLayout />}>
              <Route index element={<ProfileInfoTab />} />
              <Route path="adreslerim" element={<AddressBookTab />} />
              <Route path="siparislerim" element={<OrdersPage />} />
              <Route path="siparislerim/:id" element={<OrderDetailPage />} />
            </Route>

            {/* Eski route'lar -> panele yönlendir */}
            <Route path="/siparislerim" element={<Navigate to="/profile/siparislerim" replace />} />
            <Route path="/siparislerim/:id" element={<OldOrderDetailRedirect />} />
          </Route>

          {/* Yalnızca ADMIN */}
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminLayout />}>
              <Route index element={<AdminDashboardPage />} />
              <Route path="urunler" element={<AdminProductsPage />} />
              <Route path="kategoriler" element={<AdminCategoriesPage />} />
              <Route path="kullanicilar" element={<AdminUsersPage />} />
              <Route path="siparisler" element={<AdminOrdersPage />} />
              <Route path="siparisler/:id" element={<AdminOrderDetailPage />} />
            </Route>
          </Route>

          <Route path="*" element={<NotFoundPage />} />
          </Route>
        </Routes>

        <ToastContainer position="top-right" autoClose={3000} newestOnTop />
      </CartProvider>
    </AuthProvider>
  );
}
