import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

const navLinkClass = ({ isActive }) => `bbm-navlink${isActive ? " is-active" : ""}`;

export default function Navbar() {
  const { isAuthenticated, isAdmin, user, logout } = useAuth();
  const { cartCount } = useCart();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const close = () => setOpen(false);

  const handleLogout = () => {
    logout();
    close();
    toast.info("Çıkış yapıldı.");
    navigate("/login");
  };

  const links = (
    <>
      <NavLink to="/" end className={navLinkClass} onClick={close}>Ana Sayfa</NavLink>
      <NavLink to="/urunler" className={navLinkClass} onClick={close}>Ürünler</NavLink>
      {isAuthenticated && (
        <NavLink to="/cart" className={navLinkClass} onClick={close}>
          Sepet
          {cartCount > 0 && <span className="bbm-cart-badge">{cartCount}</span>}
        </NavLink>
      )}
      {isAuthenticated && <NavLink to="/profile" className={navLinkClass} onClick={close}>Hesabım</NavLink>}
      {isAdmin && <NavLink to="/admin" className={navLinkClass} onClick={close}>Admin</NavLink>}
      {!isAuthenticated && <NavLink to="/login" className={navLinkClass} onClick={close}>Giriş</NavLink>}
      {!isAuthenticated && (
        <Link to="/register" className="bbm-btn bbm-btn-sm" onClick={close}>Kayıt Ol</Link>
      )}
      {isAuthenticated && (
        <button type="button" className="bbm-linkbtn" onClick={handleLogout}>
          Çıkış{user?.firstName ? ` (${user.firstName})` : ""}
        </button>
      )}
    </>
  );

  return (
    <header className="bbm-navbar">
      <div className="bbm-navbar-inner">
        <Link to="/" className="bbm-brand" onClick={close}>
          <span className="bbm-brand-mark">❀</span> Bir Buket Mutluluk
        </Link>

        <nav className="bbm-nav-desktop">{links}</nav>

        <button
          type="button"
          className="bbm-nav-burger"
          aria-label="Menü"
          aria-expanded={open}
          onClick={() => setOpen((v) => !v)}
        >
          {open ? "✕" : "☰"}
        </button>
      </div>

      {open && (
        <>
          <div className="bbm-nav-backdrop" onClick={close} />
          <nav className="bbm-nav-drawer">{links}</nav>
        </>
      )}
    </header>
  );
}
