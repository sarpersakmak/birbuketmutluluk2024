import { Outlet } from "react-router-dom";

import BotanicalOverlay from "../components/BotanicalOverlay";
import Footer from "../components/Footer";
import Navbar from "../components/Navbar";

export default function MainLayout() {
  return (
    <div className="bbm-shell">
      <BotanicalOverlay />
      <Navbar />
      <main className="bbm-main">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
