import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";

import { fetchAdminOrders } from "../../services/adminService";
import { formatDate, formatPrice } from "../../utils/format";
import { ORDER_STATUS_LABELS, statusLabel } from "../../utils/orderStatus";

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(true);

  const load = (s) => {
    setLoading(true);
    fetchAdminOrders(s || undefined)
      .then((res) => setOrders(res.data))
      .catch(() => toast.error("Siparişler yüklenemedi."))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load(status);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [status]);

  return (
    <section>
      <h1>Sipariş Yönetimi</h1>

      <div className="bbm-filters">
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">Tüm Durumlar</option>
          {Object.entries(ORDER_STATUS_LABELS).map(([k, v]) => (
            <option key={k} value={k}>{v}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <p className="bbm-status">Yükleniyor...</p>
      ) : (
        <table className="bbm-table">
          <thead>
            <tr>
              <th>Sipariş No</th>
              <th>Müşteri</th>
              <th>Tarih</th>
              <th>Tutar</th>
              <th>Durum</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.id}>
                <td>{o.orderNumber}</td>
                <td>{o.customerName}</td>
                <td>{formatDate(o.createdAt)}</td>
                <td>{formatPrice(o.totalPrice)}</td>
                <td>{statusLabel(o.status)}</td>
                <td>
                  <Link to={`/admin/siparisler/${o.id}`} className="bbm-linkbtn">Detay</Link>
                </td>
              </tr>
            ))}
            {orders.length === 0 && (
              <tr><td colSpan={6} className="bbm-muted">Sipariş bulunamadı.</td></tr>
            )}
          </tbody>
        </table>
      )}
    </section>
  );
}
