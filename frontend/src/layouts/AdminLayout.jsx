import { useState } from "react";
import { NavLink, Outlet } from "react-router-dom";

const linkClass = ({ isActive }) => `bbm-admin-link${isActive ? " is-active" : ""}`;

export default function AdminLayout() {
  const [open, setOpen] = useState(false);
  const close = () => setOpen(false);

  return (
    <div className="bbm-admin">
      <button
        type="button"
        className="bbm-admin-toggle"
        onClick={() => setOpen((v) => !v)}
        aria-label="Menü"
      >
        ☰ Menü
      </button>

      <aside className={`bbm-admin-sidebar${open ? " is-open" : ""}`}>
        <div className="bbm-admin-brand">Admin Panel</div>
        <nav onClick={close}>
          <NavLink to="/admin" end className={linkClass}>Dashboard</NavLink>
          <NavLink to="/admin/urunler" className={linkClass}>Ürünler</NavLink>
          <NavLink to="/admin/kategoriler" className={linkClass}>Kategoriler</NavLink>
          <NavLink to="/admin/kullanicilar" className={linkClass}>Kullanıcılar</NavLink>
          <NavLink to="/admin/siparisler" className={linkClass}>Siparişler</NavLink>
        </nav>
      </aside>

      <main className="bbm-admin-content">
        <Outlet />
      </main>
    </div>
  );
}
