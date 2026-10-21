import { Outlet } from "react-router-dom";

import Footer from "../components/Footer";
import Navbar from "../components/Navbar";

export default function MainLayout() {
  return (
    <div className="bbm-shell">
      <Navbar />
      <main className="bbm-main">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
