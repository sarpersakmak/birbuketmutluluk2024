import { NavLink, Outlet } from "react-router-dom";

const tabClass = ({ isActive }) => `bbm-profile-tab${isActive ? " is-active" : ""}`;

export default function ProfileLayout() {
  return (
    <section className="bbm-page">
      <h1>Hesabım</h1>
      <div className="bbm-profile">
        <nav className="bbm-profile-tabs">
          <NavLink to="/profile" end className={tabClass}>Profil Bilgilerim</NavLink>
          <NavLink to="/profile/adreslerim" className={tabClass}>Adreslerim</NavLink>
          <NavLink to="/profile/siparislerim" className={tabClass}>Siparişlerim</NavLink>
        </nav>
        <div className="bbm-profile-content">
          <Outlet />
        </div>
      </div>
    </section>
  );
}
