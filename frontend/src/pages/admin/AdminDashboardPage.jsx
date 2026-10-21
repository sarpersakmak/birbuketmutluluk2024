import { useEffect, useState } from "react";

import { fetchDashboard } from "../../services/adminService";
import { formatPrice } from "../../utils/format";

export default function AdminDashboardPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    fetchDashboard()
      .then((res) => setStats(res.data))
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="bbm-status">Yükleniyor...</p>;
  if (error || !stats) return <p className="bbm-status bbm-error">İstatistikler yüklenemedi.</p>;

  const cards = [
    { label: "Toplam Kullanıcı", value: stats.totalUsers },
    { label: "Aktif Kullanıcı", value: stats.activeUsers },
    { label: "Toplam Ürün", value: stats.totalProducts },
    { label: "Toplam Sipariş", value: stats.totalOrders },
    { label: "Bekleyen Sipariş", value: stats.pendingOrders },
    { label: "Hazırlanan Sipariş", value: stats.preparingOrders },
    { label: "Teslim Edilen", value: stats.deliveredOrders },
    { label: "Toplam Ciro", value: formatPrice(stats.totalRevenue) },
  ];

  return (
    <section>
      <h1>Dashboard</h1>
      <div className="bbm-stat-grid">
        {cards.map((c) => (
          <div className="bbm-stat-card" key={c.label}>
            <span className="bbm-stat-value">{c.value}</span>
            <span className="bbm-stat-label">{c.label}</span>
          </div>
        ))}
      </div>
    </section>
  );
}
